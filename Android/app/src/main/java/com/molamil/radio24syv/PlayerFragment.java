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
    }

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

        return v;
    }

    private void setSize(PlayerSize size) {
        if (size == this.size) {
            return; // Return, already sized like that
        }

        PlayerSize oldSize = this.size;
        this.size = size;

        View bigThingy = getView().findViewById(R.id.big_thingy);
        Button expandButton = (Button)getView().findViewById(R.id.size_button);
        if (size == PlayerSize.BIG) {
            bigThingy.setVisibility(View.VISIBLE);
            expandButton.setText("\\/");
        } else {
            bigThingy.setVisibility(View.GONE);
            expandButton.setText("/\\");
        }
        if (mListener != null) {
            mListener.onPlayerSizeChanged(size, oldSize);
        }
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
    }

}
