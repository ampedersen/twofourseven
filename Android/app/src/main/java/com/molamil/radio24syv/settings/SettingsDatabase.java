package com.molamil.radio24syv.settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.molamil.radio24syv.settings.model.PodcastInfo;
import com.molamil.radio24syv.settings.model.ProgramInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jens on 24/09/15.
 */
public class SettingsDatabase extends SQLiteOpenHelper {

    private static final String TABLE_LIBRARY = "library";
    private static final String TABLE_PROGRAM = "program";
    private static final String TABLE_PODCAST = "podcast";
    private static final String TABLE_PICTURE = "picture";
    private static final String TABLE_CATEGORY = "category";

    private static final String KEY_PODCAST_ID = "podcast_id";
    private static final String KEY_DOWNLOAD_ID = "download_id";
    private static final String KEY_PROGRAM_ID = "program_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_TOPIC = "topic";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DATE = "date";
    private static final String KEY_AUDIO_URL = "audio_url";

    private static final String SQL_CREATE_TABLE_LIBRAY = "CREATE TABLE " + TABLE_LIBRARY + "(" + KEY_PODCAST_ID + " INTEGER PRIMARY KEY, "
            + KEY_DOWNLOAD_ID + " BIGINT" + ")"; // TODO table index download_id

    private static final String SQL_CREATE_TABLE_PROGRAM = "CREATE TABLE " + TABLE_PROGRAM + "(" + KEY_PROGRAM_ID + " INTEGER PRIMARY KEY, "
            + KEY_NAME + " TEXT, " + KEY_TOPIC + " TEXT, " + KEY_DESCRIPTION + " TEXT, " + KEY_IMAGE_URL + " TEXT " + ")";

    private static final String SQL_CREATE_TABLE_PODCAST = "CREATE TABLE " + TABLE_PODCAST + "(" + KEY_PODCAST_ID + " INTEGER PRIMARY KEY, "
            + KEY_PROGRAM_ID + " INTEGER, " // TODO table index program_id
            + KEY_TITLE + " TEXT, " + KEY_DESCRIPTION + " TEXT, " + KEY_AUDIO_URL + " TEXT, " + KEY_DATE + " TEXT " + ")"; // "DATE" type is not supported by Cursor so have to use "TEXT" for KEY_DATE, yay

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Settings.db";

