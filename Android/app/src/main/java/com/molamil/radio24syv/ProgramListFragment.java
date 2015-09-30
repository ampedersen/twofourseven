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
import com.molamil.radio24syv.api.model.TopicColors;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.storage.model.TopicInfo;
import com.molamil.radio24syv.view.ProgramButtonView;
import com.molamil.radio24syv.view.ProgramCategoryButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;


public class ProgramListFragment extends PageFragment {

    public static final String ARGUMENT_CATEGORY = "Category";
    public static final String ARGUMENT_TOPIC_ID = "TopicId";

    private enum Sorting { UNSORTED, SORT_BY_NAME, SORT_BY_TOPIC };

    private OnFragmentInteractionListener mListener;
    private RadioPlayer.RadioPlayerProvider radioPlayerProvider;

    private int category;
    private TopicInfo topic;

    public ProgramListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            category = getArguments().getInt(ARGUMENT_CATEGORY, ProgramCategoryButton.CATEGORY_RECOMMENDED);
            topic = (TopicInfo) getArguments().getSerializable(ARGUMENT_TOPIC_ID);
        }
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
        getTopicColors(content);
        updateProgramList(v);

        return v;
    }

    public void showProgramCategory(int category, TopicInfo topic) {
        this.category = category;
        this.topic = topic;

        View v = getView();
        if (v != null) {
            updateProgramList(v);
        }
    }

    private void updateProgramList(View v) {
        // The category button is only used as a label
        ProgramCategoryButton categoryButton = (ProgramCategoryButton) v.findViewById(R.id.category_button);
        categoryButton.setCategoryAndTopic(category, topic);
        categoryButton.setSelectedIndicatorVisible(false);
        categoryButton.adjustTitleColorForBlackBackground();

        final ViewGroup content = (ViewGroup) v.findViewById(R.id.content);
        getPrograms(content);
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
                    Log.d("JJJ", "Showing updated program list");
                    showPrograms(content, programs);
                    loadingText.setVisibility(View.GONE);
                }
                Storage.get().addPrograms(programs);
            }

            @Override
            public void onFailure(Throwable t) {
                if (!isCached) {
                    getMainActivity().onError("Kunne ikke få forbindelse, beklager."); // TODO meaningful error messages (and check internet connection)
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
        Log.d("JJJ", "showPrograms category " + category + " topic " + (topic == null ? "null" : topic.getTopicId()));
        ArrayList<ProgramInfo> filteredPrograms = new ArrayList<>(programs);

        boolean matchActive = true;
        String matchTopicId = null;
        Sorting sorting = Sorting.UNSORTED;

        switch (category) {
            case ProgramCategoryButton.CATEGORY_RECOMMENDED:
                sorting = Sorting.SORT_BY_NAME;
                break;

            case ProgramCategoryButton.CATEGORY_ALL_BY_NAME:
                sorting = Sorting.SORT_BY_NAME;
                break;

            case ProgramCategoryButton.CATEGORY_ALL_BY_TOPIC:
                sorting = Sorting.SORT_BY_TOPIC;
                break;

            case ProgramCategoryButton.CATEGORY_TOPIC_BY_NAME:
                matchTopicId = topic.getTopicId();
                sorting = Sorting.SORT_BY_NAME;
                break;

            case ProgramCategoryButton.CATEGORY_INACTIVE_BY_NAME:
                matchActive = false;
                sorting = Sorting.SORT_BY_NAME;
                break;
        }

        filteredPrograms = getFilteredPrograms(filteredPrograms, matchActive, matchTopicId);
        filteredPrograms = getSortedPrograms(filteredPrograms, sorting);

        // TODO recommended programs is a special case
        if (category == ProgramCategoryButton.CATEGORY_RECOMMENDED) {
            while (filteredPrograms.size() > 3) {
                filteredPrograms.remove(filteredPrograms.size() - 1);
            }
        }

        content.removeAllViews(); // Clear old content
        addPrograms(content, filteredPrograms);
    }

    private static ArrayList<ProgramInfo> getFilteredPrograms(ArrayList<ProgramInfo> programs, boolean isActive, String matchTopicId) {
        ArrayList<ProgramInfo> filteredPrograms = new ArrayList<>();
        for (ProgramInfo p : programs) {
            if ((p.getActive() == isActive) && ((matchTopicId == null) || p.getTopicId().equalsIgnoreCase(matchTopicId))) {
                filteredPrograms.add(p);
            }
        }
        return filteredPrograms;
    }

    private static ArrayList<ProgramInfo> getSortedPrograms(ArrayList<ProgramInfo> programs, final Sorting sorting) {
        Collections.sort(programs, new Comparator<ProgramInfo>() {
            @Override
            public int compare(ProgramInfo lhs, ProgramInfo rhs) {
                if (sorting == Sorting.SORT_BY_NAME) {
                    int result = lhs.getTopic().compareToIgnoreCase(rhs.getTopic());
                    if (result != 0) {
                        return result; // If not same topic
                    }
                }
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
        return programs;
    }

    private void addPrograms(final ViewGroup content, List<ProgramInfo> programs) {
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

    private void getTopicColors(final ViewGroup content) {
        // Read list instantly from local storage (if available).
        final Collection<TopicInfo> topics = Storage.get().getTopics();
        final boolean isCached = (topics != null) && (topics.size() > 0);

        Log.d("JJJ", "Updating topic colors");
        // Update topic colors before updating program list UI
        RestClient.getApi().getTopicColors().enqueue(new Callback<TopicColors>() {
            @Override
            public void onResponse(Response<TopicColors> response) {
                TopicColors colors = response.body();
                ArrayList<TopicInfo> topics = new ArrayList<TopicInfo>();
                // This is not dynamic at all but this is the API we got...
                topics.add(new TopicInfo("aktualitet", colors.getAktualitet()));
                topics.add(new TopicInfo("andet", colors.getAndet()));
                topics.add(new TopicInfo("debat", colors.getDebat()));
                topics.add(new TopicInfo("kultur", colors.getKultur()));
                topics.add(new TopicInfo("nyheder", colors.getNyheder()));
                topics.add(new TopicInfo("reportage", colors.getReportage()));
                topics.add(new TopicInfo("satire", colors.getSatire()));
                topics.add(new TopicInfo("sport", colors.getSport()));
                Storage.get().addTopics(topics);
            }

            @Override
            public void onFailure(Throwable t) {
                if (!isCached) {
                    getMainActivity().onError("Kunne ikke få forbindelse, beklager."); // TODO meaningful error messages (and check internet connection)
                    Log.d("JJJ", "fail " + t.getMessage());
                    t.printStackTrace();
                }
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
