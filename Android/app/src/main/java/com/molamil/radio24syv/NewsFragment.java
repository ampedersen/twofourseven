package com.molamil.radio24syv;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.Podcast;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.storage.RadioLibrary;
import com.molamil.radio24syv.storage.model.PodcastInfo;
import com.molamil.radio24syv.view.NewsDateView;
import com.molamil.radio24syv.view.NewsTextView;
import com.molamil.radio24syv.view.RadioPlayerButton;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment {

    private OnFragmentInteractionListener listener;
    private RadioPlayer.RadioPlayerProvider radioPlayerProvider;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_news, container, false);

        showLoadingText(v, true);

        RestClient.getApi().getLatestNewsPodcasts().enqueue(new Callback<List<Podcast>>() {
            @Override
            public void onResponse(Response<List<Podcast>> response) {
                if (listener != null) {
                    listener.onError(null);
                }
                showLoadingText(v, false);
                List<Podcast> podcasts = response.body();
                PodcastInfo p = null;
                if ((podcasts != null) && (podcasts.size() > 0)) {
                    p = new PodcastInfo(podcasts.get(0)); // There is only one entry
                }
                showPodcast(p);
            }

            @Override
            public void onFailure(Throwable t) {
                if (listener != null) {
                    listener.onError(t.getLocalizedMessage());
                }
                showLoadingText(v, false);
                Log.d("JJJ", "fail " + t.getMessage());
                t.printStackTrace();
            }
        });

        return v;
    }

    private void showLoadingText(View v, boolean show) {
        View loadingText = v.findViewById(R.id.loading_text);
        int visibility;
        if (show) {
            visibility = View.VISIBLE;
        } else {
            visibility = View.GONE;
        }
        loadingText.setVisibility(visibility);
    }

    private void showPodcast(PodcastInfo podcast) {
        View v = getView();
        if (v == null) {
            return;
        }

        ViewGroup content = (ViewGroup) v.findViewById(R.id.content);
        content.removeAllViews();

        if (podcast != null) {
            // Date
            NewsDateView date = new NewsDateView(v.getContext());
            String dateString = podcast.getDate();
            date.setDate(RestClient.getLocalTime(dateString), RestClient.getLocalDate(dateString));
            content.addView(date);

            // News items
            String[] newsItems = podcast.getDescription().split("\n");
            boolean isNewsEmpty = (newsItems.length < 1); // Skip the first item which is "Nyheder fra Radio24syv"
            if (!isNewsEmpty) {
                for (int i = 1; i < newsItems.length; i++) {
                    String s = newsItems[i];
                    if (s != null) {
                        s = s.trim();
                        if (s.length() > 0) {
                            NewsTextView newsText = new NewsTextView(v.getContext());
                            newsText.setText(s);
                            content.addView(newsText);
                        }
                    }
                }
            } else {
                NewsTextView newsText = new NewsTextView(v.getContext());
                newsText.setText(R.string.news_empty);
                content.addView(newsText);
            }
        }

        RadioPlayerButton playButton = (RadioPlayerButton) v.findViewById(R.id.play_button);
        if (podcast != null) {
            playButton.setVisibility(View.VISIBLE);
            playButton.setProgramId(podcast.getProgramId());
            String url = RadioLibrary.getUrl(getView().getContext(), podcast.getAudioUrl());
            playButton.setUrl(url);
            playButton.setRadioPlayer(radioPlayerProvider.getRadioPlayer());
        } else {
            playButton.setVisibility(View.GONE);
        }
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
            radioPlayerProvider = (RadioPlayer.RadioPlayerProvider) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PlayerFragment.RadioPlayerProvider");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        radioPlayerProvider = null;
    }

    public interface OnFragmentInteractionListener extends PageFragment.OnFragmentInteractionListener {
    }

}
