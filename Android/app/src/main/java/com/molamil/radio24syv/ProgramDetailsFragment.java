package com.molamil.radio24syv;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.Podcast;
import com.molamil.radio24syv.api.model.Program;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.storage.RadioLibrary;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.PodcastInfo;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.storage.model.TopicInfo;
import com.molamil.radio24syv.view.DateLineView;
import com.molamil.radio24syv.view.PodcastEpisodeView;
import com.molamil.radio24syv.view.ProgramImageView;

import org.joda.time.DateTime;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.Response;

public class ProgramDetailsFragment extends PageFragment implements
        PodcastEpisodeView.OnPodcastEpisodeViewUpdatedListener {

    public static final String ARGUMENT_PROGRAM = "ConciseProgram";

    private static final int BATCH_SIZE = 50; // Number of podcasts to load per batch

    private OnFragmentInteractionListener listener;
    private PodcastEpisodeView expandedView = null;
    private DateTime lastPodcastDate = null;

    /*
    public void setProgram(ProgramInfo program) {
        this.program = program;
    }
    */

    private ProgramInfo program;
    private RadioPlayer.RadioPlayerProvider radioPlayerProvider;
    private HashMap<Integer, PodcastInfo> podcastById = new HashMap<>();

    private ProgressBar progressSpinner;

    private Button expandButton;
    private TextView desctiptionTv;
    private boolean expanded = false;

    CheckBox notificationButton;
    private OnProgramNotificationToggleListener notificationListener;

    public static ProgramDetailsFragment newInstance(ProgramInfo program) {
        ProgramDetailsFragment fragment = new ProgramDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_PROGRAM, program);
        fragment.setArguments(args);
        return fragment;
    }

    public ProgramDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create ConciseProgram object from arguments
        program = (ProgramInfo) getArguments().getSerializable(ARGUMENT_PROGRAM);

        Log.d("JJJ", "my id " + getId() + " Rid " + R.layout.fragment_program_details);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_program_details, container, false);

        //ProgramInfo is sometimes generated from ConciseProgram which does not have broadcastInfo data or hosts. So load program data for this view when creating
        //((TextView) v.findViewById(R.id.info_text)).setText(program.getHostsAndTime(getActivity().getApplicationContext()));
        ((TextView) v.findViewById(R.id.info_text)).setText("");
        ((TextView) v.findViewById(R.id.name_text)).setText(program.getName());
        ((TextView) v.findViewById(R.id.title_text)).setText(program.getName());
        //((TextView) v.findViewById(R.id.topic_text)).setText(program.getTopic());
        desctiptionTv = ((TextView) v.findViewById(R.id.description_text));

        /*
        final Collection<TopicInfo> topics = Storage.get().getTopics();
        FrameLayout imageContainer = (FrameLayout) v.findViewById(R.id.image_container);
        for(TopicInfo topic : topics)
        {
            if(topic.getTopicId().equalsIgnoreCase(program.getTopicId()))
            {
                imageContainer.setBackgroundColor(Color.parseColor(topic.getColor()));
                break;
            }
        }
        */

        ProgramImageView image = ((ProgramImageView) v.findViewById(R.id.image));
        image.setImageUrl(program.getAppImageOverviewUrl());
        TopicInfo topic = Storage.get().getTopic(program.getTopicId());
        if (topic != null) {
            image.setTintColor(topic.getColorValue());
        }

        ImageButton backButton = (ImageButton) v.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBackButtonPressed();
                }
            }
        });

        expandButton = (Button)v.findViewById(R.id.text_expand);
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleExpanded();
            }
        });

        progressSpinner = (ProgressBar) v.findViewById(R.id.activity_indicator);
        progressSpinner.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.radio_red), android.graphics.PorterDuff.Mode.MULTIPLY);

        notificationButton = (CheckBox) v.findViewById(R.id.notification_button);
        notificationButton.setVisibility(program.getActive() ? View.VISIBLE : View.GONE);

        boolean checked = (Storage.get().getProgramAlarmId(program.getProgramSlug()) != Storage.ALARM_ID_UNKNOWN);
        notificationButton.setChecked(checked);

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationListener != null) {
                    notificationListener.OnProgramNotificationButtonClicked((CheckBox) v, program.getProgramSlug());
                }
                //OnNotificationButtonClick();
            }
        });
        final ViewGroup content = (ViewGroup) v.findViewById(R.id.content);

        //DEV
        getPodcasts(content, BATCH_SIZE, 1);
        getDetails(v);

        updateExpandedState();

        return v;
    }



    private void toggleExpanded()
    {
        expanded = !expanded;
        updateExpandedState();
    }

    private void updateExpandedState()
    {
        Drawable drawable = expanded ? getResources().getDrawable( R.drawable.button_collapse ) : getResources().getDrawable(R.drawable.button_expand);
        expandButton.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        expandButton.setText(expanded ? R.string.contract_text : R.string.expand_text);
        desctiptionTv.setText(getDescription());
    }

    private String getDescription()
    {
        String full = program.getDescription();
        if(expanded)
        {
            return full;
        }

        int stubCutoffLimit = 150;
        int stubLength = 100;
        if(full.length() < stubCutoffLimit)
        {
            expandButton.setVisibility(View.GONE);
            return full;
        }

        return full.substring(0,stubLength)+"...";
        //return program.getDescription();
    }

    private void getDetails(final View view)
    {
        RestClient.getApi().getProgram(program.getProgramSlug()).enqueue(new Callback<Program>() {
            @Override
            public void onResponse(Response<Program> response) {

                if (response.body() == null) {
                    return;
                }

                if(getMainActivity() == null)
                {
                    return;
                }

                Program p = response.body();

                ProgramImageView image = ((ProgramImageView) view.findViewById(R.id.image));
                image.setImageUrl(p.getAppImages().getOverview());
                TopicInfo topic = Storage.get().getTopic(program.getTopicId());
                if (topic != null) {
                    image.setTintColor(topic.getColorValue());
                }

                String hostsAndTime = "";
                if(!p.getActive())
                {
                    hostsAndTime = getResources().getString(R.string.category_back_catalogue);//str = localizedString("category_back_catalogue")
                }
                else
                {
                    if(p.getHosts() != null)
                    {
                        String delimiter = ", ";
                        for(int i = 0 ; i < p.getHosts().size(); i++)
                        {
                            hostsAndTime += p.getHosts().get(i).getName();

                            if(i < p.getHosts().size()-1)
                            {
                                hostsAndTime += delimiter;
                            }
                        }
                    }

                    hostsAndTime +=  "\n" + p.getBroadcastInfo().getDay()+", "+p.getBroadcastInfo().getTime();
                }

                ((TextView) view.findViewById(R.id.info_text)).setText(hostsAndTime);
                ((TextView) view.findViewById(R.id.name_text)).setText(p.getIntro());
                ((TextView) view.findViewById(R.id.title_text)).setText(p.getName());
                updateExpandedState();

            }

            @Override
            public void onFailure(Throwable t) {
                //t.printStackTrace();
            }
        });
    }

    private void getPodcasts(final ViewGroup content, final int amount, final int batch) {
        final ViewGroup parent = (ViewGroup) content.getParent();
        progressSpinner.setVisibility(View.VISIBLE);
        final Button loadButton = (Button) parent.findViewById(R.id.load_button);
        loadButton.setVisibility(View.GONE);

        RestClient.getApi().getPodcasts(program.getProgramId(), amount, batch).enqueue(new Callback<List<Podcast>>() {
            @Override
            public void onResponse(Response<List<Podcast>> response) {
                if (listener != null) {
                    listener.onError(null);
                }
                if (response.body() == null) {
                    return;
                }

                PodcastEpisodeView prevView = null;

                for (int i = 0; i < response.body().size(); i++) {
                    PodcastInfo p = new PodcastInfo(response.body().get(i));
                    podcastById.put(p.getPodcastId(), p);

                    DateTime date = DateTime.parse(p.getDate());
                    boolean isNewMonth = (lastPodcastDate == null) || (!date.monthOfYear().equals(lastPodcastDate.monthOfYear())) || (!date.year().equals(lastPodcastDate.year()));
                    if (isNewMonth) {
                        if(prevView != null)
                        {
                            prevView.findViewById(R.id.divider).setVisibility(View.GONE);
                        }
                        lastPodcastDate = date;
                        DateLineView separator = new DateLineView(content.getContext());
                        Locale locale = new Locale("da", "DK");
                        //separator.setDate(date.monthOfYear().getAsText(Locale.getDefault()), date.year().getAsText(Locale.getDefault()));
                        separator.setDate(date.monthOfYear().getAsText(locale), date.year().getAsText(locale));
                        content.addView(separator);
                    }

                    PodcastEpisodeView v = new PodcastEpisodeView(content.getContext());
                    v.setPodcast(p);
                    v.setRadioPlayer(radioPlayerProvider.getRadioPlayer());
                    v.setOnPodcastEpisodeViewUpdatedListener(ProgramDetailsFragment.this);
                    content.addView(v);

                    /*
                    //TODO: Handle load more. This hidden divider might need to be shown when more results are loaded
                    if(i == response.body().size()-1)
                    {
                        v.findViewById(R.id.divider).setVisibility(View.GONE);
                    }
                    */

                    prevView = v;

                    Storage.get().addPodcast(p);
                }

                boolean isBatchFull = (response.body().size() == amount);
                if (isBatchFull) {
                    loadButton.setVisibility(View.VISIBLE);
                    loadButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getPodcasts(content, BATCH_SIZE, batch + 1); // Load next batch
                        }
                    });
                }

                progressSpinner.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {
                if (listener != null) {
                    listener.onError(t.getLocalizedMessage());
                }
                progressSpinner.setVisibility(View.GONE);
                Log.d("JJJ", "fail " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        try {
            radioPlayerProvider = (RadioPlayer.RadioPlayerProvider) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PlayerFragment.RadioPlayerProvider");
        }
        try {
            notificationListener = (OnProgramNotificationToggleListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProgramNotificationToggleListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        notificationListener = null;
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
        RadioLibrary.getInstance().remove(getActivity(), podcastById.get(podcastId)); // Remove podcast if it has been partially downloaded
        RadioLibrary.getInstance().download(getActivity(), program, podcastById.get(podcastId));
    }

    @Override
    public void onPodcastEpisodeViewRemoveClicked(PodcastEpisodeView view, PodcastInfo podcast) {
        RadioLibrary.getInstance().remove(getActivity(), podcast);
    }

    public interface OnFragmentInteractionListener extends PageFragment.OnFragmentInteractionListener {
        public void onBackButtonPressed();
    }

    public interface OnProgramNotificationToggleListener {
        void OnProgramNotificationButtonClicked(CheckBox clickedView, String slug);
    }
}
