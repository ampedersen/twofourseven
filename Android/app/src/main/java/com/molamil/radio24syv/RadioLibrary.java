package com.molamil.radio24syv;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by jens on 22/09/15.
 */
public class RadioLibrary {

    private final int DOWNLOAD_ID_UNKNOWN = -1;

    public static final int DOWNLOAD_STATUS_UNKNOWN = 0;
    public static final int DOWNLOAD_STATUS_FAILED = 1;
    public static final int DOWNLOAD_STATUS_PAUSED = 2;
    public static final int DOWNLOAD_STATUS_PENDING = 3;
    public static final int DOWNLOAD_STATUS_RUNNING = 4;
    public static final int DOWNLOAD_STATUS_SUCCESSFUL = 5;

    public static String getDownloadStatusName(int downloadStatus) {
        final String[] names = new String[] { "unknown", "failed", "paused", "pending", "running", "successful" };
        try {
            return names[downloadStatus];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "";
        }
    }

    private static RadioLibrary instance = null;

    public static RadioLibrary getInstance() {
        if (instance == null) {
            instance = new RadioLibrary();
        }
        return instance;
    }

    private HashMap<Integer, ArrayList<OnRadioLibraryStatusUpdatedListener>> listenersByPodcastId = new HashMap<>();

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
        boolean isRemoved;

        SharedPreferences settings = context.getSharedPreferences("Test", Context.MODE_PRIVATE);
        long downloadId = settings.getLong("downloadId-" + podcastId, DOWNLOAD_ID_UNKNOWN);
        Log.d("JJJ", "read downloadId " + downloadId + " for podcastId " + podcastId);

        boolean isDownloaded = (downloadId != DOWNLOAD_ID_UNKNOWN);
        if (isDownloaded) {
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            isRemoved = (manager.remove(podcastId) == 1);
        } else {
            isRemoved = false;
        }

        if (isRemoved) {
            Log.d("JJJ", "Removed podcastId " + podcastId);
        } else {
            Log.d("JJJ", "Unable to remove podcastId " + podcastId);
        }

        return isRemoved;
    }

    private Status getStatus(Context context, int podcastId) {
        Status status = new Status();

        // Get download ID for podcast
        SharedPreferences settings = context.getSharedPreferences("Test", Context.MODE_PRIVATE);
        long downloadId = settings.getLong("downloadId-" + podcastId, DOWNLOAD_ID_UNKNOWN);
        Log.d("JJJ", "read downloadId " + downloadId + " for podcastId " + podcastId);

        if (downloadId == DOWNLOAD_ID_UNKNOWN) {
            return status; // Return, podcast has not been downloaded
        }

        // Query download manager
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(downloadId);
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor c = manager.query(q);

        if (c.moveToFirst()) {
            int statusValue = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            float progress = c.getFloat(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            float total = c.getFloat(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            String localUrl = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

            switch (statusValue) {
                case DownloadManager.STATUS_FAILED:
                    status.downloadStatus = DOWNLOAD_STATUS_FAILED;
                    break;
                case DownloadManager.STATUS_PAUSED:
                    status.downloadStatus = DOWNLOAD_STATUS_PAUSED;
                    break;
                case DownloadManager.STATUS_PENDING:
                    status.downloadStatus = DOWNLOAD_STATUS_PENDING;
                    break;
                case DownloadManager.STATUS_RUNNING:
                    status.downloadStatus = DOWNLOAD_STATUS_RUNNING;
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    status.downloadStatus = DOWNLOAD_STATUS_SUCCESSFUL;
                    break;
            }

            if (total > 0) {
                status.downloadProgress = progress/total;
            }

            status.localPodcastUrl = localUrl;

            Log.d("JJJ", getDownloadStatusName(status.downloadStatus) + " " + progress + " of " + total + " bytes " + localUrl);
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

    public void addListener(Context context, int podcastId, OnRadioLibraryStatusUpdatedListener listener) {
        if (!listenersByPodcastId.containsKey(podcastId)) {
            listenersByPodcastId.put(podcastId, new ArrayList<OnRadioLibraryStatusUpdatedListener>()); // Create entry for podcast ID
        }

        if (!listenersByPodcastId.get(podcastId).contains(listener)) {
            listenersByPodcastId.get(podcastId).add(listener); // Add listener
            listener.OnRadioLibraryStatusUpdated(podcastId, getStatus(context, podcastId)); // Fire events to reflect current state
        }
    }

    public void removeListener(int podcastId, OnRadioLibraryStatusUpdatedListener listener) {
        if (listenersByPodcastId.containsKey(podcastId)) {
            if (listenersByPodcastId.get(podcastId).contains(listener)) {
                listenersByPodcastId.get(podcastId).remove(listener); // Remove listener
            }
        }
    }

//    private void callback(int podcastId) {
//        for (OnRadioLibraryStatusUpdatedListener l : listenerList) {
//            callback(l);
//        }
//    }

    // Our handler for received Intents. This will be called whenever an Intent with an action named BROADCAST_ID is broadcasted.
//    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            // Get extra data included in the Intent
//            int state = intent.getIntExtra(RadioPlayerService.BROADCAST_STATE, STATE_UNASSIGNED);
//            String url = intent.getStringExtra(RadioPlayerService.BROADCAST_URL);
//            if (url == null) {
//                url = URL_UNASSIGNED;
//            }
//            Log.d("JJJ", "Got message: " + RadioPlayerService.BROADCAST_STATE + " " + getStateName(state) + " " + RadioPlayerService.BROADCAST_URL + " " + url);
//
//            callback(); // Callback to listeners that something happened
//        }
//    };

    public class Status {
        private int downloadStatus = DOWNLOAD_STATUS_UNKNOWN;
        private float downloadProgress = 0;
        private String localPodcastUrl = null;

        public int getDownloadStatus() {
            return downloadStatus;
        }

        public float getDownloadProgress() {
            return downloadProgress;
        }

        public String getLocalPodcastUrl() { return localPodcastUrl; }

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

    public interface OnRadioLibraryStatusUpdatedListener {
        void OnRadioLibraryStatusUpdated(int podcastId, Status status);
    }

}
