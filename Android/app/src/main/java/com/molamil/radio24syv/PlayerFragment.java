package com.molamil.radio24syv;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.molamil.radio24syv.api.model.Broadcast;
import com.molamil.radio24syv.components.OnSwipeTouchListener;
import com.molamil.radio24syv.components.RatingComponent;
import com.molamil.radio24syv.components.TimeLine;
import com.molamil.radio24syv.components.TimeLineSeekBar;
import com.molamil.radio24syv.managers.LiveContentUpdater;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.storage.model.TopicInfo;
import com.molamil.radio24syv.util.DateUtils;
import com.molamil.radio24syv.view.ProgramImageView;
import com.molamil.radio24syv.view.RadioPlayerButton;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment implements RadioPlayer.OnPlaybackListener, LiveContentUpdater.OnUpdateListener {


    public enum PlayerSize { NONE, SMALL, BIG };

    // Fragment parameters
    static final String ARG_TITLE = "title";
    private String title;

    private OnFragmentInteractionListener mListener;
    private RadioPlayer.RadioPlayerProvider radioPlayerProvider;
    private ProgramInfo programInfo = new ProgramInfo(); // TODO get broadcast info from storage if already downloaded, otherwise download it

    private PlayerSize size = PlayerSize.NONE;

    private RadioPlayerButton prevButton;
    private RadioPlayerButton nextButton;

    private TimeLine timeline;
    private TimeLine smallTimeLine;
    private TimeLineSeekBar timeLineSeekBar;

    private TextView startTimeLabel;
    private TextView endTimeLabel;

    private RatingComponent ratingComponent;

    private long timelineUpdateInterval = 900;
    private Handler timelineHandler = new Handler(); //Refactor to player service and let it update all necesary timelines
    private Runnable timelineRunnable = new Runnable() {
        @Override
        public void run()
        {
            RadioPlayer player = radioPlayerProvider.getRadioPlayer();
            UpdateTimeLines(player);

            timelineHandler.postDelayed(timelineRunnable, timelineUpdateInterval);
        }
    };
    public static PlayerFragment newInstance(String title) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public PlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_player, container, false);

        ImageButton expandButton = (ImageButton)v.findViewById(R.id.size_button);
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (size == PlayerSize.BIG) {
                    setSize(PlayerSize.SMALL);
                } else {
                    setSize(PlayerSize.BIG);
                }
            }
        });

        prevButton = (RadioPlayerButton) v.findViewById(R.id.previous_button);
        nextButton = (RadioPlayerButton) v.findViewById(R.id.next_button);

        startTimeLabel = (TextView) v.findViewById(R.id.time_start_text);
        endTimeLabel = (TextView) v.findViewById(R.id.time_end_text);



        timeline = (TimeLine) v.findViewById(R.id.player_progress);
        smallTimeLine = (TimeLine) v.findViewById(R.id.small_player_progress);
        timeLineSeekBar = (TimeLineSeekBar) v.findViewById(R.id.player_progress_seekbar);
        timeLineSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    RadioPlayer player = radioPlayerProvider.getRadioPlayer();
                    player.seekTo(progress / 100.0f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateSize(v);

        v.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeUp() {
                setSize(PlayerSize.BIG);
            }

            @Override
            public void onSwipeDown() {
                setSize(PlayerSize.SMALL);
            }
        });

        ratingComponent = new RatingComponent(getContext());

        LinearLayout ratingContainer = (LinearLayout) v.findViewById(R.id.rating_container);

        ratingContainer.addView(ratingComponent);
        ratingComponent.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RadioPlayer player = radioPlayerProvider.getRadioPlayer();
        player.addListener(this);
        setupPlaybackButtons(player, getView());

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        try {
            radioPlayerProvider = (RadioPlayer.RadioPlayerProvider) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement PlayerFragment.RadioPlayerProvider");
        }

        LiveContentUpdater.getInstance().addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        timelineHandler.removeCallbacks(timelineRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        RadioPlayer player = radioPlayerProvider.getRadioPlayer();
        if(player.getAction() == RadioPlayer.ACTION_PLAY) {
            timelineHandler.post(timelineRunnable);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        timelineHandler.removeCallbacks(timelineRunnable);

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
    public void OnBusy(RadioPlayer player) {
        if (size == PlayerSize.NONE) {
            setSize(PlayerSize.SMALL);
        }
        setupPlaybackButtons(player, getView());
        updatePlayer();
    }

    @Override
    public void OnStarted(RadioPlayer player) {
        if (size == PlayerSize.NONE) {
            setSize(PlayerSize.SMALL);
        }
        setupPlaybackButtons(player, getView());
        updatePlayer();

        timelineHandler.post(timelineRunnable);
    }

    @Override
    public void OnStopped(RadioPlayer player) {
        updatePlayer();

        timelineHandler.removeCallbacks(timelineRunnable);
    }

    @Override
    public void OnPaused(RadioPlayer player) {
        updatePlayer();

        timelineHandler.removeCallbacks(timelineRunnable);
    }



    private void setupPlaybackButtons(RadioPlayer player, View parentView) {
        // Link all buttons to always show current playback status
        setupButton(player, parentView, R.id.small_play_button);
        setupButton(player, parentView, R.id.play_button);
        setupButton(player, parentView, R.id.next_button);
        setupButton(player, parentView, R.id.previous_button);
    }

    private void setupButton(RadioPlayer player, View parentView, int buttonId) {
        if(parentView == null || player == null)
        {
            return;
        }
        RadioPlayerButton b = (RadioPlayerButton)parentView.findViewById(buttonId);
        b.setUrl(player.getUrl());
        b.setTitle(player.getTitle());
        b.setDescription(player.getDescription());
        b.setRadioPlayer(player);
    }

    public void setSize(PlayerSize size) {
        //Log.d("JJJ", "player setsize " + size + " was " + this.size);
        if (size == this.size) {
            return; // Return, already sized like that
        }

        PlayerSize oldSize = this.size;
        this.size = size;

        updateSizeSafely(getView());

        if (mListener != null) {
            mListener.onPlayerSizeChanged(size, oldSize);
        }
    }

    private void updateSizeSafely(final View parentView) {
        // Delay updating UI until the view's own thread. We may be called from the background thread handling radio playback, and only UI thread can touch UI stuff.
        if(parentView == null)
        {
            return;
        }

        parentView.post(new Runnable() {
            @Override
            public void run() {
                updateSize(parentView);
            }
        });
    }

    private void updateSize(final View parentView) {
        if(getActivity() == null)
        {
            return;
        }
        if (size == PlayerSize.NONE) {
            parentView.setVisibility(View.GONE);
        } else {

            parentView.setVisibility(View.VISIBLE);
            View bigPlayer = parentView.findViewById(R.id.big_player);
            View smallPlayer = parentView.findViewById(R.id.small_player);
            ImageButton expandButton = (ImageButton) parentView.findViewById(R.id.size_button);
            int targetColorId;
            if (size == PlayerSize.BIG) {
                Radio24syvApp.getInstance().trackScreenView("Player Screen Big");
                bigPlayer.setVisibility(View.VISIBLE);
                smallPlayer.setVisibility(View.GONE);
                expandButton.setImageResource(R.drawable.collapse_player_button);
                targetColorId = R.color.player_background;

                //Remove fling gesture
            } else {
                Radio24syvApp.getInstance().trackScreenView("Player Screen Small");
                bigPlayer.setVisibility(View.GONE);
                smallPlayer.setVisibility(View.VISIBLE);
                expandButton.setImageResource(R.drawable.expand_player_button);
                targetColorId = R.color.radio_gray_darker;//R.color.mini_player_background;

                //add fling gesture
            }
            parentView.setBackgroundColor(getResources().getColor(targetColorId));
            updatePlayer();
        }
    }

    public void updatePlayer() {

        RadioPlayer player = radioPlayerProvider.getRadioPlayer();
        programInfo.setName(player.getTitle());
        programInfo.setDescription(player.getDescription());
        programInfo.setTopic(player.getTopic());
        programInfo.setStartTime(player.getStartTime());
        programInfo.setEndTime(player.getEndTime());

        //TODO: Handle Program title vs podcast title.
        //programInfo.setName(player.getProgramTitle());

        View v = getView();
        if (v == null) {
            return;
        }
        View bigPlayer = v.findViewById(R.id.big_player);
        View smallPlayer = v.findViewById(R.id.small_player);
        if (size == PlayerSize.BIG) {
            updateBigPlayer(bigPlayer, programInfo);
        } else {
            updateSmallPlayer(smallPlayer, programInfo);
        }

        /*
        //Ratings
        if (player.getRating() != null) {
            //Log.i("PS", "Show ratings component");
        } else {

        }
        */
        ratingComponent.setVisibility(player.getRating() != null ? View.VISIBLE : View.GONE);
        ratingComponent.setPodcastId(player.getPodcastId());
        ratingComponent.updateRating(player.getRatingFloat());
        //ratingComponent.setPodcastId(player.get);
        UpdateTimeLines(player);
    }

    /*
    //TODO: FIX Duplicate code
    public void updatePlayer(Broadcast b) {

        RadioPlayer player = radioPlayerProvider.getRadioPlayer();
        programInfo.setName(b.getProgramName());
        programInfo.setDescription(b.getDescriptionText());
        programInfo.setTopic(b.getTopic());
        programInfo.setStartTime(RestClient.getLocalTime(b.getBroadcastTime().getStart()));
        programInfo.setEndTime(RestClient.getLocalTime(b.getBroadcastTime().getEnd()));

        //TODO: Handle Program title vs podcast title.
        //programInfo.setName(player.getProgramTitle());

        View v = getView();
        if (v == null) {
            return;
        }
        View bigPlayer = v.findViewById(R.id.big_player);
        View smallPlayer = v.findViewById(R.id.small_player);
        if (size == PlayerSize.BIG) {
            updateBigPlayer(bigPlayer, programInfo);
        } else {
            updateSmallPlayer(smallPlayer, programInfo);
        }

        UpdateTimeLines(player);
    }
*/
    public void setImageUrl(String imageUrl) {
        programInfo.setImageUrl(imageUrl);
        updatePlayer();
    }

    private void updateBigPlayer(View v, ProgramInfo p) {
        ProgramImageView programImage = ((ProgramImageView) v.findViewById(R.id.image));
        programImage.setImageUrl(p.getImageUrl()); //This is set to player url externally so it will be correct
        programImage.setTintColor(0x000000);
        TextView titleText =  ((TextView) v.findViewById(R.id.name_text));
        titleText.setText(p.getName());

        //TODO: Set podcast title when available, else program title
        ((TextView) v.findViewById(R.id.podcast_name_text)).setText(p.getName());


        // INJECT RATING HERE SOMEWHERE - WE DO NOT HAVE THE PODCAST ID

        titleText.setTextColor(0xffffffff);
        if(p.getTopic() != null)
        {
            TopicInfo topic = Storage.get().getTopic(p.getTopicId());
            if(topic != null) {
                titleText.setTextColor(topic.getColorValue());
            }
        }

        if(!(p.getFormattedStartTime() == "" || p.getFormattedEndTime() == ""))
        {
            ((TextView) v.findViewById(R.id.time_text)).setText(p.getFormattedStartTime() + " - " + p.getFormattedEndTime());
        }
        ((TextView) v.findViewById(R.id.description_text)).setText(p.getDescription());

        //Next prev buttons activation

        RadioPlayer player = radioPlayerProvider.getRadioPlayer();
        enableNextPrevButton(prevButton, player.hasPrevious());
        enableNextPrevButton(nextButton, player.hasNext());

        if (isAdded()) {
            if (player.getUrl() != getString(R.string.url_live_radio)) {

                timeline.setVisibility(View.GONE);
                timeLineSeekBar.setVisibility(View.VISIBLE);

            } else {
                timeline.setVisibility(View.VISIBLE);
                timeLineSeekBar.setVisibility(View.GONE);
            }
        }
    }

    private void enableNextPrevButton(RadioPlayerButton button, boolean enabled)
    {
        button.setEnabled(enabled);
        if(button.getAction() == RadioPlayer.ACTION_PREVIOUS)
        {
            button.setBackgroundResource(enabled ? R.drawable.prev_button : R.drawable.prev_button_disabled);
        }
        else if(button.getAction() == RadioPlayer.ACTION_NEXT)
        {
            button.setBackgroundResource(enabled ? R.drawable.next_button : R.drawable.next_button_disabled);
        }
    }

    private void updateSmallPlayer(View v, ProgramInfo p) {
        ((TextView) v.findViewById(R.id.small_name_text)).setText(p.getName());
    }

    public PlayerSize getSize() {
        return size;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <programInfo/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onPlayerSizeChanged(PlayerSize newSize, PlayerSize oldSize);
    }

    //Timeline control
    //HACK. Using formatted time strings HH:mm to calculate progress. Should use dates
    private void UpdateTimeLines(RadioPlayer player)
    {
        String start = player.getStartTime();
        String end = player.getEndTime();

        //Not live, playing mp3 file. Calc progress from file progress instead
        if (isAdded()) {
            if (player.getUrl() != getString(R.string.url_live_radio)) {
                float pct = player.getProgress();
                int duration = player.getDuration();
                timeline.setProgress(pct);
                smallTimeLine.setProgress(pct);
                timeLineSeekBar.setProgress(pct);

                //start and end times
                int past = (int) (pct * duration);
                int left = duration - past;

                int s0 = (int) (past / 1000) % 60;
                int m0 = (int) ((past / (1000 * 60)) % 60);
                int s1 = (int) (left / 1000) % 60;
                int m1 = (int) ((left / (1000 * 60)) % 60);

                String elapsed = String.format("%02d", m0) + ":" + String.format("%02d", s0);
                String remaining = "-" + String.format("%02d", m1) + ":" + String.format("%02d", s1);

                startTimeLabel.setText(elapsed);
                endTimeLabel.setText(remaining);

                return;
            }
        }

        Date now = new Date();
        SimpleDateFormat sdfr = new SimpleDateFormat("HH:mm");
        Date curr = DateUtils.timeStringToDate(sdfr.format(now));

        Date startDate = DateUtils.timeStringToDate(start);
        Date endDate = DateUtils.timeStringToDate(end);

        startTimeLabel.setText(start);
        endTimeLabel.setText(end);

        if(startDate != null && endDate != null && curr != null)
        {
            long t0 = startDate.getTime();
            long t1 = endDate.getTime();
            long t = curr.getTime();

            float duration = t1 - t0;
            float time = t - t0;

            //if(duration == 0)
            if(Math.signum(duration) == 0)
            {
                timeline.setProgress(0);
                smallTimeLine.setProgress(0);
                timeLineSeekBar.setProgress(0);
            }

            float pct = time/duration;

            //TODO: If we've reached pct = 1 (or something like 0.9999), then start checking for new live content
            //Log.i("PS", "UpdateTimeLines, pct: "+pct);
            /*
            if(player.isLive())
            {
                Log.i("PS","Live radio @ "+pct);
                if(pct >= 1.0 && liveReloadCallback == null)//!isLoadingNewContent)
                {
                    //isLoadingNewContent = true;//Set to false on backend respons IF content is updated
                    reloadBroadcastData();
                }
            }
            */

            timeline.setProgress(pct);
            smallTimeLine.setProgress(pct);
            timeLineSeekBar.setProgress(pct);
        }
    }

    @Override
    public void OnUpdate(Broadcast broadcast)
    {
        updatePlayer();
        //updatePlayer(broadcast);
    }
}
