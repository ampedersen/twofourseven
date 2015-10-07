package com.molamil.radio24syv.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.molamil.radio24syv.storage.model.PodcastInfo;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.storage.model.TopicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jens on 24/09/15.
 */
public class StorageDatabase extends SQLiteOpenHelper {

    private static final String TABLE_LIBRARY = "library";
    private static final String TABLE_PROGRAM = "program";
    private static final String TABLE_PODCAST = "podcast";
    private static final String TABLE_ALARM = "alarm";
    private static final String TABLE_TOPIC = "topic";
    private static final String TABLE_PLAYER_HISTORY = "player_history";
    private static final String TABLE_RELATED_PROGRAM = "related_program";

    private static final String KEY_PODCAST_ID = "podcast_id";
    private static final String KEY_DOWNLOAD_ID = "download_id";
    private static final String KEY_PROGRAM_ID = "program_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_TOPIC_ID = "topic_id";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DATE = "date";
    private static final String KEY_AUDIO_URL = "audio_url";
    private static final String KEY_COLOR = "color";
    private static final String KEY_PROGRAM_ID_RELATED = "program_id_related";
    private static final String KEY_PROGRAM_SLUG = "program_slug";
    private static final String KEY_ALARM_ID = "alarm_id";

    private static final String SQL_CREATE_TABLE_LIBRAY = "CREATE TABLE " + TABLE_LIBRARY + "("
            + KEY_PODCAST_ID + " INTEGER PRIMARY KEY, "
            + KEY_DOWNLOAD_ID + " BIGINT" // TODO index on download_id
            + ")";

    private static final String SQL_CREATE_TABLE_PROGRAM = "CREATE TABLE " + TABLE_PROGRAM + "("
            + KEY_PROGRAM_ID + " INTEGER PRIMARY KEY, "
            + KEY_PROGRAM_SLUG + " TEXT, " // TODO index on program_slug
            + KEY_NAME + " TEXT, "
            + KEY_TOPIC_ID + " TEXT, "
            + KEY_DESCRIPTION + " TEXT, "
            + KEY_IMAGE_URL + " TEXT, "
            + KEY_ACTIVE + " TEXT"
            + ")";

    private static final String SQL_CREATE_TABLE_PODCAST = "CREATE TABLE " + TABLE_PODCAST + "("
            + KEY_PODCAST_ID + " INTEGER PRIMARY KEY, "
            + KEY_PROGRAM_ID + " INTEGER, " // TODO index on program_id
            + KEY_TITLE + " TEXT, "
            + KEY_DESCRIPTION + " TEXT, "
            + KEY_AUDIO_URL + " TEXT, "
            + KEY_DATE + " TEXT" // "DATE" type is not supported by Cursor so have to use "TEXT" for KEY_DATE, yay
            + ")";

    private static final String SQL_CREATE_TABLE_TOPIC = "CREATE TABLE " + TABLE_TOPIC + "("
            + KEY_TOPIC_ID + " TEXT PRIMARY KEY, "
            + KEY_COLOR + " TEXT"
            + ")";

    private static final String SQL_CREATE_TABLE_PLAYER_HISTORY = "CREATE TABLE " + TABLE_PLAYER_HISTORY + "("
            + KEY_DATE + " TEXT, " // TODO index on date
            + KEY_PROGRAM_ID + " INTEGER"
            + ")";

    private static final String SQL_CREATE_TABLE_RELATED_PROGRAM = "CREATE TABLE " + TABLE_RELATED_PROGRAM + "("
            + KEY_PROGRAM_ID + " INTEGER, " // TODO index on program_id
            + KEY_PROGRAM_ID_RELATED + " INTEGER"
            + ")";

    private static final String SQL_CREATE_TABLE_ALARM = "CREATE TABLE " + TABLE_ALARM + "("
            + KEY_ALARM_ID + " INTEGER PRIMARY KEY, "
            + KEY_DATE + " TEXT, "
            + KEY_PROGRAM_SLUG + " TEXT"
            + ")";

    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS ";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "Storage.db";

