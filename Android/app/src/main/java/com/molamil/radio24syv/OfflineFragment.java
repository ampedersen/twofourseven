package com.molamil.radio24syv;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.view.PodcastProgramView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OfflineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OfflineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OfflineFragment extends PageFragment {

    private OnFragmentInteractionListener mListener;

    private RadioPlayer.RadioPlayerProvider radioPlayerProvider;


    public OfflineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_offline, container, false);

        v.findViewById(R.id.delete_all_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Storage.get().deleteAll(v.getContext());
            }
        });

        v.findViewById(R.id.delete_history_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Storage.get().deleteHistory(v.getContext());
            }
        });

        ViewGroup content = (ViewGroup) v.findViewById(R.id.content);
        for (ProgramInfo program : Storage.get().getProgramsWithPodcastsInLibrary()) {
            PodcastProgramView p = new PodcastProgramView(v.getContext());
            p.setProgram(program);
            p.setRadioPlayer(radioPlayerProvider.getRadioPlayer());
            p.setPodcasts(Storage.get().getPodcasts(program.getProgramId()));
            content.addView(p);
        }

        return v;
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
        try {
            radioPlayerProvider = (RadioPlayer.RadioPlayerProvider) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PlayerFragment.RadioPlayerProvider");
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
