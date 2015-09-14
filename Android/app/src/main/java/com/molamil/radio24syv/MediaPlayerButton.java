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
        View.OnClickListener,
        RadioPlayer.OnPlaybackListener {

    int action;
    String url;
    RadioPlayer player;

    public MediaPlayerButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Apply attributes from XML
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MediaPlayerButton,
                0, 0);

        try {
            action = a.getInteger(R.styleable.MediaPlayerButton_action, RadioPlayer.ACTION_PLAY);
            url = a.getString(R.styleable.MediaPlayerButton_url);
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

    public void setRadioPlayer(RadioPlayer player) {
        this.player = player;
        player.addListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // View is now attached
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // View is now detached, and about to be destroyed
        if (player != null) {
            player.removeListener(this);
            player = null;
        }
    }

    @Override
    public void onClick(View v) {
        Log.d("JJJ", "action " + action + " player " + player);
        if (player == null) {
            return;
        }

        switch (action) {
            case RadioPlayer.ACTION_PLAY:
                player.play(url);
                break;
            case RadioPlayer.ACTION_STOP:
                player.stop();
                break;
            case RadioPlayer.ACTION_PAUSE:
                player.pause();
                break;
            case RadioPlayer.ACTION_NEXT:
                break;
            case RadioPlayer.ACTION_PREVIOUS:
                break;
        }
    }

    @Override
    public void OnBusy(RadioPlayer player) {

    }

    @Override
    public void OnStarted(RadioPlayer player) {

    }

    @Override
    public void OnStopped(RadioPlayer player) {

    }

    @Override
    public void OnPaused(RadioPlayer player) {

    }
}
