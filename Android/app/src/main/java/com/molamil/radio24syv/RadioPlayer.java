package com.molamil.radio24syv;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
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

        this.url = url;
        if (action == ACTION_PAUSE) {
            if (player != null) {
                action = ACTION_PLAY;
                player.start();
                for (OnPlaybackListener l : listenerList) {
                    l.OnStarted(RadioPlayer.this);
                }
            }
        } else {
            action = ACTION_PLAY;
            if (task == null) {
                task = new PlayUrlTask();
                task.execute(url);
            } else {
                Log.d("JJJ", "Unable to play " + url + " because already trying to play " + this.url);
            }

//            URL web;
//            try {
//                web = new URL(url);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//                web = null;
//            }
//            if (web != null) {
//                try {
//                    Log.d("JJJ", web.toString());
//                    Log.d("JJJ", web.getContent().toString());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            try {
//                player.setDataSource(web.toString());
//            } catch (IOException e) {
//                Log.e("JJJ", "Unable to play URL " + url);
//                e.printStackTrace();
//                action = ACTION_STOP;
//                for (OnPlaybackListener l : listenerList) {
//                    l.OnStopped(RadioPlayer.this);
//                }
//                player.release();
//                player = null;
//            }
//
//            if (action == ACTION_PLAY) {
//                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        if (action == ACTION_PLAY) {
//                            player.start(); // Start playing if that is still the action we want to perform
//                            for (OnPlaybackListener l : listenerList) {
//                                l.OnStarted(RadioPlayer.this);
//                            }
//                        }
//                    }
//                });
//
//                for (OnPlaybackListener l : listenerList) {
//                    l.OnBusy(RadioPlayer.this);
//                }
//                player.prepareAsync();
//            }
        }
    }

    public void stop() {
        Log.d("JJJ", "stop (was \" + action + \") audioId " + (player == null ? "NULL" : player.getAudioSessionId()));

        if (action == ACTION_PLAY) {
            if (player != null) {
                player.stop();
                action = ACTION_PAUSE;
            }
            for (OnPlaybackListener l : listenerList) {
                l.OnStopped(RadioPlayer.this);
            }
        }
    }

    public void pause() {
        Log.d("JJJ", "pause (was \" + action + \") audioId " + (player == null ? "NULL" : player.getAudioSessionId()));

        if (action == ACTION_PLAY) {
            if (player != null) {
                player.pause();
                action = ACTION_PAUSE;
            }
            for (OnPlaybackListener l : listenerList) {
                l.OnPaused(RadioPlayer.this);
            }
        }
    }

    private class PlayUrlTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            Log.d("JJJ", urls[0]);

            for (OnPlaybackListener l : listenerList) {
                l.OnBusy(RadioPlayer.this);
            }

            if (player == null) {
                player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

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
                }
            }

            try {
                player.setDataSource(web.toString());
            } catch (IOException e) {
                Log.e("JJJ", "Unable to play URL " + url);
                e.printStackTrace();
                action = ACTION_STOP;
                for (OnPlaybackListener l : listenerList) {
                    l.OnStopped(RadioPlayer.this);
                }
                player.release();
                player = null;
            }

            if (action == ACTION_PLAY) {
                //Only for prepareAsync i think...
//                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        if (action == ACTION_PLAY) {
//                            player.start(); // Start playing if that is still the action we want to perform
//                            for (OnPlaybackListener l : listenerList) {
//                                l.OnStarted(RadioPlayer.this);
//                            }
//                        }
//                    }
//                });

                try {
                    player.prepare();
                    if (action == ACTION_PLAY) {
                        player.start();
                        for (OnPlaybackListener l : listenerList) {
                            l.OnStarted(RadioPlayer.this);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    action = ACTION_STOP;
                    for (OnPlaybackListener l : listenerList) {
                        l.OnStopped(RadioPlayer.this);
                    }
                    player.release();
                    player = null;
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
