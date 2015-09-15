package com.molamil.radio24syv;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
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
    private boolean isAvailable = true;

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

        if (!isAvailable()) {
            Log.d("JJJ", "Unable to click button (id " + getId() + ") because it is not available");
            return; // Return, cannot click button
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


    static Paint greenPaint = null;
    static Paint redPaint = null;

    @Override
    protected void onDraw(Canvas canvas) {
        //setEnabled(isAvailable()); // Disable if action is not available due to the current state of the player

        if (greenPaint == null) {
            greenPaint = new Paint();
            greenPaint.setTextSize(12 * getResources().getDisplayMetrics().scaledDensity);
            greenPaint.setColor(getResources().getColor(android.R.color.holo_green_light));
        }
        if (redPaint == null) {
            redPaint = new Paint();
            redPaint.setTextSize(12 * getResources().getDisplayMetrics().scaledDensity);
            redPaint.setColor(getResources().getColor(android.R.color.holo_red_light));
        }

        Paint p;
        if (isAvailable()) {
            p = greenPaint;
        } else {
            p = redPaint;
        }
        drawCenter(canvas, p, RadioPlayer.getActionName(action)); // Draw center(ish)

        super.onDraw(canvas);
    }

    // http://stackoverflow.com/a/32081250
    private void drawCenter(Canvas canvas, Paint paint, String text) {
        int cHeight = canvas.getClipBounds().height();
        int cWidth = canvas.getClipBounds().width();
        Rect r = new Rect();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, paint);
    }

    @Override
    public void OnBusy(RadioPlayer player) {
        setIsAvailable(false);
    }

    @Override
    public void OnStarted(RadioPlayer player) {
        switch (action) {
            case RadioPlayer.ACTION_PLAY:
                boolean isPlayingMyUrl = (player.url.equals(url));
                setIsAvailable(!isPlayingMyUrl); // Enable if not playing our stream
                break;
            case RadioPlayer.ACTION_STOP:
                setIsAvailable(true);
                break;
            case RadioPlayer.ACTION_PAUSE:
                setIsAvailable(true);
                break;
            case RadioPlayer.ACTION_NEXT:
                setIsAvailable(true);
                break;
            case RadioPlayer.ACTION_PREVIOUS:
                setIsAvailable(true);
                break;
        }
    }

    @Override
    public void OnStopped(RadioPlayer player) {
        switch (action) {
            case RadioPlayer.ACTION_PLAY:
                setIsAvailable(true);
                break;
            case RadioPlayer.ACTION_STOP:
                setIsAvailable(false);
                break;
            case RadioPlayer.ACTION_PAUSE:
                setIsAvailable(false);
                break;
            case RadioPlayer.ACTION_NEXT:
                setIsAvailable(true);
                break;
            case RadioPlayer.ACTION_PREVIOUS:
                setIsAvailable(true);
                break;
        }
    }

    @Override
    public void OnPaused(RadioPlayer player) {
        switch (action) {
            case RadioPlayer.ACTION_PLAY:
                setIsAvailable(true);
                break;
            case RadioPlayer.ACTION_STOP:
                setIsAvailable(true);
                break;
            case RadioPlayer.ACTION_PAUSE:
                setIsAvailable(false);
                break;
            case RadioPlayer.ACTION_NEXT:
                setIsAvailable(true);
                break;
            case RadioPlayer.ACTION_PREVIOUS:
                setIsAvailable(true);
                break;
        }
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
        postInvalidate(); // Redraw view next frame
    }
}
