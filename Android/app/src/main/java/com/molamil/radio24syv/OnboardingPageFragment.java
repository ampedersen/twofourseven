package com.molamil.radio24syv;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.molamil.radio24syv.util.DisplayUtil;

/**
 * Created by patriksvensson on 09/10/15.
 */
public class OnboardingPageFragment extends Fragment
{
    private int position = -1;
    private int mVideoWidth = 720;
    private int mVideoHeight = 720;
    private String videoPath;
    private String videoMessage;
    private VideoView videoView;
    private FrameLayout videoWrapper;
    private ImageView videoPoster;
    private TextView videoTv;
    private int posterId = 0;

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
                videoMessage = getResources().getString(R.string.onboarding_message_0);
                mVideoWidth = 718;
                posterId = R.drawable.video_poster_live;
                break;
            case 1:
                videoPath = "android.resource://" + packageName + "/"+R.raw.programmer;
                videoMessage = getResources().getString(R.string.onboarding_message_1);
                mVideoWidth = 716;
                posterId = R.drawable.video_poster_programmer;
                break;
            case 2:
                videoPath = "android.resource://" + packageName + "/"+R.raw.news;
                videoMessage = getResources().getString(R.string.onboarding_message_2);
                mVideoWidth = 714;
                posterId = R.drawable.video_poster_news;
                break;
            case 3:
                videoPath = "android.resource://" + packageName + "/"+R.raw.offline;
                videoMessage = getResources().getString(R.string.onboarding_message_3);
                mVideoWidth = 718;
                posterId = R.drawable.video_poster_offline;
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
        videoWrapper = (FrameLayout)v.findViewById(R.id.video_wrapper);
        videoPoster = (ImageView)v.findViewById(R.id.video_poster);
        videoTv = (TextView)v.findViewById(R.id.video_text);

        if(videoPoster != null)
        {
            Log.i("YUP", ""+videoPoster.getHeight());
            videoPoster.setImageResource(posterId);
        }

        if(videoTv != null)
        {
            videoTv.setText(videoMessage);
        }

        return v;
    }

    // Video control
    public void playVideo()
    {
        if(videoView != null && videoPath != null)
        {
            //int height = videoWrapper.getHeight() - DisplayUtil.dpToPx(getActivity().getApplicationContext(), 120);
            int height = videoWrapper.getHeight() - (int)getResources().getDimension(R.dimen.onboarding_bottom_margin_size);
            int width = (int)((mVideoWidth/(float)mVideoHeight) * height);

            ViewGroup.LayoutParams videoParams = videoView.getLayoutParams();
            videoParams.width = width;
            videoParams.height = height;

//            ViewGroup.LayoutParams posterParams = videoPoster.getLayoutParams();
//            posterParams.width = width;
//            posterParams.height = height;

            videoView.setVisibility(View.VISIBLE);
            videoView.setAlpha(0);

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
                                videoPoster.setAlpha(0f);
                            } else {
                                new Handler().postDelayed(this, 50);
                            }
                        }
                    });
                }
            });

            videoView.setVideoPath(videoPath);
        }
    }

    public void cleanupVideo()
    {
        if(videoView != null)
        {
            ViewGroup.LayoutParams params = videoView.getLayoutParams();
            params.width = 100;
            params.height = 100;
            
            videoView.stopPlayback();
            videoView.setVisibility(View.GONE);
            videoView.setAlpha(0);
            videoPoster.setAlpha(1f);
        }
    }

}
