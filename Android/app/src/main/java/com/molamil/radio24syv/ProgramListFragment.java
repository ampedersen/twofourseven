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
import com.molamil.radio24syv.api.model.Podcast;
import com.molamil.radio24syv.settings.model.PodcastInfo;
import com.molamil.radio24syv.settings.model.ProgramInfo;
import com.molamil.radio24syv.view.DateLineView;
import com.molamil.radio24syv.view.PodcastEpisodeView;
import com.molamil.radio24syv.view.ProgramButtonView;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;

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
        Button someProgramButton = (Button)v.findViewById(R.id.some_program_button);
        ProgramInfo p = new ProgramInfo();
        p.setProgramId(3843763);
        p.setName("Name");
        p.setTopic("Topic");
        p.setDescription("Description");
        p.setImageUrl("ImageUrl");
        someProgramButton.setTag(R.id.action_bar, p); // TODO custom button for this instead of tag ugliness
        someProgramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onProgramSelected((ProgramInfo) v.getTag(R.id.action_bar));
                }
            }
        });

        final ViewGroup content = (ViewGroup) v.findViewById(R.id.content);
        getPrograms(content);

        return v;
    }

//    private void getPrograms(final ViewGroup content, final int amount, final int batch) {
    private void getPrograms(final ViewGroup content) {
//        Log.d("JJJ", "getPodcasts id " + program.getProgramId() + " content " + content + " amount " + amount + " batch " + batch);
        final ViewGroup parent = (ViewGroup) content.getParent();

        RestClient.getApi().getPrograms().enqueue(new Callback<List<ConciseProgram>>() {
            @Override
            public void onResponse(Response<List<ConciseProgram>> response) {
                for (int i = 0; i < response.body().size(); i++) {
                    ConciseProgram conciseProgram = response.body().get(i);
                    boolean isActive = conciseProgram.getActive();
//                    if (isActive) {
                        final ProgramInfo p = new ProgramInfo(conciseProgram);

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
//                    }
                }

            }

            @Override
            public void onFailure(Throwable t) {
                ((MainActivity) getActivity()).onError("Kunne ikke få forbindelse, beklager."); // TODO meaningful error messages (and check internet connection)
                Log.d("JJJ", "fail " + t.getMessage());
                t.printStackTrace();
            }
        });
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
