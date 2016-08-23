package com.molamil.radio24syv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.molamil.radio24syv.R;
import com.molamil.radio24syv.components.RatingComponent;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.storage.RadioLibrary;
import com.molamil.radio24syv.storage.model.PodcastInfo;

/**
 * Created by jens on 21/09/15.
 */
public class PodcastEpisodeView extends LinearLayout implements
    RadioLibrary.OnRadioLibraryStatusUpdatedListener {

    public enum Size { UNASSIGNED, CONTRACTED, EXPANDED }
    private Size size = Size.UNASSIGNED;
    private OnPodcastEpisodeViewUpdatedListener listener = null;

    protected PodcastInfo podcast;
    private String podcastUrl;



    public PodcastEpisodeView(Context context) {
        super(context);
        initializeViews(context);
    }

    public PodcastEpisodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public PodcastEpisodeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(final Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_podcast_episode, this);

        View topLayout = findViewById(R.id.top_layout);
        topLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (size == Size.CONTRACTED) {
                    setSize(Size.EXPANDED); // Toggle size
                } else {
                    setSize(Size.CONTRACTED);
                }
            }
        });


        View contractedLayout = findViewById(R.id.contracted_layout);
        contractedLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setSize(Size.EXPANDED);
            }
        });


        // Download button is a child of this and the clicks interfere with each other if expanded_layout is assigned a click handler. Instead assign one for each of expanded_layout's other children.
