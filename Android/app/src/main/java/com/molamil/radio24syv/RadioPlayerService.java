package com.molamil.radio24syv;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        // TODO cleanup
    }

    private boolean isLocalUrl(final String url) {
        return !url.startsWith("http://");
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
        Log.d("JJJ", "setAction " + newAction + " (was " + action + ") + audioId " + (player == null ? "NULL" : player.getAudioSessionId()));

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

    public int getState() {
        return state;
    }

    private void setState(int newState) {
        if (newState == state) {
            return; // Return, no change in state
        }

        state = newState;
        sendMessage();
    }

    public String getUrl() {
        return url;
    }

    public void onAudioFocusChange(int focusChange) {
//        switch (focusChange) {
//            case AudioManager.AUDIOFOCUS_GAIN:
//                // resume playback
//                if (mMediaPlayer == null) initMediaPlayer();
//                else if (!mMediaPlayer.isPlaying()) mMediaPlayer.start();
//                mMediaPlayer.setVolume(1.0f, 1.0f);
//                break;
//
//            case AudioManager.AUDIOFOCUS_LOSS:
//                // Lost focus for an unbounded amount of time: stop playback and release media player
//                if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
//                mMediaPlayer.release();
//                mMediaPlayer = null;
//                break;
//
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                // Lost focus for a short time, but we have to stop
//                // playback. We don't release the media player because playback
//                // is likely to resume
//                if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
//                break;
//
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                // Lost focus for a short time, but it's ok to keep playing
//                // at an attenuated level
//                if (mMediaPlayer.isPlaying()) mMediaPlayer.setVolume(0.1f, 0.1f);
//                break;
//        }
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
            try {
                player.setDataSource(web.toString());
            } catch (IOException e) {
                Log.e("JJJ", "Unable to play URL because of data source error " + url);
                e.printStackTrace();
                setAction(url, RadioPlayer.ACTION_STOP);
                return null; // Return, data source error
            }

            // Keep wifi awake if streaming
            if (isLocalUrl(url)) {
                wifiLock.release();
            } else {
                wifiLock.acquire();
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
