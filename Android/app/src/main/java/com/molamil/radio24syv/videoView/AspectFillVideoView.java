package com.molamil.radio24syv.videoView;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.VideoView;

/**
 * Created by patriksvensson on 15/10/15.
 */
public class AspectFillVideoView extends VideoView
{

    private int mVideoWidth;
    private int mVideoHeight;


    public AspectFillVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectFillVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AspectFillVideoView(Context context) {
        super(context);
    }

    public void setVideoSize(int width, int height)
    {
        mVideoWidth = width;
        mVideoHeight = height;
    }


    /*
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Log.i("@@@", "onMeasure");
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            if (mVideoWidth * height > width * mVideoHeight) {
                // Log.i("@@@", "image too tall, correcting");
                height = width * mVideoHeight / mVideoWidth;
            } else if (mVideoWidth * height < width * mVideoHeight) {
                // Log.i("@@@", "image too wide, correcting");
                width = height * mVideoWidth / mVideoHeight;
            } else {
                // Log.i("@@@", "aspect ratio is correct: " +
                // width+"/"+height+"="+
                // mVideoWidth+"/"+mVideoHeight);
            }
        }

        setMeasuredDimension(width, height);
    }
    */

    /*
    @Override
    public void setVideoPath(String path)
    {
//        //Get size of video
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(path);
//        int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
//        int height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
//        retriever.release();
//
//        Log.i("PS", width + " x " + height);

        super.setVideoPath(path);
    }
    */
}
