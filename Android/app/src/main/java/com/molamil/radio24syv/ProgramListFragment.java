package com.molamil.radio24syv;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.ConciseProgram;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.view.ProgramButtonView;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;


public class ProgramListFragment extends PageFragment {

    private OnFragmentInteractionListener mListener;
    private RadioPlayer.RadioPlayerProvider radioPlayerProvider;

    public ProgramListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_program_list, container, false);

        Button categoriesButton = (Button)v.findViewById(R.id.categories_button);
        categoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onShowSidePage(OnFragmentInteractionListener.Side.SHOW_LEFT);
                }
            }
        });
        Button searchButton = (Button) v.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onShowSidePage(OnFragmentInteractionListener.Side.SHOW_RIGHT);
                }
            }
        });

        final ViewGroup content = (ViewGroup) v.findViewById(R.id.content);
        getPrograms(content);

        return v;
    }

    private void getPrograms(final ViewGroup content) {
        // Read list instantly from local storage (if available).
        final List<ProgramInfo> programs = Storage.get().getPrograms();
        final boolean isCached = (programs != null) && (programs.size() > 0);

        // Download updated list, save to local storage. Next time the UI is shown it will show the updated list (if it had time to complete download).
        final ViewGroup parent = (ViewGroup) content.getParent();
        final TextView loadingText = (TextView) parent.findViewById(R.id.loading_text);
        if (!isCached) {
            loadingText.setVisibility(View.VISIBLE);
        }

        Log.d("JJJ", "Updating program list");
        RestClient.getApi().getPrograms().enqueue(new Callback<List<ConciseProgram>>() {
            @Override
            public void onResponse(Response<List<ConciseProgram>> response) {
                if (isCached) {
                    programs.clear(); // Clear old values
                }
                for (int i = 0; i < response.body().size(); i++) {
                    ConciseProgram conciseProgram = response.body().get(i);
                    programs.add(new ProgramInfo(conciseProgram));
                }
                if (!isCached) {
                    Log.d("JJJ", "TODO AWAITING API SERVER-SIDE Updating topic colors");
                    //RestClient.getApi().getTopicColor() //TODO update topic colors before updating program list
                    Log.d("JJJ", "Showing updated program list");
                    showPrograms(content, programs);
                    loadingText.setVisibility(View.GONE);
                }
                Storage.get().addPrograms(programs);
            }

            @Override
            public void onFailure(Throwable t) {
                if (!isCached) {
                    ((MainActivity) getActivity()).onError("Kunne ikke få forbindelse, beklager."); // TODO meaningful error messages (and check internet connection)
                    Log.d("JJJ", "fail " + t.getMessage());
                    t.printStackTrace();
                }
            }
        });

        // Show cached program list while waiting for response.
        if (isCached) {
            Log.d("JJJ", "Showing cached program list");
            showPrograms(content, programs);
        }
    }

    private void showPrograms(final ViewGroup content, List<ProgramInfo> programs) {
        for (final ProgramInfo p : programs) {
            ProgramButtonView v = new ProgramButtonView(content.getContext());
            v.setProgram(p);
            //v.setAudioUrl(); //TODO get audio url with different api call
            v.setRadioPlayer(radioPlayerProvider.getRadioPlayer());
            v.setOnProgramButtonViewListener(new ProgramButtonView.OnProgramButtonViewListener() {
                @Override
                public void OnProgramButtonViewClicked(ProgramButtonView view) {
                    if (mListener != null) {
                        mListener.onProgramSelected(p);
                    }
                }
            });
            content.addView(v);
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

    public interface OnFragmentInteractionListener extends PageFragment.OnFragmentInteractionListener {
        public void onProgramSelected(ProgramInfo program);
    }
}
