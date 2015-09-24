package com.molamil.radio24syv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.molamil.radio24syv.Settings;

import java.util.Locale;

/**
 * Created by jens on 24/09/15.
 */
public class SettingsDatabase extends SQLiteOpenHelper {

    private static final String TABLE_PROGRAM = "program";
    private static final String TABLE_PODCAST = "podcast";
    private static final String TABLE_PICTURE = "picture";
    private static final String TABLE_CATEGORY = "category";
    private static final String TABLE_LIBRARY = "library";

    private static final String KEY_PODCAST_ID = "podcast_id";
    private static final String KEY_DOWNLOAD_ID = "download_id";

    private static final String SQL_CREATE_TABLE_LIBRAY =
            "CREATE TABLE " + TABLE_LIBRARY + "(" + KEY_PODCAST_ID + " INTEGER PRIMARY KEY, "+ KEY_DOWNLOAD_ID + " BIGINT" + ")"; // TODO table index download_id

//        private static final String CREATE_TABLE_TAG = "CREATE TABLE " + TABLE_TAG
//                + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TAG_NAME + " TEXT,"
//                + KEY_CREATED_AT + " DATETIME" + ")";
//
//        private static final String CREATE_TABLE_TODO_TAG = "CREATE TABLE "
//                + TABLE_TODO_TAG + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
//                + KEY_TODO_ID + " INTEGER," + KEY_TAG_ID + " INTEGER,"
//                + KEY_CREATED_AT + " DATETIME" + ")";

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Settings.db";

    public SettingsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(CREATE_TABLE_TODO);
//        db.execSQL(CREATE_TABLE_TAG);
//        db.execSQL(CREATE_TABLE_TODO_TAG);
        db.execSQL(SQL_CREATE_TABLE_LIBRAY);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO_TAG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIBRARY);
        onCreate(db);
    }

    public void addLibraryIds(int podcastId, long downloadId) {
        ContentValues values = new ContentValues();
        values.put(KEY_PODCAST_ID, podcastId);
        values.put(KEY_DOWNLOAD_ID, downloadId);
        getWritableDatabase().replace(TABLE_LIBRARY, null, values);
    }

    public long getLibraryDownloadId(int podcastId) {
        // Better performance but unreadable
        //String query = String.format(Locale.US, "SELECT %s FROM %s WHERE %s = %d", KEY_DOWNLOAD_ID, TABLE_LIBRARY,KEY_PODCAST_ID, podcastId );
        String query = "SELECT " + KEY_DOWNLOAD_ID + " FROM " + TABLE_LIBRARY + " WHERE " + KEY_PODCAST_ID + " = " + podcastId;
        Log.d("JJJ", query);

        Cursor c = getReadableDatabase().rawQuery(query, null);
        if ((c != null) && c.moveToFirst()) {
            long downloadId = c.getLong(c.getColumnIndex(KEY_DOWNLOAD_ID));
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
        String query = "DELETE FROM " + TABLE_LIBRARY + " WHERE " + KEY_PODCAST_ID + " = " + podcastId;
        Log.d("JJJ", query);
        getWritableDatabase().rawQuery(query, null); // TODO error handling?
    }

    /*
    // TODO nicer settings implementation
    // Writes podcast ID and download ID to settings file so we can look them up later.
    private void writeIds(Context context, int podcastId, long downloadId) {
        SharedPreferences settings = context.getSharedPreferences("Test", Context.MODE_PRIVATE);
        settings.edit()
                .putLong("downloadId-" + podcastId, downloadId)
                .putInt("podcastId-" + downloadId, podcastId)
                .apply();
        //Log.d("JJJ", "write downloadId "+ downloadId + " for podcastId " + podcastId);
    }

    // Look up download ID for a podcast ID. Returns DOWNLOAD_ID_UNKNOWN if not found.
    private long readDownloadId(Context context, int podcastId) {
        SharedPreferences settings = context.getSharedPreferences("Test", Context.MODE_PRIVATE);
        long downloadId = settings.getLong("downloadId-" + podcastId, DOWNLOAD_ID_UNKNOWN);
        //Log.d("JJJ", "read downloadId " + downloadId + " for podcastId " + podcastId);
        return downloadId;
    }

    // Look up podcast ID for a download ID. Returns PODCAST_ID_UNKNOWN if not found.
    private int readPodcastId(Context context, long downloadId) {
        SharedPreferences settings = context.getSharedPreferences("Test", Context.MODE_PRIVATE);
        int podcastId = settings.getInt("podcastId-" + downloadId, PODCAST_ID_UNKNOWN);
        //Log.d("JJJ", "read podcastId " + podcastId + " for downloadId " + downloadId);
        return podcastId;
    }

    // Removes podcast ID and download ID from settings file.
    private void removeIds(Context context, int podcastId, long downloadId) {
        SharedPreferences settings = context.getSharedPreferences("Test", Context.MODE_PRIVATE);
        settings.edit()
                .remove("downloadId-" + podcastId)
                .remove("podcastId-" + downloadId)
                .apply();
        //Log.d("JJJ", "remove downloadId " + downloadId + " and podcastId " + podcastId);
    }
    */
}
