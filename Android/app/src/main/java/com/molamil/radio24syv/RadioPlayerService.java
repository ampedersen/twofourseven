package com.molamil.radio24syv;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.acl.NotOwnerException;

/**
 * Encapsulates MediaPlayer and makes it possible to keep playing audio even when app is sent to background.
 * Created by jens on 15/09/15.
 */
public class RadioPlayerService extends Service implements
//        MediaPlayer.OnBufferingUpdateListener,
//        MediaPlayer.OnCompletionListener,
//        MediaPlayer.OnInfoListener,
//        MediaPlayer.OnPreparedListener,
//        MediaPlayer.OnErrorListener,
//        MediaPlayer.OnSeekCompleteListener,
        AudioManager.OnAudioFocusChangeListener {

    // Broadcast IDs used when sending messages to connected clients
    public final static String BROADCAST_ID = "RadioPlayerServiceEvent";
    public final static String BROADCAST_STATE = "State";
    public final static String BROADCAST_URL = "Url";

    private final static int NOTIFICATION_ID = 1; // Lock screen notification ID. Must not be 0.

    private final IBinder binder = new RadioPlayerServiceBinder(); // Binder given to clients

    private int state = RadioPlayer.STATE_STOPPED;
    private String url = RadioPlayer.URL_UNASSIGNED;

    private MediaPlayer player;
    private int action = RadioPlayer.ACTION_STOP;
    private PlayUrlTask task = null;
    private WifiManager.WifiLock wifiLock; // Used to keeps wifi running while streaming

    @Override
    public void onCreate() {
        // The service is being created
        // TODO start as foreground + notifications on lock screen
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "playerLock");
        wifiLock.setReferenceCounted(false); // For convenience, do not keep track of how many times the lock has been required. Release it when release() is called no matter what.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        return Service.START_REDELIVER_INTENT; // If the system kills the service after onStartCommand() returns, recreate the service and call onStartCommand() with the last intent that was delivered to the service
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return binder;
    }

    @Override
    public boolean onUnbind (Intent intent) {
        Log.d("JJJ", "service onUnbind");
//        setAction(url, RadioPlayer.ACTION_STOP);
//        cleanup();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        // TODO cleanup
        Log.d("JJJ", "service onDestroy");
        cleanup();
    }

    private void cleanup() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private boolean isLocalUrl(final String url) {
        return (url == RadioPlayer.URL_UNASSIGNED) || (!url.startsWith("http://"));
    }
    private boolean isConnectedToInternetIfNeeded(final String url, int action) {
        if (action == RadioPlayer.ACTION_STOP) {
            return true; // Does not need internet
        }

        if (isLocalUrl(url)) {
            return true; // Does not need internet
        } else {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true; // Return true, internet is happy
            } else {
                Log.d("JJJ", "Unable to play " + url + " because internet connection is down");
                setAction(url, RadioPlayer.ACTION_STOP);
                return false; // Return false, internet is not happy
            }
        }
    }

    public void setAction(final String url, int newAction) {
        Log.d("JJJ", "service setAction " + RadioPlayer.getActionName(newAction) + " (was " + RadioPlayer.getActionName(action) + ") + audioId " + (player == null ? "NULL" : player.getAudioSessionId()));

        if (!isActionAllowed(newAction)) {
            Log.d("JJJ", "Unable to perform action " + newAction + " because it is not allowed while doing action " + action);
            return; // Return, action is not allowed
        }

        if (!isConnectedToInternetIfNeeded(url, newAction)) {
            Log.d("JJJ", "Unable to perform action " + newAction + " because it requires internet connection");
            return; // Return, action needs internet connection
        }

        this.url = url;

        switch (newAction) {

            case RadioPlayer.ACTION_PLAY:
                if (action == RadioPlayer.ACTION_PAUSE) {
                    if (player != null) {
                        player.start();
                        setState(RadioPlayer.STATE_STARTED);
                    }
                }
                else {
                    if (task != null) {
                        task.cancel(true);
                    }
                    task = new PlayUrlTask();
                    task.execute(url);
                }
                break;

            case RadioPlayer.ACTION_STOP:
                if (player != null) {
                    if (player.isPlaying()) {
                        player.stop();
                    }
                    player.release();
                    player = null;
                    setState(RadioPlayer.STATE_STOPPED);
                }
                break;

            case RadioPlayer.ACTION_PAUSE:
                if (player != null) {
                    player.pause();
                }
                setState(RadioPlayer.STATE_PAUSED);
                break;

            case RadioPlayer.ACTION_NEXT:
                Log.d("JJJ", "TODO implement ACTION_NEXT");
                break;

            case RadioPlayer.ACTION_PREVIOUS:
                Log.d("JJJ", "TODO implement ACTION_PREVIOUS");
                break;
        }

        action = newAction;

        updateWifiLock();
        updateRunInForeground();
    }

    private boolean isActionAllowed(int newAction) {
        switch (newAction) {
            case RadioPlayer.ACTION_PLAY:
                return true;
            case RadioPlayer.ACTION_STOP:
                return (action == RadioPlayer.ACTION_PLAY) || (action == RadioPlayer.ACTION_PAUSE);
            case RadioPlayer.ACTION_PAUSE:
                return (action == RadioPlayer.ACTION_PLAY);
            case RadioPlayer.ACTION_NEXT:
                return (action == RadioPlayer.ACTION_PLAY) || (action == RadioPlayer.ACTION_STOP) || (action == RadioPlayer.ACTION_PAUSE);
            case RadioPlayer.ACTION_PREVIOUS:
                return (action == RadioPlayer.ACTION_PLAY) || (action == RadioPlayer.ACTION_STOP) || (action == RadioPlayer.ACTION_PAUSE);
            default:
                return true;
        }
    }

    private void updateWifiLock() {
        boolean isPlayingOnlineStream = (action == RadioPlayer.ACTION_PLAY) && (!isLocalUrl(url));
        if (isPlayingOnlineStream) {
            Log.d("JJJ", "Wifi lock on");
            wifiLock.acquire();
        } else {
            Log.d("JJJ", "Wifi lock off");
            wifiLock.release();
        }
    }

    private void updateRunInForeground() {
        if (action == RadioPlayer.ACTION_PLAY) {
            Log.d("JJJ", "Run in foreground " + RadioPlayer.getActionName(action));
            // When running in the foreground, the service also must provide a status bar notification
            // to ensure that users are aware of the running service and allow them to open an activity that can interact with the service.
            String audioTitle = "Live radio"; // TODO get audio title from AudioInfo class thingy
            String audioDescription = "This is an audio description"; // TODO audio description
//            String audioInfo = null; // TODO audio info (not needed, remove it if unwanted)
            int smallIconId = R.drawable.tab_icon; // TODO icon for player state

            // Start MainActivity when notification is touched
            PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), MainActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            // Get app icon
            Bitmap largeIcon;
            try {
                largeIcon = ((BitmapDrawable) getPackageManager().getApplicationIcon(getApplicationContext().getPackageName())).getBitmap(); // Get app icon
            } catch (PackageManager.NameNotFoundException e) {
                largeIcon = null; // Null means only the action icon will be used (e.g. play symbol). This will not happen anyway, our app always exists.
                Log.d("JJJ", "Unable to get app icon because of " + e.getMessage());
                e.printStackTrace();
            }

            // Create lock screen widget thingy
            Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(audioTitle)
                    .setContentText(audioDescription)
//                    .setContentInfo(audioInfo)
                    .setSmallIcon(smallIconId)
                    .setLargeIcon(largeIcon)
                    .setOngoing(true)
                    .setShowWhen(false) // No timestamp (Android 5)
                    .setWhen(0) // No timestamp (Android 4)
                    .setVisibility(Notification.VISIBILITY_PUBLIC) // Show everywhere
                    .setPriority(Notification.PRIORITY_MAX) // Show at top in list
                    .setContentIntent(intent)
                    .build();

            startForeground(NOTIFICATION_ID, notification);
        } else {
            Log.d("JJJ", "Run in background " + RadioPlayer.getActionName(action));
            stopForeground(true); // TODO keep notification if paused?
        }
    }

    private void updateAudioFocus() {
        if (state == RadioPlayer.STATE_STARTED) {
            AudioManager a = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
            a.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    public int getState() {
        return state;
    }

    private void setState(int newState) {
        if (newState == state) {
            return; // Return, no change in state
        }

        state = newState;
        sendMessage();

        updateAudioFocus();
    }

    public String getUrl() {
        return url;
    }

    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.d("JJJ", "Audiofocus gain");
//                // resume playback
//                if (mMediaPlayer == null) initMediaPlayer();
//                else if (!mMediaPlayer.isPlaying()) mMediaPlayer.start();
//                mMediaPlayer.setVolume(1.0f, 1.0f);
                if (action == RadioPlayer.ACTION_PAUSE) {
                    setAction(url, RadioPlayer.ACTION_PLAY);
                }
                if (player != null) {
                    player.setVolume(1, 1);
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                Log.d("JJJ", "Audiofocus loss");
                // Lost focus for an unbounded amount of time: stop playback and release media player
//                if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
//                mMediaPlayer.release();
//                mMediaPlayer = null;
                if (action == RadioPlayer.ACTION_PLAY) {
                    setAction(url, RadioPlayer.ACTION_PAUSE);
                } else {
                    setAction(url, RadioPlayer.ACTION_STOP);
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.d("JJJ", "Audiofocus loss transient");
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
//                if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
                setAction(url, RadioPlayer.ACTION_PAUSE);
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.d("JJJ", "Audiofocus duck");
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
//                if (mMediaPlayer.isPlaying()) mMediaPlayer.setVolume(0.1f, 0.1f);
                if (player != null) {
                    player.setVolume(0.2f, 0.2f);
                }
                break;
        }
    }

    // Send an Intent with an action named BROADCAST_ID. The Intent sent can be received by connected clients (which is just the RadioPlayer for now).
    private void sendMessage() {
        Log.d("JJJ", "Sending message: " + RadioPlayerService.BROADCAST_STATE + " " + RadioPlayer.getStateName(state) + " " + RadioPlayerService.BROADCAST_URL + " " + url);
        Intent intent = new Intent(BROADCAST_ID);
        intent.putExtra(BROADCAST_STATE, state);
        intent.putExtra(BROADCAST_URL, url);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent); // Use LocalBroadcastManager to send messages to listeners within our own process. It is fast and we do not need to specify all sorts of intents & actions in the manifest when using this.
    }

    private class PlayUrlTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            Log.d("JJJ", urls[0]);

            setState(RadioPlayer.STATE_BUSY);
            url = urls[0];

            // Validate URL
            URL web;
            try {
                web = new URL(url);
            } catch (MalformedURLException e) {
                Log.d("JJJ", "Unable play URL because it is not valid " + url);
                e.printStackTrace();
                setAction(url, RadioPlayer.ACTION_STOP);
                return null; // Return, bad URL
            }

            // Prepare player
            if (player == null) {
                player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK); // Keep CPU awake while playback is started and release the CPU when stopped/paused
            } else {
                if (player.isPlaying()) {
                    player.stop();
                }
                player.reset(); // Reset before changing data source
            }
            player.setVolume(1, 1);
            try {
                player.setDataSource(web.toString());
            } catch (IOException e) {
                Log.e("JJJ", "Unable to play URL because of data source error " + url);
                e.printStackTrace();
                setAction(url, RadioPlayer.ACTION_STOP);
                return null; // Return, data source error
            }

            // Play URL
            if (action == RadioPlayer.ACTION_PLAY) {
                try {
                    player.prepare();
                    player.start();
                    setState(RadioPlayer.STATE_STARTED);
                } catch (IOException e) {
                    Log.e("JJJ", "Unable to play URL because of some playback error " + url);
                    e.printStackTrace();
                    setAction(url, RadioPlayer.ACTION_STOP);
                    return null; // Return, playback error
                }
            }

            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Void nothing) {
            task = null;
        }
    }

    public interface RadioPlayerServiceListener {
        public void onStateChanged();
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class RadioPlayerServiceBinder extends Binder {
        RadioPlayerService getService() {
            // Return this instance of RadioPlayerService so clients can call public methods
            return RadioPlayerService.this;
        }
    }
}