    public StorageDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade existing table to new format if table version is upgraded in a later app version.
        // For now, discard the data and start over.
        Log.d("JJJ", "Upgrading database to version " + newVersion + " (was version " + oldVersion + ")");
        dropTables(db);
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        Log.d("JJJ", "Creating all database tables");
        db.beginTransaction();
        try {
            execSQL(db, SQL_CREATE_TABLE_LIBRAY);
            execSQL(db, SQL_CREATE_TABLE_PROGRAM);
            execSQL(db, SQL_CREATE_TABLE_PODCAST);
            execSQL(db, SQL_CREATE_TABLE_TOPIC);
            execSQL(db, SQL_CREATE_TABLE_PLAYER_HISTORY);
            execSQL(db, SQL_CREATE_TABLE_RELATED_PROGRAM);
            execSQL(db, SQL_CREATE_TABLE_ALARM);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void dropTables(SQLiteDatabase db) {
        Log.d("JJJ", "Dropping all database tables");
        db.beginTransaction();
        try {
            execSQL(db, SQL_DROP_TABLE + TABLE_LIBRARY);
            execSQL(db, SQL_DROP_TABLE + TABLE_PROGRAM);
            execSQL(db, SQL_DROP_TABLE + TABLE_PODCAST);
            execSQL(db, SQL_DROP_TABLE + TABLE_TOPIC);
            execSQL(db, SQL_DROP_TABLE + TABLE_PLAYER_HISTORY);
            execSQL(db, SQL_DROP_TABLE + TABLE_RELATED_PROGRAM);
            execSQL(db, SQL_DROP_TABLE + TABLE_ALARM);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
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
        String query = "SELECT " + KEY_DOWNLOAD_ID + " FROM " + TABLE_LIBRARY + " WHERE " + KEY_PODCAST_ID + " = " + podcastId + " LIMIT 1";
        Log.d("JJJ", query);

        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            long downloadId = c.getLong(c.getColumnIndex(KEY_DOWNLOAD_ID));
            c.close();
            Log.d("JJJ", "downloadId " + downloadId);
            return downloadId;
        } else {
            Log.d("JJJ", "downloadId unknown");
            return Storage.DOWNLOAD_ID_UNKNOWN;
        }
    }

    public int getLibraryPodcastId(long downloadId) {
        String query = "SELECT " + KEY_PODCAST_ID + " FROM " + TABLE_LIBRARY + " WHERE " + KEY_DOWNLOAD_ID + " = " + downloadId + " LIMIT 1";
        Log.d("JJJ", query);

        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            int podcastId = c.getInt(c.getColumnIndex(KEY_PODCAST_ID));
            c.close();
            Log.d("JJJ", "podcastId " + podcastId);
            return podcastId;
        }
        Log.d("JJJ", "podcastId unknown");
        return Storage.PODCAST_ID_UNKNOWN;
    }

    public void removeLibraryIds(int podcastId) {
        deleteRow(TABLE_LIBRARY, KEY_PODCAST_ID + " = " + podcastId);
    }

    public void writeProgramInfo(ProgramInfo program) {
        writeProgramInfo(getWritableDatabase(), program);
    }

    public void addPrograms(List<ProgramInfo> programs) {
        Log.d("JJJ", "Writing " + programs.size() + " programs to database");
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction(); // Use transaction because we will be adding lots of items (saves a lot of database operations)
        try {
            for (ProgramInfo p : programs) {
                writeProgramInfo(db, p);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public ProgramInfo getProgram(int programId) {
        String query = "SELECT * FROM " + TABLE_PROGRAM + " WHERE " + KEY_PROGRAM_ID + " = " + programId + " LIMIT 1";
        Log.d("JJJ", query);

        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            ProgramInfo program = readProgramInfo(c);
            c.close();
            Log.d("JJJ", "program " + program.getName() + " id " + program.getProgramId() + " slug " + program.getProgramSlug());
            return program;
        }
        Log.d("JJJ", "programId not found " + programId);
        return null;
    }

    public ProgramInfo getProgram(String programSlug) {
        String query = "SELECT * FROM " + TABLE_PROGRAM + " WHERE " + KEY_PROGRAM_SLUG + " = '" + programSlug + "' LIMIT 1";
        Log.d("JJJ", query);

        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            ProgramInfo program = readProgramInfo(c);
            c.close();
            Log.d("JJJ", "program " + program.getName() + " id " + program.getProgramId() + " slug " + program.getProgramSlug());
            return program;
        }
        Log.d("JJJ", "programSlug not found " + programSlug);
        return null;
    }

    public List<ProgramInfo> getPrograms() {
        String query = "SELECT * FROM " + TABLE_PROGRAM + " ORDER BY " + KEY_NAME;
        Log.d("JJJ", query);

        ArrayList<ProgramInfo> programs = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            do {
                ProgramInfo program = readProgramInfo(c);
                programs.add(program);
                //Log.d("JJJ", "program " + program.getName() + " id " + program.getProgramId());
            } while (c.moveToNext());
            c.close();
        }
        if (programs.size() == 0) {
            Log.d("JJJ", "no programs found");
        }
        return programs;
    }

