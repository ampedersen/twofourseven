package com.molamil.radio24syv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molamil.radio24syv.R;
import com.molamil.radio24syv.RadioLibrary;
import com.molamil.radio24syv.RadioPlayer;

import java.util.Locale;

/**
 * Created by jens on 21/09/15.
 */
public class PodcastEpisodeView extends LinearLayout {

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
//        View contractButton = findViewById(R.id.contract_button);
//        contractButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setSize(Size.CONTRACTED);
//            }
//        });
//
//        TextView descriptionText = (TextView) findViewById(R.id.description_text);
//        descriptionText.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setSize(Size.CONTRACTED);
//            }
//        });

        View contractedLayout = findViewById(R.id.contracted_layout);
        contractedLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setSize(Size.EXPANDED);
            }
        });

        View expandedLayout = findViewById(R.id.expanded_layout);
        expandedLayout.setOnClickListener(new OnClickListener() {
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
        // This is never called when instantiating with: myView = new MyView()
        // It is only called when instantiating in XML: <com.bla.bla.MyView />
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

    public void setPodcastId(int podcastId) {
        this.podcastId = podcastId;
        updateLibraryStatus();
    }

    public void setPodcastUrl(String podcastUrl) {
        this.podcastUrl = podcastUrl;
        RadioPlayerButton playButton = (RadioPlayerButton) findViewById(R.id.play_button);
        playButton.setUrl(RadioLibrary.getInstance().getUrl(getContext(), podcastUrl));
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

    private void updateLibraryStatus() {
        RadioLibrary.Status status = RadioLibrary.getInstance().getStatus(getContext(), podcastId);

        TextView downloadedImage = (TextView) findViewById(R.id.downloaded_image);
        downloadedImage.setText(status.getDownloadStatusText());

        TextView downloadButton = (TextView) findViewById(R.id.download_image);
        downloadButton.setText(status.getDownloadStatusText() + " " + status.getDownloadProgressText());

        downloadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioLibrary.getInstance().download(getContext(), podcastId, podcastUrl, title);
//                updateLibraryStatus();
            }
        });

    }

    public void setOnPodcastEpisodeViewUpdatedListener(OnPodcastEpisodeViewUpdatedListener listener) {
        this.listener = listener;
    }

    public interface OnPodcastEpisodeViewUpdatedListener {
        void onPodcastEpisodeViewSizeChanged(PodcastEpisodeView view, Size size);
//        void onPodcastEpisodeViewRadioPlayerAction(PodcastEpisodeView view, int action);
    }
}