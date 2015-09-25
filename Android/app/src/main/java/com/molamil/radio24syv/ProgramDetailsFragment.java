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
import com.molamil.radio24syv.settings.model.PodcastInfo;
import com.molamil.radio24syv.settings.model.ProgramInfo;
import com.molamil.radio24syv.view.DateLineView;
import com.molamil.radio24syv.view.PodcastEpisodeView;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.Response;

public class ProgramDetailsFragment extends PageFragment implements
        PodcastEpisodeView.OnPodcastEpisodeViewUpdatedListener {

    public static final String ARGUMENT_PROGRAM = "Program";

    private static final int BATCH_SIZE = 50; // Number of podcasts to load per batch

    private OnFragmentInteractionListener mListener;
    private PodcastEpisodeView expandedView = null;
    private DateTime lastPodcastDate = null;

    private ProgramInfo program;
    private RadioPlayer.RadioPlayerProvider radioPlayerProvider;
    private HashMap<Integer, PodcastInfo> podcastById = new HashMap<>();

    public static ProgramDetailsFragment newInstance(ProgramInfo program) {
        ProgramDetailsFragment fragment = new ProgramDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_PROGRAM, program);
        fragment.setArguments(args);
        return fragment;
    }

    public ProgramDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create Program object from arguments
        program = (ProgramInfo) getArguments().getSerializable(ARGUMENT_PROGRAM);

        Log.d("JJJ", "my id " + getId() + " Rid " + R.layout.fragment_program_details);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_program_details, container, false);

        ((TextView) v.findViewById(R.id.program_id_text)).setText(Integer.toString(program.getProgramId()));
        ((TextView) v.findViewById(R.id.title_text)).setText(program.getName());
        ((TextView) v.findViewById(R.id.topic_text)).setText(program.getTopic());
        ((TextView) v.findViewById(R.id.description_text)).setText(program.getDescription());
        ((TextView) v.findViewById(R.id.image)).setText(program.getImageUrl());

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

    private void getPodcasts(final ViewGroup content, final int amount, final int batch) {
        Log.d("JJJ", "getPodcasts id " + program.getProgramId() + " content " + content + " amount " + amount + " batch " + batch);
        final ViewGroup parent = (ViewGroup) content.getParent();
        final TextView loadingText = (TextView) parent.findViewById(R.id.loading_text);
        loadingText.setVisibility(View.VISIBLE);
        final Button loadButton = (Button) parent.findViewById(R.id.load_button);
        loadButton.setVisibility(View.GONE);

        RestClient.getApi().getPodcasts(program.getProgramId(), amount, batch).enqueue(new Callback<List<Podcast>>() {
            @Override
            public void onResponse(Response<List<Podcast>> response) {
                //for (Podcast p : response.body()) { // For some reason this does not work
                //for (Podcast p : response.body().iterator) { // For some reason this does not work neither
                for (int i = 0; i < response.body().size(); i++) {
                    PodcastInfo p = new PodcastInfo(response.body().get(i));
                    podcastById.put(p.getPodcastId(), p);

                    DateTime date = DateTime.parse(p.getDate());
                    boolean isNewMonth = (lastPodcastDate == null) || (!date.monthOfYear().equals(lastPodcastDate.monthOfYear())) || (!date.year().equals(lastPodcastDate.year()));
                    if (isNewMonth) {
                        lastPodcastDate = date;
                        DateLineView separator = new DateLineView(content.getContext());
                        separator.setDate(date.monthOfYear().getAsText(Locale.getDefault()), date.year().getAsText(Locale.getDefault()));
                        content.addView(separator);
                    }

                    PodcastEpisodeView v = new PodcastEpisodeView(content.getContext());
                    v.setTitle(p.getTitle());
                    v.setDescription(p.getDescription());
                    v.setPodcastIdAndUrl(p.getPodcastId(), RadioLibrary.getUrl(content.getContext(), p.getAudioUrl()));
                    v.setRadioPlayer(radioPlayerProvider.getRadioPlayer());
                    v.setOnPodcastEpisodeViewUpdatedListener(ProgramDetailsFragment.this);
                    content.addView(v);
                }

                boolean isBatchFull = (response.body().size() == amount);
                if (isBatchFull) {
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

    @Override
    public void onPodcastEpisodeViewDownloadClicked(PodcastEpisodeView view, int podcastId) {
        RadioLibrary.getInstance().remove(getActivity(), podcastById.get(podcastId)); // Remove podcast if it has been partially downloaded
        RadioLibrary.getInstance().download(getActivity(), program, podcastById.get(podcastId));
    }

    @Override
    public void onPodcastEpisodeViewRemoveClicked(PodcastEpisodeView view, int podcastId) {
        RadioLibrary.getInstance().remove(getActivity(), podcastById.get(podcastId));
    }

    public interface OnFragmentInteractionListener extends PageFragment.OnFragmentInteractionListener {
        public void onBackButtonPressed();
    }
}
