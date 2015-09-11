package com.molamil.radio24syv;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by jens on 11/09/15.
 */
public class MediaPlayerButton extends Button implements
        View.OnClickListener {

    public final static int ACTION_PLAY = 0;
    public final static int ACTION_STOP = 1;
    public final static int ACTION_PAUSE = 2;
    public final static int ACTION_NEXT = 3;
    public final static int ACTION_PREVIOUS = 4;

    int action;
    MediaPlayer mediaPlayer;

    public MediaPlayerButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Apply attributes from XML
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MediaPlayerButton,
                0, 0);

        try {
            action = a.getInteger(R.styleable.MediaPlayerButton_action, ACTION_PLAY);
        } finally {
            a.recycle();
        }

        setOnClickListener(this);
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void onClick(View v) {
        Log.d("JJJ", "action " + action + " mediapleyer " + mediaPlayer);
        if (mediaPlayer == null) {
            return;
        }

        switch (action) {
            case ACTION_PLAY:
                mediaPlayer.start();
                break;
            case ACTION_STOP:
                mediaPlayer.stop();
                break;
            case ACTION_PAUSE:
                mediaPlayer.pause();
                break;
            case ACTION_NEXT:
                break;
            case ACTION_PREVIOUS:
                break;
        }
    }
}
