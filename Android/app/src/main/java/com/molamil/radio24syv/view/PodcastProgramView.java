package com.molamil.radio24syv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molamil.radio24syv.R;
import com.molamil.radio24syv.storage.RadioLibrary;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.PodcastInfo;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.storage.model.TopicInfo;

import java.util.List;

/**
 * Created by jens on 25/09/15.
 */
public class PodcastProgramView extends LinearLayout implements
    PodcastEpisodeView.OnPodcastEpisodeViewUpdatedListener {

    public enum Size { UNASSIGNED, CONTRACTED, EXPANDED }
    private Size size = Size.UNASSIGNED;
//    private OnPodcastEpisodeViewUpdatedListener listener = null;
    private PodcastEpisodeView expandedView = null;
    private ProgramInfo program;

    private RadioPlayer radioPlayer;

    public PodcastProgramView(Context context) {
        super(context);
        initializeViews(context);
    }

    public PodcastProgramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public PodcastProgramView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(final Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_podcast_program, this);

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

        setSize(Size.EXPANDED);
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
//        RadioLibrary.getInstance().removeListener(podcastId, this);
        radioPlayer = null;
    }

    public void setProgram(ProgramInfo program) {
        this.program = program;

        TextView titleText = (TextView) findViewById(R.id.name_text);
        titleText.setText(program.getName());

        TopicInfo topic = Storage.get().getTopic(program.getTopicId());
        boolean isTopicInStorage = (topic != null); // When the podcast is saved there is no way to get past downloading all topics, but just in case
        if (isTopicInStorage) {
            titleText.setTextColor(topic.getColorValue());
        }
    }

    public void setRadioPlayer(RadioPlayer radioPlayer) {
        this.radioPlayer = radioPlayer;
    }

    public void setPodcasts(List<PodcastInfo> podcasts) {
        ViewGroup expandedLayout = (ViewGroup) findViewById(R.id.expanded_layout);
        for (PodcastInfo p : podcasts) {
            PodcastEpisodeView v = new PodcastEpisodeView(getContext());
            v.setPodcast(p);
            v.setRadioPlayer(radioPlayer);
            v.setOnPodcastEpisodeViewUpdatedListener(this);
            expandedLayout.addView(v); // Add podcast as a child
        }

        updatePodcastCount(podcasts.size());
    }

    private void updatePodcastCount(int count) {
        TextView countText = (TextView) findViewById(R.id.count_text);
        int countTextId;
        if (count == 1) {
            countTextId = R.string.episode_count;
        } else {
            countTextId = R.string.episodes_count;
        }
        countText.setText(String.format(getResources().getString(countTextId), count)); // Number of episodes
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

        TextView sizeButton = (TextView) findViewById(R.id.size_button);
        if (size == Size.CONTRACTED) {
            sizeButton.setText("[expand image]");
        } else {
            sizeButton.setText("[contract image]");
        }

//        if (listener != null) {
//            listener.onPodcastEpisodeViewSizeChanged(this, size);
//        }
    }

    @Override
    public void onPodcastEpisodeViewSizeChanged(PodcastEpisodeView view, PodcastEpisodeView.Size size) {
        if (size == PodcastEpisodeView.Size.EXPANDED) {
            View oldExpandedView = expandedView;
            if (expandedView != null) {
                expandedView.setSize(PodcastEpisodeView.Size.CONTRACTED); // Make sure only one view is expanded at a time
            }
            if (oldExpandedView != view) {
                expandedView = view; // If the same view was clicked, it has just been contracted and expandedView set to null. Do not assign it again.
            }
        } else {
            expandedView = null;
        }
    }

    @Override
    public void onPodcastEpisodeViewDownloadClicked(PodcastEpisodeView view, int podcastId) {
        // Not used. Download button is only visible if the podcast is not downloaded, and only downloaded podcasts are shown here.
    }

    @Override
    public void onPodcastEpisodeViewRemoveClicked(PodcastEpisodeView view, PodcastInfo podcast) {
        ViewGroup expandedLayout = (ViewGroup) findViewById(R.id.expanded_layout);
        expandedLayout.removeView(view);
        RadioLibrary.getInstance().remove(getContext(), podcast);

        int count = Storage.get().getPodcastCount(program.getProgramId());
        if (count > 0) {
            updatePodcastCount(count);
        } else {
            ((ViewGroup) getParent()).removeView(this); // Remove self when all podcasts are removed
        }
    }


}
