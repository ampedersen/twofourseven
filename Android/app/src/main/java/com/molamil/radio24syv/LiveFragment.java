package com.molamil.radio24syv;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.molamil.radio24syv.api.RestApi;
import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.Broadcast;
import com.molamil.radio24syv.components.TimeLine;
import com.molamil.radio24syv.managers.LiveContentUpdater;
import com.molamil.radio24syv.managers.LiveContentUpdater.OnUpdateListener;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.view.ProgramImageView;
import com.molamil.radio24syv.view.RadioPlayerButton;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.lang.reflect.Field;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LiveFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveFragment extends PageFragment implements OnUpdateListener {

    private OnFragmentInteractionListener listener;
    private PlayerFragment.OnFragmentInteractionListener playerListener;
    private RadioPlayer.RadioPlayerProvider radioPlayerProvider;

    private TimeLine timeline;

    private Button expandButton;
    private boolean expanded = false;
    private String fullDescription = "";

    private TextView titleTv;
    //private TextView topicTv;
    private TextView beginTimeTv;
    private TextView endTimeTv;
    private TextView desctiptionTv;
    private ProgramImageView coverIv;

    private RadioPlayerButton playButton;

    public LiveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_live, container, false);

        timeline = (TimeLine) v.findViewById(R.id.player_progress);
        timeline.setProgress(0);
        desctiptionTv = ((TextView) v.findViewById(R.id.program_description));
        titleTv = ((TextView) v.findViewById(R.id.program_title));
        //topicTv = ((TextView) v.findViewById(R.id.program_description));
        beginTimeTv = ((TextView) v.findViewById(R.id.program_time_begin));
        endTimeTv = ((TextView) v.findViewById(R.id.program_time_end));

        playButton = (RadioPlayerButton) v.findViewById(R.id.play_button);

        coverIv = (ProgramImageView) v.findViewById(R.id.image_cover);

        Button scheduleButton = (Button)v.findViewById(R.id.schedule_button);
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onShowSidePage(OnFragmentInteractionListener.Side.SHOW_RIGHT);
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

        RestClient.getApi().getCurrentBroadcast().enqueue(new Callback<List<Broadcast>>() {
            @Override
            public void onResponse(Response<List<Broadcast>> response) {

                if (listener != null) {
                    listener.onError(null);
                }
                if (response == null) {
                    return;
                }
                List<Broadcast> body = response.body();
                if (body != null) {
                    Broadcast b = body.get(0);
                    View v = getView();

                    if (v == null)
                        return; // We are still assigned as a callback to the previous instance of the fragment. TODO store the call in a variable, cancel it in onDestroy to getInstance rid of callbacks.

                    populateView(b);

                } else {
                    if (listener != null) {
                        listener.onError(response.message());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("JJJ", "fail " + t.getMessage());
                t.printStackTrace();
                if (listener != null) {
                    listener.onError(t.getLocalizedMessage());
                }
            }
        });

        return v;
    }

    private void populateView(Broadcast b)
    {

        String title = b.getProgramName();
        String description = b.getDescriptionText();
        String startTime = b.getBroadcastTime().getStart();
        String endTime = b.getBroadcastTime().getEnd();
        String topic = b.getTopic();

        titleTv.setText(title);
        beginTimeTv.setText(RestClient.getLocalTime(startTime));
        endTimeTv.setText(RestClient.getLocalTime(endTime));
        //topicTv.setText(b.getTopic());
        expanded = false;
        fullDescription = description;
        updateExpandedState();

        playButton.setTitle(title);
        playButton.setDescription(description);
        playButton.setProgramTitle(title);
        playButton.setTopic(topic);
        playButton.setStartTime(RestClient.getLocalTime(startTime));
        playButton.setEndTime(RestClient.getLocalTime(endTime));

        //Update timeline
        DateTime start = new DateTime(startTime);
        DateTime end = new DateTime(endTime);
        timeline.setProgress(start, end);

        if(coverIv != null)
        {
            if(b.getAppImages().getLive() != null && !b.getAppImages().getLive().isEmpty()) {
                coverIv.setVisibility(View.VISIBLE);
                coverIv.setImageUrl(b.getAppImages().getLive()); //This is set to player url externally so it will be correct
                coverIv.setTintColor(0xffffff);
            }
            else
            {
                coverIv.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RadioPlayerButton playButton = (RadioPlayerButton)getView().findViewById(R.id.play_button);
        playButton.setRadioPlayer(radioPlayerProvider.getRadioPlayer()); // Setup play button. Must do this in onActivityCreated() to be sure our host activity is up and running.
    }

    @Override
    public void onAttach(Activity activity) {

        //getMainActivity().getRadioPlayer()

        super.onAttach(activity);
        try {
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        try {
            playerListener = (PlayerFragment.OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PlayerFragment.OnFragmentInteractionListener");
        }
        try {
            radioPlayerProvider = (RadioPlayer.RadioPlayerProvider) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PlayerFragment.RadioPlayerProvider");
        }

        LiveContentUpdater.getInstance().addListener(this);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        playerListener = null;
        radioPlayerProvider = null;

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        LiveContentUpdater.getInstance().removeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Radio24syvApp.getInstance().trackScreenView("Live Screen");
    }

    //Description expand
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

    private String getDescription() {
        if (expanded) {
            return fullDescription;
        }

        int stubCutoffLimit = 150;
        int stubLength = 100;
        if (fullDescription.length() < stubCutoffLimit) {
            expandButton.setVisibility(View.GONE);
            return fullDescription;
        }

        return fullDescription.substring(0, stubLength) + "...";
    }

    @Override
    public void OnUpdate(Broadcast broadcast)
    {
        populateView(broadcast);
    }
}