    public List<ProgramInfo> getProgramsWithPodcastsInLibrary() {
        String query = "SELECT * FROM " + TABLE_PROGRAM + " WHERE " + KEY_PROGRAM_ID + " IN (SELECT " + KEY_PROGRAM_ID + " FROM " + TABLE_PODCAST + ")";
        Log.d("JJJ", query);

        ArrayList<ProgramInfo> programs = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            do {
                ProgramInfo program = readProgramInfo(c);
                programs.add(program);
                //Log.d("JJJ", "program " + program.getName() + " id " + program.getProgramId());
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
        writePodcastInfo(getWritableDatabase(), podcast);
    }

    public PodcastInfo getPodcast(int podcastId) {
        String query = "SELECT * FROM " + TABLE_PODCAST + " WHERE " + KEY_PODCAST_ID + " = " + podcastId + " LIMIT 1";
        Log.d("JJJ", query);

        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            PodcastInfo podcast = readPodcastInfo(c);
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
                PodcastInfo podcast = readPodcastInfo(c);
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
                PodcastInfo podcast = readPodcastInfo(c);
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

//    public void addTopic(String topic) {
//        ContentValues values = new ContentValues();
//        values.put(KEY_TOPIC_ID, topic); // Funny thing is that the key is also the actual topic text displayed...
//        values.put(KEY_COLOR, Storage.COLOR_UNKNOWN);
//        getWritableDatabase().replace(TABLE_TOPIC, null, values);
//    }

//    public void setTopicColor(String topic, String color) {
//        ContentValues values = new ContentValues();
//        values.put(KEY_TOPIC_ID, topic);
//        values.put(KEY_COLOR, color);
//        getWritableDatabase().replace(TABLE_TOPIC, null, values);
//    }

//    public String getTopicColor(String topic) {
//        String query = "SELECT " + KEY_COLOR + " FROM " + TABLE_TOPIC + " WHERE " + KEY_TOPIC_ID + " = " + topic + " LIMIT 1";
//        Log.d("JJJ", query);
//
//        Cursor c = getReadableDatabase().rawQuery(query, null);
//        if ((c != null) && c.moveToFirst()) {
//            String color = c.getString(c.getColumnIndex(KEY_COLOR));
//            c.close();
//            Log.d("JJJ", "color " + color);
//            return color;
//        } else {
//            Log.d("JJJ", "color unknown");
//            return Storage.COLOR_UNKNOWN;
//        }
//    }

//    public List<String> getTopicNames() {
//        String query = "SELECT DISTINCT " + KEY_TOPIC_ID + " FROM " + TABLE_PROGRAM;
//        Log.d("JJJ", query);
//
//        ArrayList<String> names = new ArrayList<>();
//        Cursor c = getReadableDatabase().rawQuery(query, null);
//        if ((c != null) && c.moveToFirst()) {
//            do {
//                String topic = c.getString(c.getColumnIndex(KEY_TOPIC_ID));
//                names.add(topic);
//                Log.d("JJJ", "topic " + topic);
//            } while (c.moveToNext());
//            c.close();
//            return names;
//        } else {
//            Log.d("JJJ", "topics unknown");
//            return names;
//        }
//    }

    public void addTopics(List<TopicInfo> topics) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (TopicInfo t : topics) {
                writeTopicInfo(db, t);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public TopicInfo getTopic(String topicId) {
        String query = "SELECT * FROM " + TABLE_TOPIC + " WHERE " + KEY_TOPIC_ID + " = '" + topicId + "' LIMIT 1";
        Log.d("JJJ", query);

        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            TopicInfo topic = readTopicInfo(c);
            c.close();
            Log.d("JJJ", "topic " + topic.getTopicId() + " color " + topic.getColor());
            return topic;
        } else {
            Log.d("JJJ", "topic unknown " + topicId);
            return null;
        }
    }

    public List<TopicInfo> getTopics() {
        String query = "SELECT * FROM " + TABLE_TOPIC + " ORDER BY " + KEY_TOPIC_ID;
        Log.d("JJJ", query);

        ArrayList<TopicInfo> topics = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            do {
                TopicInfo t = readTopicInfo(c);
                topics.add(t);
                Log.d("JJJ", "topic " + t.getTopicId() + " color " + t.getColor());
            } while (c.moveToNext());
            c.close();
        } else {
            Log.d("JJJ", "no topics");
        }
        return topics;
    }

    public void addPlayerHistory(int programId, String date) {
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, date);
        values.put(KEY_PROGRAM_ID, programId);
        getWritableDatabase().insert(TABLE_PLAYER_HISTORY, null, values);
    }

