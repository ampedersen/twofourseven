package com.molamil.radio24syv;

import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.molamil.radio24syv.api.model.Podcast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

/**
 * Created by jens on 22/09/15.
 */
public class RadioLibrary {

    public enum DownloadStatus { UNKNOWN, FAILED, PAUSED, PENDING, RUNNING, SUCCESSFUL }

    public static String getDownloadStatusName(DownloadStatus downloadStatus) {
        final String[] names = new String[] { "unknown", "failed", "paused", "pending", "running", "successful" };
        try {
            return names[downloadStatus.ordinal()];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "";
        }
    }

    public class Status {
        private DownloadStatus downloadStatus = DownloadStatus.UNKNOWN;
        private float downloadProgress = 0;

        public DownloadStatus getDownloadStatus() {
            return downloadStatus;
        }

        public float getDownloadProgress() {
            return downloadProgress;
        }

        public String getDownloadStatusText() {
            return getDownloadStatusName(downloadStatus);
        }

        public String getDownloadProgressText() {
            return String.format(Locale.getDefault(), "%d%%", (int)(100 * downloadProgress)); // "%%" is converted to "%" (Java secret)
        }

        @Override
        public String toString() {
            return String.format("%s %s", getDownloadStatusText(), getDownloadProgressText());
        }
    }

    private static RadioLibrary instance = null;

    public static RadioLibrary getInstance() {
        if (instance == null) {
            instance = new RadioLibrary();
        }
        return instance;
    }

    public void download(Context context, int podcastId, String podcastUrl, String title)  {
        String url = getUrl(context, podcastUrl);
        String filename = getFilename(url);

        Log.d("JJJ", "download " + filename + " " + url);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_PODCASTS, filename);
        request.setTitle(title);
        request.setDescription(context.getResources().getString(R.string.app_name));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE); // TODO hide podcast download from notifications?

        // Get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = manager.enqueue(request);

        SharedPreferences settings = context.getSharedPreferences("Test", Context.MODE_PRIVATE);
        settings.edit().putLong("downloadId-" + podcastId, downloadId).apply();
        Log.d("JJJ", "write downloadId " + downloadId + " for podcastId " + podcastId);
    }

    // Deletes the downloaded file or cancels its download and cleans up.
    public boolean remove(Context context, int podcastId) {
        SharedPreferences settings = context.getSharedPreferences("Test", Context.MODE_PRIVATE);
        long downloadId = settings.getLong("downloadId-" + podcastId, 0);
        Log.d("JJJ", "read downloadId " + downloadId + " for podcastId " + podcastId);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        boolean isRemoved = (manager.remove(podcastId) == 1);
        if (isRemoved) {
            Log.d("JJJ", "Removed podcastId " + podcastId);
        } else {
            Log.d("JJJ", "Unable to remove podcastId " + podcastId);
        }
        return isRemoved;
    }

    public Status getStatus(Context context, int podcastId) {
        Status status = new Status();

        // Get download ID for podcast
        SharedPreferences settings = context.getSharedPreferences("Test", Context.MODE_PRIVATE);
        long downloadId = settings.getLong("downloadId-" + podcastId, 0);
        Log.d("JJJ", "read downloadId " + downloadId + " for podcastId " + podcastId);

        // Query download manager
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(downloadId);
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor c = manager.query(q);

        if (c.moveToFirst()) {
            int statusValue = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            float progress = c.getFloat(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            float total = c.getFloat(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

            switch (statusValue) {
                case DownloadManager.STATUS_FAILED:
                    status.downloadStatus = DownloadStatus.FAILED;
                    break;
                case DownloadManager.STATUS_PAUSED:
                    status.downloadStatus = DownloadStatus.PAUSED;
                    break;
                case DownloadManager.STATUS_PENDING:
                    status.downloadStatus = DownloadStatus.PENDING;
                    break;
                case DownloadManager.STATUS_RUNNING:
                    status.downloadStatus = DownloadStatus.RUNNING;
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    status.downloadStatus = DownloadStatus.SUCCESSFUL;
                    break;
            }

            if (total > 0) {
                status.downloadProgress = progress/total;
            }
            Log.d("JJJ", progress + " of " + total + " bytes");
        } else {
            Log.d("JJJ", "Unable to query downloadmanager about podcastId " + podcastId);
        }

        c.close();

        return status;
    }

    private static String getFilename(final String path) {
        if (path == null) {
            return ""; // Return empty string, path is null
        }
        int pathEndIndex = path.lastIndexOf('/');
        if (pathEndIndex < path.length() - 1) {
            return path.substring(pathEndIndex + 1); // Return everything after the last slash
        } else {
            return ""; // Return empty string, there is no filename when the path ends with a slash
        }
    }

    public static String getUrl(Context context, String podcastUrl) {
        return context.getResources().getString(R.string.url_offline_radio) + podcastUrl;
    }


}
