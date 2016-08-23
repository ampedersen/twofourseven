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

import static android.graphics.Typeface.DEFAULT;

/**
 * Created by anderspedersen on 18/08/16.
 */
public class RatingComponent extends LinearLayout implements View.OnClickListener {

    private LinearLayout initLayout, activeLayout, receiptLayout;
    private FrameLayout divider;
    private TextView first, second, third, fourth, fifth, currentRatingView, ratingEndString;
    private int podcastId;
    private float currentRating;

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

    private void finishRating(int podcastId, final float vote) {
        final Handler receipt = new Handler();
        receipt.postDelayed(new Runnable() {
            @Override
            public void run() {
                activeLayout.setVisibility(GONE);
                divider.setBackgroundColor(getResources().getColor(R.color.radio_gray_darker));
                initLayout.setVisibility(GONE);
                receiptLayout.setVisibility(VISIBLE);
                currentRatingView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                currentRatingView.setTextColor(getResources().getColor(R.color.white));
                ratingEndString.setText(R.string.rating_receipt_text);
                updateRating(vote/5);

            }
        }, 2000);

        final Handler end = new Handler();
        end.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentRatingView.setTextColor(getResources().getColor(R.color.radio_gray_dark));

            }
        }, 4000);

        SharedPreferences.Editor editor;
        editor = ratedPodcasts.edit();
        editor.putFloat(String.valueOf(podcastId), vote);
        editor.apply();


        Log.i("Test", "I made it here! And this is my podcastId:" + podcastId);



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
}
