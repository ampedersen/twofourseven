package com.molamil.radio24syv;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

/**
 * Encapsulates MediaPlayer and keeps playing audio even when app is sent to background.
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

    String url;
    private final IBinder binder = new RadioPlayerServiceBinder(); // Binder given to clients
    private RadioPlayerServiceListener listener = null;

    @Override
    public void onCreate() {
        // The service is being created
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

    public String getUrl() {
        return null;
    }

    public void setListener(RadioPlayerServiceListener listener) {
        this.listener = listener;
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