    public List<ProgramInfo> getPlayerHistory(int limit) {
        //cool but has duplicates
        String query = "SELECT * FROM " + TABLE_PROGRAM + " JOIN " + TABLE_PLAYER_HISTORY + " ON " + TABLE_PROGRAM + "." + KEY_PROGRAM_ID + " = " + TABLE_PLAYER_HISTORY + "." + KEY_PROGRAM_ID + " ORDER BY " + KEY_DATE + " DESC";
        Log.d("JJJ", query);

//        String s = "---------------------";
        List<ProgramInfo> programs = new ArrayList<>();
        List<Integer> addedProgramIds = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            do {
                ProgramInfo program = readProgramInfo(c);
//                String date = c.getString(c.getColumnIndex(KEY_DATE));
                boolean isAdded = addedProgramIds.contains(program.getProgramId()); // Compensating for SQL skills (do not want to waste more time trying different queries using keywords (NATRUAL JOIN hello) which may or may not be supported by SQLite on Android 4...)
                if (!isAdded) {
                    programs.add(program);
                    addedProgramIds.add(program.getProgramId());
                }
//                s += "\n" + date + " program " + program.getName();
            } while (c.moveToNext() && (programs.size() < limit));
            c.close();
//            Log.d("JJJ", s);
            return programs;
        } else {
            Log.d("JJJ", "no programs");
            return programs;
        }
    }

    public void deletePlayerHistory() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            execSQL(db, SQL_DROP_TABLE + TABLE_PLAYER_HISTORY);
            execSQL(db, SQL_DROP_TABLE + TABLE_RELATED_PROGRAM);
            execSQL(db, SQL_CREATE_TABLE_PLAYER_HISTORY);
            execSQL(db, SQL_CREATE_TABLE_RELATED_PROGRAM);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void addRelatedPrograms(int programId, List<Integer> relatedProgramIds) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (int otherId : relatedProgramIds) {
                ContentValues values = new ContentValues();
                values.put(KEY_PROGRAM_ID, programId);
                values.put(KEY_PROGRAM_ID_RELATED, otherId);
                db.replace(TABLE_RELATED_PROGRAM, null, values); // Replace instead of insert to avoid duplicates
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<ProgramInfo> getRelatedPrograms(int programId, int limit) {
        return getProgramsInQuery("SELECT DISTINCT " + KEY_PROGRAM_ID_RELATED + " FROM " + TABLE_RELATED_PROGRAM + " WHERE " + KEY_PROGRAM_ID + " = " + programId + " LIMIT " + limit);
    }

    public int addAlarm(String programSlug, String programTime) {
        int alarmId = Storage.ALARM_ID_UNKNOWN;

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // First write the new entry
            ContentValues values = new ContentValues();
            values.put(KEY_PROGRAM_SLUG, programSlug);
            values.put(KEY_DATE, programTime);
            db.insert(TABLE_ALARM, null, values); // Alarm ID is automatically set by the database because it is the primary key. Same as if the column was AUTOINCREMENT.
            // Then read the alarm ID it got assigned
            alarmId = getAlarmId(db, programSlug, programTime);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return alarmId;
    }

    public int getAlarmId(String programSlug, String programTime) {
        return getAlarmId(getReadableDatabase(), programSlug, programTime);
    }

    private int getAlarmId(SQLiteDatabase db, String programSlug, String programTime) {
        String query = "SELECT " + KEY_ALARM_ID + " FROM " + TABLE_ALARM + " WHERE " + KEY_PROGRAM_SLUG + " = '" + programSlug + "' AND " + KEY_DATE + " = '" + programTime + "' LIMIT 1";
        Log.d("JJJ", query);
        int alarmId = Storage.ALARM_ID_UNKNOWN;
        Cursor c = db.rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            alarmId = c.getInt(c.getColumnIndex(KEY_ALARM_ID));
            c.close();
        }
        return alarmId;
    }

    public void removeAlarm(int alarmId) {
        deleteRow(TABLE_ALARM, KEY_ALARM_ID + " = " + alarmId);
    }

    private List<ProgramInfo> getProgramsInQuery(String queryWithProgramIds) {
        String query = "SELECT * FROM " + TABLE_PROGRAM + " WHERE " + KEY_PROGRAM_ID + " IN (" + queryWithProgramIds + ")";
        Log.d("JJJ", query);

        List<ProgramInfo> programs = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            do {
                ProgramInfo program = readProgramInfo(c);
                programs.add(program);
                Log.d("JJJ", "program " + program.getName());
            } while (c.moveToNext());
            c.close();
            return programs;
        } else {
            Log.d("JJJ", "no programs");
            return programs;
        }
    }

    private void deleteRow(String table, String whereSelector) {
        int rows = getWritableDatabase().delete(table, whereSelector, null);
        Log.d("JJJ", "DELETE FROM " + table + " WHERE " + whereSelector + " (" + rows + " row(s) deleted)");
    }

    private static void writeProgramInfo(SQLiteDatabase db, ProgramInfo program) {
        ContentValues values = new ContentValues();
        values.put(KEY_PROGRAM_ID, program.getProgramId());
        values.put(KEY_PROGRAM_SLUG, program.getProgramSlug());
        values.put(KEY_NAME, program.getName());
        values.put(KEY_TOPIC_ID, program.getTopic());
        values.put(KEY_DESCRIPTION, program.getDescription());
        values.put(KEY_IMAGE_URL, program.getImageUrl());
        values.put(KEY_ACTIVE, Boolean.toString(program.getActive()));
        //Log.d("JJJ", "Writing " + program.getProgramSlug());
        db.replace(TABLE_PROGRAM, null, values);
    }

    private static ProgramInfo readProgramInfo(Cursor c) {
        ProgramInfo program = new ProgramInfo();
        program.setProgramId(c.getInt(c.getColumnIndex(KEY_PROGRAM_ID)));
        program.setProgramSlug(c.getString(c.getColumnIndex(KEY_PROGRAM_SLUG)));
        program.setName(c.getString(c.getColumnIndex(KEY_NAME)));
        program.setTopic(c.getString(c.getColumnIndex(KEY_TOPIC_ID)));
        program.setDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
        program.setImageUrl(c.getString(c.getColumnIndex(KEY_IMAGE_URL)));
        program.setActive(Boolean.parseBoolean(c.getString(c.getColumnIndex(KEY_ACTIVE))));
        return program;
    }

    private static void writePodcastInfo(SQLiteDatabase db, PodcastInfo podcast) {
        ContentValues values = new ContentValues();
        values.put(KEY_PODCAST_ID, podcast.getPodcastId());
        values.put(KEY_PROGRAM_ID, podcast.getProgramId());
        values.put(KEY_TITLE, podcast.getTitle());
        values.put(KEY_DESCRIPTION, podcast.getDescription());
        values.put(KEY_DATE, podcast.getDate());
        values.put(KEY_AUDIO_URL, podcast.getAudioUrl());
        db.replace(TABLE_PODCAST, null, values);
    }

    private static PodcastInfo readPodcastInfo(Cursor c) {
        PodcastInfo podcast = new PodcastInfo();
        podcast.setPodcastId(c.getInt(c.getColumnIndex(KEY_PODCAST_ID)));
        podcast.setProgramId(c.getInt(c.getColumnIndex(KEY_PROGRAM_ID)));
        podcast.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
        podcast.setDescriptionText(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
        podcast.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
        podcast.setAudioUrl(c.getString(c.getColumnIndex(KEY_AUDIO_URL)));
        return podcast;
    }

    private static void writeTopicInfo(SQLiteDatabase db, TopicInfo topic) {
        ContentValues values = new ContentValues();
        values.put(KEY_TOPIC_ID, topic.getTopicId()); // Funny thing is that the key is also the actual topic text displayed...
        values.put(KEY_COLOR, topic.getColor());
        db.replace(TABLE_TOPIC, null, values);
    }

    private static TopicInfo readTopicInfo(Cursor c) {
        TopicInfo topic = new TopicInfo();
        topic.setTopicId(c.getString(c.getColumnIndex(KEY_TOPIC_ID)));
        topic.setColor(c.getString(c.getColumnIndex(KEY_COLOR)));
        return topic;
    }

}
