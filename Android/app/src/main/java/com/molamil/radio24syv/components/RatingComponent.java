package com.molamil.radio24syv.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
                Log.i("Test", String.valueOf(this.podcastId));
                finishRating(this.podcastId, 1);
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


    // Method to handle completing the rating flow.
    // It does some "animation", writes the rating to the users RatedPodcasts and submits the actual rating

    private void finishRating(final int podcastId, final float vote) {
        Log.i("PS", "rate");
        String data = podcastId+"/"+(vote/5.0f);
        RestClient.getApi().ratePodcast(podcastId, vote/5.0f).enqueue(new Callback<HashMap<String, String>>() {

            @Override
            public void onResponse(Response<HashMap<String, String>> response) {
                activeLayout.setVisibility(GONE);
                divider.setBackgroundColor(getResources().getColor(R.color.radio_gray_darker));
                initLayout.setVisibility(GONE);
                receiptLayout.setVisibility(VISIBLE);
                currentRatingView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                currentRatingView.setTextColor(getResources().getColor(R.color.white));
                ratingEndString.setText(R.string.rating_receipt_text);

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
                activeLayout.setVisibility(GONE);
                divider.setBackgroundColor(getResources().getColor(R.color.radio_gray_darker));
                initLayout.setVisibility(GONE);
                receiptLayout.setVisibility(VISIBLE);
                currentRatingView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                currentRatingView.setTextColor(getResources().getColor(R.color.white));
                ratingEndString.setText(R.string.rating_receipt_text);
            }
        });
    }

    public boolean isRated(int podcastId) {
        return ratedPodcasts.contains(String.valueOf(podcastId));
    }

    public void setUnclicked(TextView view) {
        view.setTextColor(getResources().getColor(R.color.radio_gray_dark));
        view.setTypeface(DEFAULT);
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
