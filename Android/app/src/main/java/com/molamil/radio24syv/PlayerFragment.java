package com.molamil.radio24syv;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment {
    public enum PlayerSize { NONE, SMALL, BIG };
    public enum PlayerAction { PLAY, STOP };

    // Fragment parameters
    static final String ARG_TITLE = "title";
    String title;

    OnFragmentInteractionListener mListener;

    PlayerSize size = PlayerSize.NONE;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Track title.
     * @return A new instance of fragment PlayerFragment.
     */
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

//        if (savedInstanceState != null) {
//            size = PlayerSize.valueOf(savedInstanceState.getString("size"));
//        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putString("size", size.toString());
//        super.onSaveInstanceState(savedInstanceState);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_player, container, false);

        Button expandButton = (Button)v.findViewById(R.id.size_button);
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

        Button playButton = (Button)v.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    if (isPlaying) {
                        mListener.onPlayerControl(PlayerAction.STOP);
                    } else {
                        mListener.onPlayerControl(PlayerAction.PLAY);
                    }
                }
            }
        });

        updateSize(v);
        updatePlayButton(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    boolean isOffline;
    boolean isPlaying;

    public void setOffline(boolean isOffline) {
        this.isOffline = isOffline;
        updatePlayButton(getView());
    }

    public void setPlaying(boolean isPlaying) {
        if (size == PlayerSize.NONE) {
            setSize(PlayerSize.SMALL);
        }
        this.isPlaying = isPlaying;
        updatePlayButton(getView());
    }

    private void updatePlayButton(View parentView) {
        Button playButton = (Button) parentView.findViewById(R.id.play_button);
        if (isPlaying) {
            if (isOffline) {
                playButton.setText("Pause");
            } else {
                playButton.setText("Stop");
            }
        } else {
            playButton.setText("Play");
        }
    }

    public void setSize(PlayerSize size) {
        Log.d("JJJ", "player setsize " + size + " was " + this.size);
        if (size == this.size) {
            return; // Return, already sized like that
        }

        PlayerSize oldSize = this.size;
        this.size = size;

        updateSize(getView());

        if (mListener != null) {
            mListener.onPlayerSizeChanged(size, oldSize);
        }
    }

    private void updateSize(View parentView) {
        if (size == PlayerSize.NONE) {
            parentView.setVisibility(View.GONE);
        } else {
            parentView.setVisibility(View.VISIBLE);
            View bigThingy = parentView.findViewById(R.id.big_thingy);
            Button expandButton = (Button) parentView.findViewById(R.id.size_button);
            int targetColorId;
            if (size == PlayerSize.BIG) {
                bigThingy.setVisibility(View.VISIBLE);
                expandButton.setText("Small");
                targetColorId = R.color.radio_gray_dark;
            } else {
                bigThingy.setVisibility(View.GONE);
                expandButton.setText("Big");
                targetColorId = R.color.radio_gray_darker;
            }
            parentView.setBackgroundColor(getResources().getColor(targetColorId));
        }
    }

    public PlayerSize getSize() {
        return size;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onPlayerSizeChanged(PlayerSize newSize, PlayerSize oldSize);
        public void onPlayerControl(PlayerAction action);
    }

}
