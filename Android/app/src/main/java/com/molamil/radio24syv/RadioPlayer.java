package com.molamil.radio24syv;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Plays audio and manages the RadioPlayerService. Supports playback of URL, standard play/pause/etc actions, multiple listener callbacks for player states.
 * Created by jens on 14/09/15.
 */
public class RadioPlayer {

    public final static int ACTION_PLAY = 0;
    public final static int ACTION_STOP = 1;
    public final static int ACTION_PAUSE = 2;
    public final static int ACTION_NEXT = 3;
    public final static int ACTION_PREVIOUS = 4;

    public static String getActionName(int action) {
        final String[] names = new String[] { "play", "stop", "pause", "next", "previous" };
        try {
            return names[action];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "";
        }
    }

//    MediaPlayer player;
    ArrayList<OnPlaybackListener> listenerList = new ArrayList<>();
    int action = ACTION_STOP;
//    String url;
//    PlayUrlTask task = null;
    Context context;
    RadioPlayerService service = null;
    boolean isBoundToService;

    public RadioPlayer(Context context) {
        Log.d("JJJ", "Create RadioPlayer - starting radio service");
        this.context = context;

        Intent i = new Intent(context, RadioPlayerService.class);
        context.startService(i); // Because the service is started using startService() it will keep running even when the host activity is destroyed. It would usually auto-cleanup because we are using bindService().
        context.bindService(i, serviceConnection, Context.BIND_AUTO_CREATE); // ServiceConnection will assign service reference to our "service" variable
    }

    public void cleanup() {
        Log.d("JJJ", "Cleanup RadioPlayer - stopping radio service");
        if (isBoundToService) {
            context.unbindService(serviceConnection);
            Intent i = new Intent(context, RadioPlayerService.class);
            context.stopService(i);
        }
    }

    public String getUrl() {
        if (isBoundToService) {
            return service.getUrl();
        } else {
            Log.d("JJJ", "Unable to get URL from service because it is not connected");
            return "";
        }
    }

    public void addListener(OnPlaybackListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);

            // Fire events to reflect current state
            if (task != null) {
                listener.OnBusy(RadioPlayer.this);
            } else if (action == ACTION_PLAY) {
                listener.OnStarted(RadioPlayer.this);
            } else if (action == ACTION_STOP) {
                listener.OnStopped(RadioPlayer.this);
            } else if (action == ACTION_PAUSE) {
                listener.OnPaused(RadioPlayer.this);
            }
        }
    }

    public void removeListener(OnPlaybackListener listener) {
        if (listenerList.contains(listener)) {
            listenerList.remove(listener);
        }
    }

    public void play(String url) {
        Log.d("JJJ", "play (was " + action + ") audioId " + (player == null ? "NULL" : player.getAudioSessionId()));

        boolean isLocal = !url.startsWith("http://");
        if (!isLocal) {
            ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // Internet is happy
            } else {
                Log.d("JJJ", "Unable to play " + url + " because internet connection is down");
                setAction(url, ACTION_STOP);
                return; // Return, internet is not happy
            }
        }

        this.url = url;
        setAction(url, ACTION_PLAY);
    }

    public void stop() {
        setAction(url, ACTION_STOP);
    }

    public void pause() {
        setAction(url, ACTION_PAUSE);
    }

    public void next() {
        setAction(url, ACTION_NEXT);
    }

    public void previous() {
        setAction(url, ACTION_PREVIOUS);
    }

    private void setAction(final String url, int newAction) {
        Log.d("JJJ", "setAction " + newAction + " (was " + action + ") + audioId " + (player == null ? "NULL" : player.getAudioSessionId()));

        if (!isActionAllowed(newAction)) {
            Log.d("JJJ", "Unable to perform action " + newAction + " because it is not allowed while doing action " + action);
            return; // Return, action is not allowed
        }

        switch (newAction) {

            case ACTION_PLAY:
                if (action == ACTION_PAUSE) {
                    if (player != null) {
                        player.start();
                        callbackStarted();
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

            case ACTION_STOP:
                if (player != null) {
                    if (player.isPlaying()) {
                        player.stop();
                    }
                    player.release();
                    player = null;
                    callbackStopped();
                }
                break;

            case ACTION_PAUSE:
                if (player != null) {
                    player.pause();
                }
                callbackPaused();
                break;

            case ACTION_NEXT:
                Log.d("JJJ", "TODO implement ACTION_NEXT");
                break;

            case ACTION_PREVIOUS:
                Log.d("JJJ", "TODO implement ACTION_PREVIOUS");
                break;
        }

        action = newAction;
    }

    private boolean isActionAllowed(int newAction) {
        if (!isBoundToService) {
            return false; // Return false, we cannot perform any action before we are connected to the service
        }

        switch (newAction) {
            case ACTION_PLAY:
                return true;
            case ACTION_STOP:
                return (action == ACTION_PLAY) || (action == ACTION_PAUSE);
            case ACTION_PAUSE:
                return (action == ACTION_PLAY);
            case ACTION_NEXT:
                return (action == ACTION_PLAY) || (action == ACTION_STOP) || (action == ACTION_PAUSE);
            case ACTION_PREVIOUS:
                return (action == ACTION_PLAY) || (action == ACTION_STOP) || (action == ACTION_PAUSE);
            default:
                return true;
        }
    }

    private class PlayUrlTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            Log.d("JJJ", urls[0]);

            callbackBusy();

            // Validate URL
            URL web;
            try {
                web = new URL(url);
            } catch (MalformedURLException e) {
                Log.d("JJJ", "Unable play URL because it is not valid " + url);
                e.printStackTrace();
                setAction(url, ACTION_STOP);
                return null; // Return, bad URL
            }

            // Prepare player
            if (player == null) {
                player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
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
                setAction(url, ACTION_STOP);
                return null; // Return, data source error
            }

            // Play URL
            if (action == ACTION_PLAY) {
                try {
                    player.prepare();
                    player.start();
                    callbackStarted();
                } catch (IOException e) {
                    Log.e("JJJ", "Unable to actually play URL because of some playback error " + url);
                    e.printStackTrace();
                    setAction(url, ACTION_STOP);
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

    private void callbackBusy() {
        for (OnPlaybackListener l : listenerList) {
            l.OnBusy(RadioPlayer.this);
        }
    }
    private void callbackStarted() {
        for (OnPlaybackListener l : listenerList) {
            l.OnStarted(RadioPlayer.this);
        }
    }

    private void callbackStopped() {
        for (OnPlaybackListener l : listenerList) {
            l.OnStopped(RadioPlayer.this);
        }
    }

    private void callbackPaused() {
        for (OnPlaybackListener l : listenerList) {
            l.OnPaused(RadioPlayer.this);
        }
    }

    public interface OnPlaybackListener {
        public void OnBusy(RadioPlayer player);
        public void OnStarted(RadioPlayer player);
        public void OnStopped(RadioPlayer player);
        public void OnPaused(RadioPlayer player);
    }

    public interface RadioPlayerProvider {
        public RadioPlayer getRadioPlayer();
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to RadioPlayerService, cast the IBinder and get RadioPlayerService instance
            RadioPlayerService.RadioPlayerServiceBinder binder = (RadioPlayerService.RadioPlayerServiceBinder) service;
            RadioPlayer.this.service = binder.getService();
            RadioPlayer.this.service.setOn
            isBoundToService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBoundToService = false;
        }
    };
}