//        View expandedLayout = findViewById(R.id.expanded_layout);
//        expandedLayout.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setSize(Size.CONTRACTED);
//            }
//        });

        View contractButton = findViewById(R.id.contract_button);
        contractButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setSize(Size.CONTRACTED);
            }
        });

        TextView descriptionText = (TextView) findViewById(R.id.description_text);
        descriptionText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setSize(Size.CONTRACTED);
            }
        });


        setSize(Size.CONTRACTED);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // This is never called when instantiating with: myView = new MyView();
        // It is only called when instantiating in XML: <com.bla.bla.MyView />
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
        if (podcast != null) {
            RadioLibrary.getInstance().removeListener(getContext(), podcast.getPodcastId(), this);
        }
    }

    public void setPodcast(PodcastInfo podcast) {
        this.podcast = podcast;
        this.podcastUrl = RadioLibrary.getUrl(getContext(), podcast.getAudioUrl());


        TextView titleText = (TextView) findViewById(R.id.name_text);
        titleText.setText(podcast.getTitle());
        TextView descriptionText = (TextView) findViewById(R.id.description_text);
        descriptionText.setText(podcast.getDescription());
        TextView currentRatingTv = (TextView) findViewById(R.id.current_rating);

        RadioPlayerButton playButton = (RadioPlayerButton) findViewById(R.id.play_button);
        playButton.setUrl(podcast.getAudioUrl());
        playButton.setTitle(podcast.getTitle());
        playButton.setDescription(podcast.getDescription());
        playButton.setPlayListType(RadioPlayer.PLAYLIST_PODCAST);
        playButton.setProgramId(podcast.getProgramId());
        playButton.setRating(podcast.getRating());


        // Connect to layout, initialize rating component, update rating and finally addView to the
        // container.
        LinearLayout ratingContainer = (LinearLayout) findViewById(R.id.rating_container);
        RatingComponent ratingComponent = new RatingComponent(getContext(), podcast.getPodcastId());
        if(podcast.getRating() == "" || podcast.getRating() == null){
            ratingComponent.updateRating(0);
            } else {
                ratingComponent.updateRating(Float.parseFloat(podcast.getRating()));
            }
        ratingContainer.addView(ratingComponent);


        // Listen for download updates for this podcast ID
        RadioLibrary.getInstance().addListener(getContext(), podcast.getPodcastId(), this);
    }

    public void setPlayable(boolean playable)
    {
        RadioPlayerButton playButton = (RadioPlayerButton) findViewById(R.id.play_button);
        playButton.setVisibility(playable ? VISIBLE : GONE);

        ProgressBar activity = (ProgressBar) findViewById(R.id.download_activity_indicator);
        activity.setVisibility(playable ? GONE : VISIBLE);
        activity.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.radio_red), android.graphics.PorterDuff.Mode.SRC_ATOP); // SRC_ATOP works on Android 5, MULTIPLY makes the spinner invisible for some reason

    }

    public void setRadioPlayer(RadioPlayer player) {
        RadioPlayerButton playButton = (RadioPlayerButton) findViewById(R.id.play_button);
        playButton.setRadioPlayer(player);
    }

    public void setSize(Size size) {
        Log.i("PS", "setSize "+size);
        if (this.size == size) {
            Log.i("PS", "same size, return");
            return; // Return, same value
        }

        this.size = size;

        View contractedLayout = findViewById(R.id.contracted_layout);
        View expandedLayout = findViewById(R.id.expanded_layout);
        if (size == Size.CONTRACTED) {
            Log.i("PS", "contract");
            contractedLayout.setVisibility(View.VISIBLE);
            expandedLayout.setVisibility(View.GONE);
        } else {
            Log.i("PS", "expand");
            contractedLayout.setVisibility(View.GONE);
            expandedLayout.setVisibility(View.VISIBLE);
        }

        if (listener != null) {
            listener.onPodcastEpisodeViewSizeChanged(this, size);
        }
    }

    @Override
    public void OnRadioLibraryStatusUpdated(int podcastId, RadioLibrary.Status status) {


        ImageView downloadedImage = (ImageView) findViewById(R.id.downloaded_image);

        View downloadButton = findViewById(R.id.download_button);
        //TextView downloadButtonImage = (TextView) findViewById(R.id.download_button_image);
        TextView downloadButtonText = (TextView) findViewById(R.id.download_button_text);
        RadioPlayerButton playButton = (RadioPlayerButton) findViewById(R.id.play_button);
        ImageView downloadButtonImage = (ImageView) findViewById(R.id.download_icon);

        switch (status.getDownloadStatus()) {

            case RadioLibrary.DOWNLOAD_STATUS_UNKNOWN:
                downloadButtonImage.setImageResource(R.drawable.button_download);
                downloadedImage.setImageResource(R.drawable.icon_download_small);
                //downloadButtonImage.setText(downloadedImage.getText());
                downloadButtonText.setText(R.string.download_podcast);
                downloadButton.setOnClickListener(downloadOnClick);
                playButton.setUrl(podcastUrl);
                break;

            case RadioLibrary.DOWNLOAD_STATUS_FAILED:
                downloadButtonImage.setImageResource(R.drawable.button_download);
                downloadedImage.setImageResource(R.drawable.icon_download_small);
                //downloadButtonImage.setText(downloadedImage.getText());
                downloadButtonText.setText(R.string.download_podcast);
                downloadButton.setOnClickListener(downloadOnClick);
                playButton.setUrl(podcastUrl);
                break;

            case RadioLibrary.DOWNLOAD_STATUS_PAUSED:
                downloadButtonImage.setImageResource(R.drawable.button_garbage_can);
                downloadedImage.setImageResource(R.drawable.icon_download_small);
                //downloadButtonImage.setText(downloadedImage.getText());
                downloadButtonText.setText(R.string.remove_podcast);
                downloadButton.setOnClickListener(removeOnClick);
                playButton.setUrl(podcastUrl);
                break;

            case RadioLibrary.DOWNLOAD_STATUS_PENDING:
                downloadButtonImage.setImageResource(R.drawable.button_garbage_can);
                downloadedImage.setImageResource(R.drawable.icon_download_small);
                //downloadButtonImage.setText(downloadedImage.getText());
                downloadButtonText.setText(R.string.remove_podcast);
                downloadButton.setOnClickListener(removeOnClick);
                playButton.setUrl(podcastUrl);
                break;

            case RadioLibrary.DOWNLOAD_STATUS_RUNNING:
                downloadButtonImage.setImageResource(R.drawable.button_garbage_can);
                downloadedImage.setImageResource(R.drawable.icon_download_small);
                //downloadButtonImage.setText(downloadedImage.getText());
                downloadButtonText.setText(R.string.remove_podcast);
                downloadButton.setOnClickListener(removeOnClick);
                playButton.setUrl(podcastUrl);
                break;

            case RadioLibrary.DOWNLOAD_STATUS_SUCCESSFUL:
                downloadButtonImage.setImageResource(R.drawable.button_garbage_can);
                downloadedImage.setImageResource(R.drawable.icon_download_checked_small);
                //downloadButtonImage.setText(downloadedImage.getText());
                downloadButtonText.setText(R.string.remove_podcast);
                downloadButton.setOnClickListener(removeOnClick);
                playButton.setUrl(status.getLocalPodcastUrl()); // Play local file

                setPlayable(true);

                //remove listener, it is no longer needed?
                if (podcast != null) {
                    RadioLibrary.getInstance().removeListener(getContext(), podcast.getPodcastId(), this);
                }

                break;
        }
   }

    private final OnClickListener downloadOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            listener.onPodcastEpisodeViewDownloadClicked(PodcastEpisodeView.this, podcast.getPodcastId());
        }
    };

    private final OnClickListener removeOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            listener.onPodcastEpisodeViewRemoveClicked(PodcastEpisodeView.this, podcast);
        }
    };

    public void setOnPodcastEpisodeViewUpdatedListener(OnPodcastEpisodeViewUpdatedListener listener) {
        this.listener = listener;
    }

    public interface OnPodcastEpisodeViewUpdatedListener {
        void onPodcastEpisodeViewSizeChanged(PodcastEpisodeView view, Size size);
        void onPodcastEpisodeViewDownloadClicked(PodcastEpisodeView view, int podcastId);
        void onPodcastEpisodeViewRemoveClicked(PodcastEpisodeView view, PodcastInfo podcast);
    }
}