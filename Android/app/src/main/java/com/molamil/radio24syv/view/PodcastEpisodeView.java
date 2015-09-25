package com.molamil.radio24syv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molamil.radio24syv.R;
import com.molamil.radio24syv.RadioLibrary;
import com.molamil.radio24syv.RadioPlayer;

/**
 * Created by jens on 21/09/15.
 */
public class PodcastEpisodeView extends LinearLayout implements
    RadioLibrary.OnRadioLibraryStatusUpdatedListener {

    public enum Size { UNASSIGNED, CONTRACTED, EXPANDED }
    private Size size = Size.UNASSIGNED;
    private OnPodcastEpisodeViewUpdatedListener listener = null;

    private String title;
    private String podcastUrl;
    private int podcastId;

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

//        View expandButton = findViewById(R.id.contracted_layout);
//        expandButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setSize(Size.EXPANDED);
//            }
//        });
//
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
        RadioLibrary.getInstance().removeListener(podcastId, this);
    }

    public void setTitle(String title) {
        this.title = title;
        TextView titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText(title);
    }

    public void setDescription(String description) {
        TextView descriptionText = (TextView) findViewById(R.id.description_text);
        descriptionText.setText(description);
    }

    public void setPodcastIdAndUrl(int podcastId, String podcastUrl) {
        this.podcastId = podcastId;
        this.podcastUrl = podcastUrl;
        RadioLibrary.getInstance().addListener(getContext(), podcastId, this); // Listen for updates for this podcast ID
    }

    public void setRadioPlayer(RadioPlayer player) {
        RadioPlayerButton playButton = (RadioPlayerButton) findViewById(R.id.play_button);
        playButton.setRadioPlayer(player);
    }

    public void setSize(Size size) {
        if (this.size == size) {
            return; // Return, same value
        }

        this.size = size;

        View contractedLayout = findViewById(R.id.contracted_layout);
        View expandedLayout = findViewById(R.id.expanded_layout);
        if (size == Size.CONTRACTED) {
            contractedLayout.setVisibility(View.VISIBLE);
            expandedLayout.setVisibility(View.GONE);
        } else {
            contractedLayout.setVisibility(View.GONE);
            expandedLayout.setVisibility(View.VISIBLE);
        }

        if (listener != null) {
            listener.onPodcastEpisodeViewSizeChanged(this, size);
        }
    }

    @Override
    public void OnRadioLibraryStatusUpdated(int podcastId, RadioLibrary.Status status) {
        TextView downloadedImage = (TextView) findViewById(R.id.downloaded_image);
        View downloadButton = findViewById(R.id.download_button);
        TextView downloadButtonImage = (TextView) findViewById(R.id.download_button_image);
        TextView downloadButtonText = (TextView) findViewById(R.id.download_button_text);
        RadioPlayerButton playButton = (RadioPlayerButton) findViewById(R.id.play_button);

        switch (status.getDownloadStatus()) {

            case RadioLibrary.DOWNLOAD_STATUS_UNKNOWN:
                downloadedImage.setText("Ikke hentet");
                downloadButtonImage.setText(downloadedImage.getText());
                downloadButtonText.setText(R.string.download_podcast);
                downloadButton.setOnClickListener(downloadOnClick);
                playButton.setUrl(podcastUrl);
                break;

            case RadioLibrary.DOWNLOAD_STATUS_FAILED:
                downloadedImage.setText("Kunne ikke hente");
                downloadButtonImage.setText(downloadedImage.getText());
                downloadButtonText.setText(R.string.download_podcast);
                downloadButton.setOnClickListener(downloadOnClick);
                playButton.setUrl(podcastUrl);
                break;

            case RadioLibrary.DOWNLOAD_STATUS_PAUSED:
                downloadedImage.setText("Henter");
                downloadButtonImage.setText(downloadedImage.getText());
                downloadButtonText.setText(R.string.remove_podcast);
                downloadButton.setOnClickListener(removeOnClick);
                playButton.setUrl(podcastUrl);
                break;

            case RadioLibrary.DOWNLOAD_STATUS_PENDING:
                downloadedImage.setText("Henter");
                downloadButtonImage.setText(downloadedImage.getText());
                downloadButtonText.setText(R.string.remove_podcast);
                downloadButton.setOnClickListener(removeOnClick);
                playButton.setUrl(podcastUrl);
                break;

            case RadioLibrary.DOWNLOAD_STATUS_RUNNING:
                downloadedImage.setText("Henter");
                downloadButtonImage.setText(downloadedImage.getText());
                downloadButtonText.setText(R.string.remove_podcast);
                downloadButton.setOnClickListener(removeOnClick);
                playButton.setUrl(podcastUrl);
                break;

            case RadioLibrary.DOWNLOAD_STATUS_SUCCESSFUL:
                downloadedImage.setText("Hentet");
                downloadButtonImage.setText(downloadedImage.getText());
                downloadButtonText.setText(R.string.remove_podcast);
                downloadButton.setOnClickListener(removeOnClick);
                playButton.setUrl(status.getLocalPodcastUrl()); // Play local file
                break;
        }
   }

    private final OnClickListener downloadOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            listener.onPodcastEpisodeViewDownloadClicked(PodcastEpisodeView.this, podcastId);
        }
    };

    private final OnClickListener removeOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            listener.onPodcastEpisodeViewRemoveClicked(PodcastEpisodeView.this, podcastId);
        }
    };

    public void setOnPodcastEpisodeViewUpdatedListener(OnPodcastEpisodeViewUpdatedListener listener) {
        this.listener = listener;
    }

    public interface OnPodcastEpisodeViewUpdatedListener {
        void onPodcastEpisodeViewSizeChanged(PodcastEpisodeView view, Size size);
        void onPodcastEpisodeViewDownloadClicked(PodcastEpisodeView view, int podcastId);
        void onPodcastEpisodeViewRemoveClicked(PodcastEpisodeView view, int podcastId);
    }
}