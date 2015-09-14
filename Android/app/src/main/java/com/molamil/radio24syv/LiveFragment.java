package com.molamil.radio24syv;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LiveFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveFragment extends PageFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener listener;
    private PlayerFragment.OnFragmentInteractionListener playerListener;
    private PlayerFragment.RadioPlayerProvider radioPlayerProvider;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LiveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LiveFragment newInstance(String param1, String param2) {
        LiveFragment fragment = new LiveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_live, container, false);

        Button scheduleButton = (Button)v.findViewById(R.id.schedule_button);
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onShowSidePage(OnFragmentInteractionListener.Side.SHOW_RIGHT);
                }
            }
        });

//        MediaPlayerButton playButton = (MediaPlayerButton)v.findViewById(R.id.play_button);
//        playButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (playerListener != null) {
//                    playerListener.onPlayerControl(PlayerFragment.PlayerAction.PLAY); // TODO custom play button taking care of its own state
//                }
//            }
//        });

        return v;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MediaPlayerButton playButton = (MediaPlayerButton)getView().findViewById(R.id.play_button);
        playButton.setRadioPlayer(radioPlayerProvider.getRadioPlayer()); // Setup play button. Must do this in onActivityCreated() to be sure our host activity is up and running.
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
            playerListener = (PlayerFragment.OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PlayerFragment.OnFragmentInteractionListener");
        }
        try {
            radioPlayerProvider = (PlayerFragment.RadioPlayerProvider) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PlayerFragment.RadioPlayerProvider");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        playerListener = null;
        radioPlayerProvider = null;
    }


}
