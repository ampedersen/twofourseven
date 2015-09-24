package com.molamil.radio24syv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by jens on 24/09/15.
 */
public class Settings {
    public final static long DOWNLOAD_ID_UNKNOWN = -1;
    public final static int PODCAST_ID_UNKNOWN = -1;

    private SettingsDatabase settings;

    private static Settings instance = null;

    public static void initialize(Context context) {
        instance = new Settings(context);
    }

    public static Settings get() {
        if (instance == null) {
            Log.d("JJJ", "Settings must be initialized before you can use it");
            return null; // Not good
        }
        return instance;
    }

    public Settings(Context context) {
        settings = new SettingsDatabase(context);
    }

    public void writeLibraryIds(int podcastId, long downloadId) {
        settings.writeLibraryIds(podcastId, downloadId);
    }

    public long readLibraryDownloadId(int podcastId) {
        return settings.readLibraryDownloadId(podcastId);
    }

    public int readLibraryPodcastId(long downloadId) {
        return settings.readLibraryPodcastId(downloadId);
    }

    public void removeLibraryIds(int podcastId) {
        settings.removeLibraryIds(podcastId);
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
