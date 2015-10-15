package com.molamil.radio24syv;

import android.app.ActionBar;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.VideoView;

import com.molamil.radio24syv.videoView.AspectFillVideoView;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by patriksvensson on 09/10/15.
 */
public class OnboardingPageFragment extends Fragment
{
    private int position = -1;
    private int mVideoWidth = 720;
    private int mVideoHeight = 720;
    private String videoPath;
    private VideoView videoView;

    //private TextureView textureView;
    //private MediaPlayer mMediaPlayer;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position");
        String packageName = getArguments().getString("packageName");
        int videoIndex = position - 1;

        //NB! Update vW & vH when updating videos
        switch (videoIndex)
        {
            case 0:
                videoPath = "android.resource://" + packageName + "/"+R.raw.live;
                mVideoWidth = 718;
                break;
            case 1:
                videoPath = "android.resource://" + packageName + "/"+R.raw.programmer;
                mVideoWidth = 716;
                break;
            case 2:
                videoPath = "android.resource://" + packageName + "/"+R.raw.news;
                mVideoWidth = 720;
                break;
            case 3:
                videoPath = "android.resource://" + packageName + "/"+R.raw.offline;
                mVideoWidth = 718;
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int resourceID = R.layout.fragment_onboarding_video_page;
        if(position == 0)
        {
            resourceID = R.layout.fragment_onboarding_welcome_page;
        }
        else if(position == 5)
        {
            resourceID = R.layout.fragment_onboarding_main_page;
        }

        View v = inflater.inflate(resourceID, container, false);
        videoView = (VideoView)v.findViewById(R.id.video);

        return v;
    }

    // Video control
    public void playVideo()
    {
        Log.i("PS", "playVideo (called from pager)");
        //Using SurfaceView and MediaPlayer
        //surfaceView.setVisibility(View.VISIBLE);

        //Using VideoView
        if(videoView != null && videoPath != null)
        {

            //ViewGroup.LayoutParams params = videoView.getLayoutParams();
            //Log.i("PS", "videoView.getWidth(): "+videoView.getWidth());
            //Log.i("PS", "videoView.getHeight(): "+videoView.getHeight());
            //params.width = videoView.getWidth()*100;
            //params.height = videoView.getHeight();


            //videoView.setAlpha(0);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mp.setLooping(true);
                    videoView.start();
                    //HACK: Show videoview AFTER it has started playing
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            if(videoView.getCurrentPosition() != 0){
                                Log.i("PS", "video prepared, show it");
                                //View placeholder = findViewById(R.id.placeholder);
                                //placeholder.setVisibility(View.GONE);
                                videoView.setAlpha(1);
                            } else {
                                new Handler().postDelayed(this, 50);
                            }
                        }
                    });
                }
            });


            //videoView.start();
            //videoView.setVideoSize(mVideoWidth, mVideoWidth);
            videoView.setVideoPath(videoPath);
        }
    }

    public void cleanupVideo()
    {
        if(videoView != null)
        {
            //TODO: Stop and unload video
            //videoView.setVisibility(View.GONE);
            videoView.setAlpha(0);
        }
    }

}
