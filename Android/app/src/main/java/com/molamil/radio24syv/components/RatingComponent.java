package com.molamil.radio24syv.components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molamil.radio24syv.R;
import com.molamil.radio24syv.api.RestClient;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.Response;

import static android.graphics.Typeface.DEFAULT;

/**
 * Created by anderspedersen on 18/08/16.
 */
public class RatingComponent extends LinearLayout implements View.OnClickListener {

    private LinearLayout initLayout, activeLayout, receiptLayout;
    private FrameLayout divider;
    private TextView first, second, third, fourth, fifth, currentRatingView, ratingEndString;
    private View blackBox;

    private int activeLayoutWidth;

    public void setPodcastId(int podcastId) {
        this.podcastId = podcastId;
    }

    private int podcastId;
    private float currentRating;


    public OnRatingUpdatedListener getListener() {
        return listener;
    }

    public void setListener(OnRatingUpdatedListener listener) {
        this.listener = listener;
    }

    private OnRatingUpdatedListener listener = null;

    SharedPreferences ratedPodcasts = getContext().getSharedPreferences(String.valueOf(podcastId), 0);

    public RatingComponent(Context context, int podcastId) {
        super(context);
       // this.podcastId = podcastId;
       // this.currentRatingString = currentRatingFloat;

        initializeViews(context, podcastId);

    }

    public RatingComponent(Context context, int podcastId, AttributeSet attrs) {
        super(context, attrs);
       // this.podcastId = podcastId;
        initializeViews(context, podcastId);
    }

    public RatingComponent(Context context, int podcastId, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
       // this.podcastId = podcastId;
        initializeViews(context, podcastId);
    }

    public RatingComponent(Context context) {
        super(context);
        initializeViews(context);

    }

    private void initializeViews(final Context context)
    {
        initializeViews(context, -1);
    }

    private void initializeViews(final Context context, int podcastId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_rating_component, this);

        this.podcastId = podcastId;

        Log.i("Test", "PodcastId " + this.podcastId);


    //    if(this.podcastId == -1){
    //        blackBox.setBackgroundColor(getResources().getColor(R.color.radio_gray_dark));
    //    };

        currentRatingView = (TextView) findViewById(R.id.current_rating);
        ratingEndString = (TextView) findViewById(R.id.rating_end_string);

        first = (TextView) findViewById(R.id.vote_1_string);
        first.setOnClickListener(this);
        second = (TextView) findViewById(R.id.vote_2_string);
        second.setOnClickListener(this);
        third = (TextView) findViewById(R.id.vote_3_string);
        third.setOnClickListener(this);
        fourth = (TextView) findViewById(R.id.vote_4_string);
        fourth.setOnClickListener(this);
        fifth = (TextView) findViewById(R.id.vote_5_string);
        fifth.setOnClickListener(this);

        divider = (FrameLayout) findViewById(R.id.rating_divider);
        blackBox = findViewById(R.id.black_box);

        initLayout = (LinearLayout) findViewById(R.id.init_layout);
        activeLayout = (LinearLayout) findViewById(R.id.active_layout);
        receiptLayout = (LinearLayout) findViewById(R.id.receipt_layout);

        initLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initLayout.setVisibility(GONE);
                divider.setBackgroundColor(getResources().getColor(R.color.radio_red));
                activeLayout.setVisibility(VISIBLE);
            }
        });



        if(isRated(podcastId)) {
            initLayout.setVisibility(GONE);
            activeLayout.setVisibility(GONE);
            ratingEndString.setText(R.string.rating_end_text);
            receiptLayout.setVisibility(VISIBLE);
        };


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.vote_1_string:
                setClicked(first);
                setUnclicked(second);
                setUnclicked(third);
                setUnclicked(fourth);
                setUnclicked(fifth);
                finishRating(podcastId, 1);
                break;

            case R.id.vote_2_string:
                setClicked(first);
                setClicked(second);
                setUnclicked(third);
                setUnclicked(fourth);
                setUnclicked(fifth);
                finishRating(podcastId, 2);
                break;

            case R.id.vote_3_string:
                setClicked(first);
                setClicked(second);
                setClicked(third);
                setUnclicked(fourth);
                setUnclicked(fifth);
                finishRating(podcastId, 3);
                break;

            case R.id.vote_4_string:
                setClicked(first);
                setClicked(second);
                setClicked(third);
                setClicked(fourth);
                setUnclicked(fifth);
                finishRating(podcastId, 4);
                break;

            case R.id.vote_5_string:
                setClicked(first);
                setClicked(second);
                setClicked(third);
                setClicked(fourth);
                setClicked(fifth);
                finishRating(podcastId, 5);
                break;
        }
    }



    public void setClicked(TextView view){
        view.setTextColor(getResources().getColor(R.color.white));
        view.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    public void setUnclicked(TextView view) {
        view.setTextColor(getResources().getColor(R.color.radio_gray_dark));
        view.setTypeface(DEFAULT);
    }



    private void finishRating(final int podcastId, final float vote) {
        String data = podcastId+"/"+(vote/5.0f);
        RestClient.getApi().ratePodcast(podcastId, vote/5.0f).enqueue(new Callback<HashMap<String, String>>() {

            @Override
            public void onResponse(Response<HashMap<String, String>> response) {

                if (response.body() == null) {
                    return;
                }

                String id = response.body().get("videoPodcastId");
                String ratingText = response.body().get("rating");
                if(id.equalsIgnoreCase(String.valueOf(podcastId)))
                {
                    try
                    {
                        Float rating = Float.parseFloat(ratingText);
                        updateRating(rating);
                        doAnimation();

                        if(listener != null)
                        {
                            listener.onRatingUpdated(podcastId, rating);
                        }
                    } catch (Exception e) {}
                }

                SharedPreferences.Editor editor;
                editor = ratedPodcasts.edit();
                editor.putFloat(String.valueOf(podcastId), vote);
                editor.apply();
            }

            @Override
            public void onFailure(Throwable t) {
                doAnimation();
            }
        });
    }

    public boolean isRated(int podcastId) {
        return ratedPodcasts.contains(String.valueOf(podcastId));
    }

    public void doAnimation() {


        activeLayoutWidth = activeLayout.getWidth();

        ValueAnimator anim = ValueAnimator.ofInt(blackBox.getMeasuredWidth(),activeLayoutWidth+60);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                Log.i("Test", "Val" + val);
                if(val >= activeLayoutWidth+55){
                    activeLayout.setVisibility(GONE);
                    initLayout.setVisibility(GONE);
                    receiptLayout.setVisibility(VISIBLE);
                    currentRatingView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    currentRatingView.setTextColor(getResources().getColor(R.color.white));
                    ratingEndString.setText(R.string.rating_receipt_text);
                }
                ViewGroup.LayoutParams layoutParams = blackBox.getLayoutParams();
                layoutParams.width= val;
                blackBox.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(500);
        anim.setRepeatCount(1);
        anim.setRepeatMode(2);
        anim.start();






     //   divider.setBackgroundColor(getResources().getColor(R.color.radio_gray_darker));



    }




    public void updateRating(float cr) {
        currentRating = cr;
        if(currentRating == 0) {
            currentRatingView.setText("--");
            Log.i("Test", "just set current rating to --");
        } else if (currentRating != 0) {
            currentRatingView.setText("" + String.valueOf(cr * 5));
        }
    }

    public void updateState()
    {
        blackBox.setBackgroundColor(getResources().getColor(R.color.player_background));
        if(isRated(podcastId)) {
            initLayout.setVisibility(GONE);
            activeLayout.setVisibility(GONE);
            ratingEndString.setText(R.string.rating_end_text);
            receiptLayout.setVisibility(VISIBLE);
        }
        else
        {
            initLayout.setVisibility(VISIBLE);
            activeLayout.setVisibility(GONE);
            ratingEndString.setText(R.string.rating_end_text);
            receiptLayout.setVisibility(GONE);
        }
    }

    public interface OnRatingUpdatedListener {
        void onRatingUpdated(int podcastId, float rating);
    }


}
