package com.molamil.radio24syv.storage;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.molamil.radio24syv.R;
import com.molamil.radio24syv.storage.model.PodcastInfo;
import com.molamil.radio24syv.storage.model.ProgramInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Podcast download manager. Uses Android's built-in download manager for file download and local file management.
 * Created by jens on 22/09/15.
 */
public class RadioLibrary {

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

    public void download(Context context, ProgramInfo program, PodcastInfo podcast) {
        int podcastId = podcast.getPodcastId();
        String podcastUrl = podcast.getAudioUrl();

        String url = getUrl(context, podcastUrl);
        String filename = getFilename(url, podcastId);

        Log.d("JJJ", "download " + filename + " " + url);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_PODCASTS, filename);
        request.setTitle(podcast.getTitle());
        request.setDescription(context.getResources().getString(R.string.app_name));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        // Get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = manager.enqueue(request);

        Storage.get().addProgram(program);
        Storage.get().addPodcast(podcast);
        Storage.get().addLibraryIds(podcastId, downloadId);
        callback(context, podcastId);
    }

    // Deletes the downloaded file or cancels its download and cleans up.
    public void remove(Context context, PodcastInfo podcast) {
        boolean isRemoved;
        long downloadId = Storage.get().getLibraryDownloadId(podcast.getPodcastId());
        boolean isDownloaded = (downloadId != Storage.DOWNLOAD_ID_UNKNOWN);
        if (isDownloaded) {
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            isRemoved = (manager.remove(downloadId) == 1);
        } else {
            isRemoved = false;
        }

        if (isRemoved) {
            Log.d("JJJ", "Removed podcastId " + podcast.getPodcastId() + " " + podcast.getTitle());
            // Remove entries for the now non-existing download
            Storage.get().removePodcast(podcast.getPodcastId());
            Storage.get().removeLibraryIds(podcast.getPodcastId());
            // Keep program info in the database, so do not do this:
            //if (Storage.get().getPodcastCount(podcast.getProgramId()) == 0) {
            //    Storage.get().removeProgram(podcast.getProgramId()); // Remove program if it has no downloaded podcasts left
            //}
        } else {
            // Something is funky
            Status status = getStatus(context, podcast.getPodcastId());
            Log.w("JJJ", "Unable to remove podcastId " + podcast.getPodcastId() + " status " + status.getDownloadProgressText() + " " + status.getDownloadStatusText());
        }

        callback(context, podcast.getPodcastId());
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
            return String.format(Locale.US, "%d.%s", podcastId, extension); // "1234.mp3"
        } else {
            return String.format(Locale.US, "%d", podcastId); // "1234" (no extension)
        }
    }

    public static String getUrl(Context context, String podcastUrl) {
        if (!podcastUrl.startsWith(context.getResources().getString(R.string.url_offline_radio))) {
            return context.getResources().getString(R.string.url_offline_radio) + podcastUrl;
        } else {
            return podcastUrl; // Return same URL because it has already been prepended (just in case)
        }
    }

    public Status getStatus(Context context, int podcastId) {
        long downloadId = Storage.get().getLibraryDownloadId(podcastId);
        return getStatus(context, downloadId);
    }

    private Status getStatus(Context context, long downloadId) {
        Status status = new Status();

        if (downloadId == Storage.DOWNLOAD_ID_UNKNOWN) {
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
                status.downloadProgress = progress / total;
            }

            status.localPodcastUrl = localUrl;

            Log.d("JJJ", getDownloadStatusName(status.downloadStatus) + " " + progress + " of " + total + " bytes " + localUrl);
        } else {
            Log.d("JJJ", "Unable to query downloadmanager about downloadId " + downloadId);
        }

        c.close();

        return status;
    }

    private Context recieverContext;
    public void addListener(Context context, int podcastId, OnRadioLibraryStatusUpdatedListener listener) {
        boolean isInitialized = (listenersByPodcastId.size() != 0);
        if (!isInitialized) {
            recieverContext = context;
            context.registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)); // Create broadcast receiver for "download complete" messages
        }

        if (!listenersByPodcastId.containsKey(podcastId)) {
            listenersByPodcastId.put(podcastId, new ArrayList<OnRadioLibraryStatusUpdatedListener>()); // Create entry for podcast ID
        }

        boolean isNewListener = !listenersByPodcastId.get(podcastId).contains(listener);
        if (isNewListener) {
            listenersByPodcastId.get(podcastId).add(listener); // Add listener
            listener.OnRadioLibraryStatusUpdated(podcastId, getStatus(context, podcastId)); // Fire events to reflect current state
        }
    }

    public void removeListener(Context context, int podcastId, OnRadioLibraryStatusUpdatedListener listener) {
        if (listenersByPodcastId.containsKey(podcastId)) {
            if (listenersByPodcastId.get(podcastId).contains(listener)) {
                listenersByPodcastId.get(podcastId).remove(listener); // Remove listener
            }
        }
    }

    public void disableDownloadReceiver()
    {
        if(recieverContext != null) {
            try {
                recieverContext.unregisterReceiver(downloadCompleteReceiver);
            } catch (IllegalArgumentException e) {

            }
        }
    }


    public void resumeDownloadReceiver()
    {
        /*
        if(recieverContext != null)
        {
            recieverContext.registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
        */
    }

    // Our handler for received Intents. This will be called whenever an Intent with an action named ACTION_DOWNLOAD_COMPLETE is broadcasted.
    private BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, Storage.DOWNLOAD_ID_UNKNOWN);
            boolean isVerified = verifyDownload(context, downloadId);
            if (isVerified) {
                Log.d("JJJ", "Download complete and verified " + downloadId);
            } else {
                Log.d("JJJ", "Download complete NOT verified " + downloadId);
            }
            Log.d("JJJ", "Got message: " + DownloadManager.ACTION_DOWNLOAD_COMPLETE + " downloadId " + downloadId + " isVerified " + isVerified);

            int podcastId = Storage.get().getLibraryPodcastId(downloadId);
            if (podcastId != Storage.PODCAST_ID_UNKNOWN) {
                callback(context, podcastId); // Callback to listeners that something happened
            } else {
                Log.d("JJJ", "Download complete but no podcastId is linked to that downloadId " + downloadId + " - is settings database up to date (or are we just getting this callback from Android a little late?)");
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
     * Check if download was valid, see issue
     * http://code.google.com/p/android/issues/detail?id=18462
     * From
     * http://stackoverflow.com/questions/8937817/downloadmanager-action-download-complete-broadcast-receiver-receiving-same-downl
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
            return String.format("%d%%", (int)(100 * downloadProgress)); // "%%" is converted to "%" (secret!)
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