    public SettingsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade existing table to new format if table version is upgraded in a later app version.
        // For now, discard the data and start over.
        dropTables(db);
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        execSQL(db, SQL_CREATE_TABLE_LIBRAY);
        execSQL(db, SQL_CREATE_TABLE_PROGRAM);
        execSQL(db, SQL_CREATE_TABLE_PODCAST);
    }

    private void dropTables(SQLiteDatabase db) {
        execSQL(db, "DROP TABLE IF EXISTS " + TABLE_LIBRARY);
        execSQL(db, "DROP TABLE IF EXISTS " + TABLE_PROGRAM);
        execSQL(db, "DROP TABLE IF EXISTS " + TABLE_PODCAST);
    }

    private static void execSQL(SQLiteDatabase db, String query) {
        Log.d("JJJ", query);
        db.execSQL(query);
    }

    public void createTables() {
        createTables(getWritableDatabase());
    }

    public void dropTables() {
        dropTables(getWritableDatabase());
    }

    public void addLibraryIds(int podcastId, long downloadId) {
        ContentValues values = new ContentValues();
        values.put(KEY_PODCAST_ID, podcastId);
        values.put(KEY_DOWNLOAD_ID, downloadId);
        getWritableDatabase().replace(TABLE_LIBRARY, null, values);
    }

    public long getLibraryDownloadId(int podcastId) {
        // Better performance but unreadable:
        //String query = String.format(Locale.US, "SELECT %s FROM %s WHERE %s = %d", KEY_DOWNLOAD_ID, TABLE_LIBRARY,KEY_PODCAST_ID, podcastId );
        String query = "SELECT " + KEY_DOWNLOAD_ID + " FROM " + TABLE_LIBRARY + " WHERE " + KEY_PODCAST_ID + " = " + podcastId;
        Log.d("JJJ", query);

        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            long downloadId = c.getLong(c.getColumnIndex(KEY_DOWNLOAD_ID));
            c.close();
            Log.d("JJJ", "downloadId " + downloadId);
            return downloadId;
        } else {
            Log.d("JJJ", "downloadId unknown");
            return Settings.DOWNLOAD_ID_UNKNOWN;
        }
    }

    public int getLibraryPodcastId(long downloadId) {
        String query = "SELECT " + KEY_PODCAST_ID + " FROM " + TABLE_LIBRARY + " WHERE " + KEY_DOWNLOAD_ID + " = " + downloadId;
        Log.d("JJJ", query);

        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            int podcastId = c.getInt(c.getColumnIndex(KEY_PODCAST_ID));
            c.close();
            Log.d("JJJ", "podcastId " + podcastId);
            return podcastId;
        }
        Log.d("JJJ", "podcastId unknown");
        return Settings.PODCAST_ID_UNKNOWN;
    }

    public void removeLibraryIds(int podcastId) {
        deleteRow(TABLE_LIBRARY, KEY_PODCAST_ID + " = " + podcastId);
    }

    public void addProgram(ProgramInfo program) {
        ContentValues values = new ContentValues();
        values.put(KEY_PROGRAM_ID, program.getProgramId());
        values.put(KEY_NAME, program.getName());
        values.put(KEY_TOPIC, program.getTopic());
        values.put(KEY_DESCRIPTION, program.getDescription());
        values.put(KEY_IMAGE_URL, program.getImageUrl());
        getWritableDatabase().replace(TABLE_PROGRAM, null, values);
    }

    public ProgramInfo getProgram(int programId) {
        String query = "SELECT * FROM " + TABLE_PROGRAM + " WHERE " + KEY_PROGRAM_ID + " = " + programId;
        Log.d("JJJ", query);

        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            ProgramInfo program = new ProgramInfo();
            program.setProgramId(c.getInt(c.getColumnIndex(KEY_PROGRAM_ID)));
            program.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            program.setTopic(c.getString(c.getColumnIndex(KEY_TOPIC)));
            program.setDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
            program.setImageUrl(c.getString(c.getColumnIndex(KEY_IMAGE_URL)));
            c.close();
            Log.d("JJJ", "program " + program.getName() + " id " + program.getProgramId());
            return program;
        }
        Log.d("JJJ", "programId not found " + programId);
        return new ProgramInfo();
    }

    public List<ProgramInfo> getPrograms() {
        String query = "SELECT * FROM " + TABLE_PROGRAM + " ORDER BY " + KEY_NAME;
        Log.d("JJJ", query);

        ArrayList<ProgramInfo> programs = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            do {
                ProgramInfo program = new ProgramInfo();
                program.setProgramId(c.getInt(c.getColumnIndex(KEY_PROGRAM_ID)));
                program.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                program.setTopic(c.getString(c.getColumnIndex(KEY_TOPIC)));
                program.setDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
                program.setImageUrl(c.getString(c.getColumnIndex(KEY_IMAGE_URL)));
                programs.add(program);
                Log.d("JJJ", "program " + program.getName() + " id " + program.getProgramId());
            } while (c.moveToNext());
            c.close();
        }
        if (programs.size() == 0) {
            Log.d("JJJ", "no programs found");
        }
        return programs;
    }

    public void removeProgram(int programId) {
        deleteRow(TABLE_PROGRAM, KEY_PROGRAM_ID + " = " + programId);
    }

    public void addPodcast(PodcastInfo podcast) {
        ContentValues values = new ContentValues();
        values.put(KEY_PODCAST_ID, podcast.getPodcastId());
        values.put(KEY_PROGRAM_ID, podcast.getProgramId());
        values.put(KEY_TITLE, podcast.getTitle());
        values.put(KEY_DESCRIPTION, podcast.getDescription());
        values.put(KEY_DATE, podcast.getDate());
        values.put(KEY_AUDIO_URL, podcast.getAudioUrl());
        getWritableDatabase().replace(TABLE_PODCAST, null, values);
    }

    public PodcastInfo getPodcast(int podcastId) {
        String query = "SELECT * FROM " + TABLE_PODCAST + " WHERE " + KEY_PODCAST_ID + " = " + podcastId;
        Log.d("JJJ", query);

        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            PodcastInfo podcast = new PodcastInfo();
            podcast.setPodcastId(c.getInt(c.getColumnIndex(KEY_PODCAST_ID)));
            podcast.setProgramId(c.getInt(c.getColumnIndex(KEY_PROGRAM_ID)));
            podcast.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
            podcast.setDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
            podcast.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
            podcast.setAudioUrl(c.getString(c.getColumnIndex(KEY_AUDIO_URL)));
            c.close();
            Log.d("JJJ", "podcast " + podcast.getTitle() + " id " + podcast.getPodcastId());
            return podcast;
        }
        Log.d("JJJ", "podcastId not found " + podcastId);
        return new PodcastInfo();
    }

    public List<PodcastInfo> getPodcasts() {
        String query = "SELECT * FROM " + TABLE_PODCAST;
        Log.d("JJJ", query);

        ArrayList<PodcastInfo> podcasts = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            do {
                PodcastInfo podcast = new PodcastInfo();
                podcast.setPodcastId(c.getInt(c.getColumnIndex(KEY_PODCAST_ID)));
                podcast.setProgramId(c.getInt(c.getColumnIndex(KEY_PROGRAM_ID)));
                podcast.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                podcast.setDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
                podcast.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
                podcast.setAudioUrl(c.getString(c.getColumnIndex(KEY_AUDIO_URL)));
                podcasts.add(podcast);
                Log.d("JJJ", "podcast " + podcast.getTitle() + " id " + podcast.getPodcastId());
            } while (c.moveToNext());
            c.close();
        }
        if (podcasts.size() == 0) {
            Log.d("JJJ", "no podcasts found");
        }
        return podcasts;
    }

    public List<PodcastInfo> getPodcasts(int programId) {
        String query = "SELECT * FROM " + TABLE_PODCAST + " WHERE " + KEY_PROGRAM_ID + " = " + programId + " ORDER BY " + KEY_DATE + " DESC";
        Log.d("JJJ", query);

        ArrayList<PodcastInfo> podcasts = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            do {
                PodcastInfo podcast = new PodcastInfo();
                podcast.setPodcastId(c.getInt(c.getColumnIndex(KEY_PODCAST_ID)));
                podcast.setProgramId(c.getInt(c.getColumnIndex(KEY_PROGRAM_ID)));
                podcast.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                podcast.setDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
                podcast.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
                podcast.setAudioUrl(c.getString(c.getColumnIndex(KEY_AUDIO_URL)));
                podcasts.add(podcast);
                Log.d("JJJ", "podcast " + podcast.getTitle() + " id " + podcast.getPodcastId() + " date " + podcast.getDate());
            } while (c.moveToNext());
            c.close();
        }
        if (podcasts.size() == 0) {
            Log.d("JJJ", "no podcasts found");
        }
        return podcasts;
    }

    public int getPodcastCount(int programId) {
        String query = "SELECT COUNT(*) FROM " + TABLE_PODCAST + " WHERE " + KEY_PROGRAM_ID + " = " + programId;
        Log.d("JJJ", query);
        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            int count = c.getInt(0);
            c.close();
            Log.d("JJJ", "podcast count " + count + " for programId " + programId);
            return count;
        }
        Log.d("JJJ", "programId not found " + programId);
        return 0;
    }

    public void removePodcast(int podcastId) {
        deleteRow(TABLE_PODCAST, KEY_PODCAST_ID + " = " + podcastId);
    }

    private void deleteRow(String table, String whereSelector) {
        int rows = getWritableDatabase().delete(table, whereSelector, null);
        Log.d("JJJ", "DELETE FROM " + table + " WHERE " + whereSelector + " (" + rows + " row(s) deleted)");
    }

}
