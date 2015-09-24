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

    private final long DOWNLOAD_ID_UNKNOWN = -1;
    private final int PODCAST_ID_UNKNOWN = -1;

    public static final int DOWNLOAD_STATUS_UNKNOWN = 0;
    public static final int DOWNLOAD_STATUS_FAILED = 1;
    public static final int DOWNLOAD_STATUS_PAUSED = 2;
    public static final int DOWNLOAD_STATUS_PENDING = 3;
    public static final int DOWNLOAD_STATUS_RUNNING = 4;
    public static final int DOWNLOAD_STATUS_SUCCESSFUL = 5;

    public static String getDownloadStatusName(int downloadStatus) {
        final String[] names = new String[]{"unknown", "failed", "paused", "pending", "running", "successful"};
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

    public void download(Context context, int podcastId, String podcastUrl, String title) {
        String url = getUrl(context, podcastUrl);
        //String filename = getFilename(url);
        String filename = getFilename(url, podcastId);

        Log.d("JJJ", "download " + filename + " " + url);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_PODCASTS, filename);
        request.setTitle(title);
        request.setDescription(context.getResources().getString(R.string.app_name));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        // Get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = manager.enqueue(request);

        writeIds(context, podcastId, downloadId);
        callback(context, podcastId);
    }

    // Deletes the downloaded file or cancels its download and cleans up.
    public void remove(Context context, int podcastId) {
        boolean isRemoved;
        long downloadId = readDownloadId(context, podcastId);
        boolean isDownloaded = (downloadId != DOWNLOAD_ID_UNKNOWN);
        if (isDownloaded) {
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Status status = getStatus(context, podcastId);
            Log.d("JJJ", "status " + status.getDownloadProgressText() + " " + status.getDownloadStatusText());
            isRemoved = (manager.remove(downloadId) == 1);
        } else {
            isRemoved = false;
        }

        if (isRemoved) {
            Log.d("JJJ", "Removed podcastId " + podcastId);
            removeIds(context, podcastId, downloadId); // Remove entries for the now non-existing download
            callback(context, podcastId);
        } else {
            Log.d("JJJ", "Unable to remove podcastId " + podcastId);
        }
    }

    private Status getStatus(Context context, int podcastId) {
        long downloadId = readDownloadId(context, podcastId);
        return getStatus(context, downloadId);
    }

    private Status getStatus(Context context, long downloadId) {
        Status status = new Status();

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
            Log.d("JJJ", "Unable to query downloadmanager about downloadId " + downloadId);
        }

        c.close();

        return status;
    }

    // TODO nicer settings implementation
    private void writeIds(Context context, int podcastId, long downloadId) {
        SharedPreferences settings = context.getSharedPreferences("Test", Context.MODE_PRIVATE);
        settings.edit()
                .putLong("downloadId-" + podcastId, downloadId)
                .putInt("podcastId-" + downloadId, podcastId)
                .apply();
        //Log.d("JJJ", "write downloadId "+ downloadId + " for podcastId " + podcastId);
    }

    private long readDownloadId(Context context, int podcastId) {
        // Get download ID for podcast
        SharedPreferences settings = context.getSharedPreferences("Test", Context.MODE_PRIVATE);
        long downloadId = settings.getLong("downloadId-" + podcastId, DOWNLOAD_ID_UNKNOWN);
        //Log.d("JJJ", "read downloadId " + downloadId + " for podcastId " + podcastId);
        return downloadId;
    }

    private int readPodcastId(Context context, long downloadId) {
        // Get podcast ID for download
        SharedPreferences settings = context.getSharedPreferences("Test", Context.MODE_PRIVATE);
        int podcastId = settings.getInt("podcastId-" + downloadId, PODCAST_ID_UNKNOWN);
        //Log.d("JJJ", "read podcastId " + podcastId + " for downloadId " + downloadId);
        return podcastId;
    }

    private void removeIds(Context context, int podcastId, long downloadId) {
        SharedPreferences settings = context.getSharedPreferences("Test", Context.MODE_PRIVATE);
        settings.edit()
                .remove("downloadId-" + podcastId)
                .remove("podcastId-" + downloadId)
                .apply();
        //Log.d("JJJ", "remove downloadId " + downloadId + " and podcastId " + podcastId);
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

    private static String getFilename(final String path, int podcastId) {
        if (path == null) {
            return ""; // Return empty string, path is null
        }

        String extension;
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex < path.length() - 1) {
            extension = path.substring(dotIndex + 1); // Extension is everything after the dot
        } else {
            extension = null; // No extension
        }

        if (extension != null) {
            return String.format(Locale.US, "%d.%s", podcastId, extension);
        } else {
            return String.format(Locale.US, "%d", podcastId);
        }
    }

    public static String getUrl(Context context, String podcastUrl) {
        if (!podcastUrl.startsWith(context.getResources().getString(R.string.url_offline_radio))) {
            return context.getResources().getString(R.string.url_offline_radio) + podcastUrl;
        } else {
            return podcastUrl; // Return same URL because it has already been prepended
        }
    }

    public void addListener(Context context, int podcastId, OnRadioLibraryStatusUpdatedListener listener) {
        // Create "download complete" listener
        boolean isInitialized = (listenersByPodcastId.size() != 0);
        if (!isInitialized) {
            context.registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

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

    // Our handler for received Intents. This will be called whenever an Intent with an action named ACTION_DOWNLOAD_COMPLETE is broadcasted.
    private BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, DOWNLOAD_ID_UNKNOWN);
            boolean isVerified = verifyDownload(context, downloadId);
            if (isVerified) {
                Log.d("JJJ", "Download complete and verified " + downloadId);
            } else {
                Log.d("JJJ", "Download complete NOT verified " + downloadId);
            }
            Log.d("JJJ", "Got message: " + DownloadManager.ACTION_DOWNLOAD_COMPLETE + " downloadId " + downloadId + " isVerified " + isVerified);

            int podcastId = readPodcastId(context, downloadId);
            if (podcastId != PODCAST_ID_UNKNOWN) {
                callback(context, podcastId); // Callback to listeners that something happened
            } else {
                Log.d("JJJ", "Download complete but no podcastId is linked to that downloadId " + downloadId + " - is it our download at all (are we even notified about other downloads?) and if it is - is settings file up to date?");
            }
        }
    };

    private void callback(Context context, int podcastId) {
        if (!listenersByPodcastId.containsKey(podcastId)) {
            Log.d("JJJ", "Unable to make library status callbacks for podcastId " + podcastId + " because no listeners are subscribed");
            return; // Return, nobody is listening
        }

        Status status = getStatus(context, podcastId);
        for (OnRadioLibraryStatusUpdatedListener l : listenersByPodcastId.get(podcastId)) {
            l.OnRadioLibraryStatusUpdated(podcastId, status);
        }
    }

    /**
     * From http://stackoverflow.com/questions/8937817/downloadmanager-action-download-complete-broadcast-receiver-receiving-same-downl
     * Check if download was valid, see issue
     * http://code.google.com/p/android/issues/detail?id=18462
     */
    private boolean verifyDownload(Context context, long downloadId) {
        Log.d("JJJ", "Verifying file for downloadId " + downloadId);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor c = manager.query(new DownloadManager.Query().setFilterById(downloadId));
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                return true; // Download is valid, celebrate
            } else {
                int reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
                Log.d("JJJ", "Download not verified, status [" + status + "] reason [" + reason + "]");
                return false;
            }
        }
        return false;
    }

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
