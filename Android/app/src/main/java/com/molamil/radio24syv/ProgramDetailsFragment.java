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
import com.molamil.radio24syv.api.model.Podcast;
import com.molamil.radio24syv.view.DateLineView;
import com.molamil.radio24syv.view.PodcastEpisodeView;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.Response;

public class ProgramDetailsFragment extends PageFragment implements
        PodcastEpisodeView.OnPodcastEpisodeViewUpdatedListener {

    public static final String ARGUMENT_PROGRAM_ID = "ProgramId";
    private static final int BATCH_SIZE = 50; // Number of podcasts to load per batch

    private OnFragmentInteractionListener mListener;
    private PodcastEpisodeView expandedView = null;

    private String programId;

    public static ProgramDetailsFragment newInstance(String programId) {
        ProgramDetailsFragment fragment = new ProgramDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_PROGRAM_ID, programId);
        fragment.setArguments(args);
        return fragment;
    }

    public ProgramDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        programId = getArguments().getString(ARGUMENT_PROGRAM_ID);
        Log.d("JJJ", "my id " + getId() + " Rid " + R.layout.fragment_program_details);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_program_details, container, false);

        TextView programIdText = (TextView) v.findViewById(R.id.program_id_text);
        programIdText.setText(programId);

        Button backButton = (Button) v.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onBackButtonPressed();
                }
            }
        });

        final ViewGroup content = (ViewGroup) v.findViewById(R.id.content);
        getPodcasts(content, BATCH_SIZE, 1);

        return v;
    }

    DateTime lastDate = null;

    private void getPodcasts(final ViewGroup content, final int amount, final int batch) {
        Log.d("JJJ", "getPodcasts id " + programId + " content " + content + " amount " + amount + " batch " + batch);
        final ViewGroup parent = (ViewGroup) content.getParent();
        final TextView loadingText = (TextView) parent.findViewById(R.id.loading_text);
        loadingText.setVisibility(View.VISIBLE);
        final Button loadButton = (Button) parent.findViewById(R.id.load_button);
        loadButton.setVisibility(View.GONE);

        RestClient.getApi().getPodcasts(programId, amount, batch).enqueue(new Callback<List<Podcast>>() {
            @Override
            public void onResponse(Response<List<Podcast>> response) {
                //for (Podcast p : response.body()) { // For some reason this does not work
                //for (Podcast p : response.body().iterator) { // For some reason this does not work neither
                for (int i = 0; i < response.body().size(); i++) {
                    Podcast p = response.body().get(i);

                    DateTime date = DateTime.parse(p.getPublishInfo().getCreatedAt());
                    boolean isNewMonth = (lastDate == null) || (!date.monthOfYear().equals(lastDate.monthOfYear())) || (!date.year().equals(lastDate.year()));
                    if (isNewMonth) {
                        lastDate = date;
                        DateLineView separator = new DateLineView(content.getContext());
                        separator.setDate(date.monthOfYear().getAsText(Locale.getDefault()), date.year().getAsText(Locale.getDefault()));
                        content.addView(separator);
                    }

                    PodcastEpisodeView v = new PodcastEpisodeView(content.getContext());
//                    PodcastEpisodeView v = (PodcastEpisodeView) getLayoutInflater(getArguments()).inflate(R.layout.view_podcast_episode, content);
                    v.setTitle(p.getTitle());
                    v.setDescription(p.getDescription().getText());
                    v.setOnPodcastEpisodeViewUpdatedListener(ProgramDetailsFragment.this);
                    content.addView(v);
                }

                if (response.body().size() == amount) {
                    loadButton.setVisibility(View.VISIBLE);
                    loadButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getPodcasts(content, BATCH_SIZE, batch + 1); // Load next batch
                        }
                    });
                }

                loadingText.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {
                loadingText.setText("Kunne ikke få forbindelse, beklager."); // TODO meaningful error messages (and check internet connection)
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    public interface OnFragmentInteractionListener extends PageFragment.OnFragmentInteractionListener {
        public void onBackButtonPressed();
    }
}
