package com.molamil.radio24syv;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.storage.ImageLibrary;
import com.molamil.radio24syv.storage.RadioLibrary;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.PodcastInfo;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.view.PodcastProgramView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


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

        TextView statusText = (TextView) v.findViewById(R.id.status_text);
        List<ProgramInfo> programs = Storage.get().getProgramsWithPodcastsInLibrary();
        statusText.setVisibility(programs.size() > 0 ? View.GONE : View.VISIBLE);

        ViewGroup content = (ViewGroup) v.findViewById(R.id.content);
        for (ProgramInfo program :programs) {
            PodcastProgramView p = new PodcastProgramView(v.getContext());
            p.setProgram(program);
            p.setRadioPlayer(radioPlayerProvider.getRadioPlayer());
            ArrayList<PodcastInfo> podcasts = new ArrayList<>();
            for (PodcastInfo podcast : Storage.get().getPodcastsInLibrary(program.getProgramId())) {
                podcasts.add(podcast); // Only show podcasts that known by the download manager
            }
            p.setPodcasts(podcasts);
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

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
