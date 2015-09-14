package com.molamil.radio24syv;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jens on 14/09/15.
 */
public class RadioPlayer {

    public final static int ACTION_PLAY = 0;
    public final static int ACTION_STOP = 1;
    public final static int ACTION_PAUSE = 2;
    public final static int ACTION_NEXT = 3;
    public final static int ACTION_PREVIOUS = 4;

    public final static String URL_LIVE_RADIO = "http://streaming.radio24syv.dk/nice"; //TODO fetch live radio url from somewhere?
    MediaPlayer player;
    ArrayList<OnPlaybackListener> listenerList = new ArrayList<>();
    int action = -1;
    String url;
    PlayUrlTask task = null;
    Context context;

    public RadioPlayer(Context context) {
        this.context = context;
    }

    public void addListener(OnPlaybackListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
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
                setAction(ACTION_STOP);
                return; // Return, internet is not happy
            }
        }

        this.url = url;
        setAction(ACTION_PLAY);
    }

    public void stop() {
        setAction(ACTION_STOP);
    }

    public void pause() {
        setAction(ACTION_PAUSE);
    }

    public void next() {
        setAction(ACTION_NEXT);
    }

    public void previous() {
        setAction(ACTION_PREVIOUS);
    }

    private void setAction(int newAction) {
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
                        for (OnPlaybackListener l : listenerList) {
                            l.OnStarted(RadioPlayer.this);
                        }
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
                    for (OnPlaybackListener l : listenerList) {
                        l.OnStopped(RadioPlayer.this);
                    }
                }
                break;

            case ACTION_PAUSE:
                if (player != null) {
                    player.pause();
                }
                for (OnPlaybackListener l : listenerList) {
                    l.OnPaused(RadioPlayer.this);
                }
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
        switch (newAction) {
            case ACTION_PLAY:
                return true;
            case ACTION_STOP:
                return (action == ACTION_PLAY);
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

            for (OnPlaybackListener l : listenerList) {
                l.OnBusy(RadioPlayer.this);
            }

            // Validate URL
            URL web;
            try {
                web = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                web = null;
            }
            if (web != null) {
                try {
                    Log.d("JJJ", web.toString());
                    Log.d("JJJ", web.getContent().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    web = null;
                }
            }
            if (web == null) {
                Log.d("JJJ", "Unable play URL because it is not valid " + url);
                setAction(ACTION_STOP);
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
                player.reset(); // Reset before assigning a new data source
            }
            try {
                player.setDataSource(web.toString());
            } catch (IOException e) {
                Log.e("JJJ", "Unable to play URL because of data source error " + url);
                e.printStackTrace();
                setAction(ACTION_STOP);
                return null; // Return, data source error
            }

            // Play URL
            if (action == ACTION_PLAY) {
                try {
                    player.prepare();
                    player.start();
                    for (OnPlaybackListener l : listenerList) {
                        l.OnStarted(RadioPlayer.this);
                    }
                } catch (IOException e) {
                    Log.e("JJJ", "Unable to actually play URL because of some playback error " + url);
                    e.printStackTrace();
                    setAction(ACTION_STOP);
                    return null; // Return, playback error
                }
            }

            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
//        @Override
//        protected void onPostExecute(Void nothing) {
//            if (action == ACTION_PLAY) {
//            }
//        }
    }

    public interface OnPlaybackListener {
        public void OnBusy(RadioPlayer player);
        public void OnStarted(RadioPlayer player);
        public void OnStopped(RadioPlayer player);
        public void OnPaused(RadioPlayer player);
    }
}
