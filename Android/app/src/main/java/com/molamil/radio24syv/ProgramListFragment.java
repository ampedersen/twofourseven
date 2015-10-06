package com.molamil.radio24syv;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.ConciseProgram;
import com.molamil.radio24syv.api.model.Program;
import com.molamil.radio24syv.api.model.TopicColors;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.storage.model.TopicInfo;
import com.molamil.radio24syv.view.ProgramButtonView;
import com.molamil.radio24syv.view.ProgramCategoryButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import retrofit.Callback;
import retrofit.Response;


public class ProgramListFragment extends PageFragment {

    public static final String ARGUMENT_CATEGORY = "Category";
    public static final String ARGUMENT_TOPIC_ID = "TopicId";

    private enum Sorting { UNSORTED, SORT_BY_NAME, SORT_BY_TOPIC };

    private OnFragmentInteractionListener listener;
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

//    public class Utilities {
//        /**
//         * Hides the on-screen keyboard. NOTE: Only works when called from an Activity. Does not work when called from a Fragment (this won't work because you'll be passing a reference to the Fragment's host Activity, which will have no focused control while the Fragment is shown)
//         * http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
//         */
//        public static void hideKeyboard(Activity activity) {
//            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//            //Find the currently focused view, so we can grab the correct window token from it.
//            View view = activity.getCurrentFocus();
//            //If no view currently has focus, create a new one, just so we can grab a window token from it
//            if(view == null) {
//                view = new View(activity);
//            }
//            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_program_list, container, false);

        Button categoriesButton = (Button)v.findViewById(R.id.categories_button);
        categoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onShowSidePage(OnFragmentInteractionListener.Side.SHOW_LEFT);
                }
            }
        });
        Button searchButton = (Button) v.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onShowSidePage(OnFragmentInteractionListener.Side.SHOW_RIGHT);
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
                if (listener != null) {
                    listener.onError(null);
                }
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
                    if (listener != null) {
                        listener.onError(t.getLocalizedMessage());
                    }
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

        // Recommended programs is a special case
        if (category == ProgramCategoryButton.CATEGORY_RECOMMENDED) {
            content.removeAllViews(); // Clear old content

            // Show recent programs
            final List<ProgramInfo> recentPrograms = Storage.get().getPlayerHistory(4); // Get one more than we need because if a program is playing it should not be shown
            if (recentPrograms.size() > 0) {
                // Remove the extra program
                int playingProgramId = radioPlayerProvider.getRadioPlayer().getProgramId();
                ProgramInfo removeProgram = null;
                for (ProgramInfo p : recentPrograms) {
                    if (p.getProgramId() == playingProgramId) {
                        removeProgram = p; // Remove currently playing program
                        break;
                    }
                }
                if ((removeProgram == null) && (recentPrograms.size() > 3)) {
                    removeProgram = recentPrograms.get(recentPrograms.size() - 1); // Remove last program
                }
                if (removeProgram != null) {
                    Log.d("JJJ", "Remove recent " + removeProgram.getName());
                    recentPrograms.remove(removeProgram);
                }

                // Show programs (if we still have any left)
                if (recentPrograms.size() > 0) {
                    TextView t = new TextView(content.getContext());
                    t.setText(R.string.programs_recent);
                    content.addView(t);
                    addPrograms(content, recentPrograms);
                }
            }

            // The recent program IDs are needed for easy lookup later
            List<Integer> recentProgramIds = new ArrayList<>();
            for (ProgramInfo p : recentPrograms) {
                recentProgramIds.add(p.getProgramId());
            }

            // Count how many times each program is related
            List<ProgramInfo> allRecentPrograms = Storage.get().getPlayerHistory(Integer.MAX_VALUE);
            final HashMap<Integer, Integer> relationsByProgramId = new HashMap<>();
            for (ProgramInfo p : allRecentPrograms) {
                for (ProgramInfo related : Storage.get().getRelatedPrograms(p.getProgramId(), Integer.MAX_VALUE)) {
                    int id = related.getProgramId();
                    boolean isRecentProgram = (recentProgramIds.contains(id)); // Do not recommend recent programs because that looks silly
                    if (!isRecentProgram) {
                        if (!relationsByProgramId.containsKey(id)) {
                            relationsByProgramId.put(id, 0);
                        }
                        relationsByProgramId.put(id, relationsByProgramId.get(id) + 1); // Count another relation to this program
                    }
                }
            }

            // Sort related programs by how many times they are related
            List<Integer> relatedProgramIds = new ArrayList<>();
            relatedProgramIds.addAll(relationsByProgramId.keySet());
            final Random random = new Random();
            Collections.sort(relatedProgramIds, new Comparator<Integer>() {
                @Override
                public int compare(Integer lhs, Integer rhs) {
                    int result = relationsByProgramId.get(lhs).compareTo(relationsByProgramId.get(rhs)); // Sort by number of relations
                    if (result == 0) {
                        // Random pick if count is the same. Helps making the list appear different if recommendations are even.
                        if (random.nextBoolean()) {
                            result = 1;
                        } else {
                            result = -1;
                        }
                    }
                    return result;
                }
            });

            // Recommend the three most related programs
            List<ProgramInfo> recommendedPrograms = new ArrayList<>();
            for (int i = 0; (i < 3) && (i < relatedProgramIds.size()); i++) {
                ProgramInfo p = Storage.get().getProgram(relatedProgramIds.get(i));
                recommendedPrograms.add(p);
            }

            // Show recommended programs
            if (recommendedPrograms.size() > 0) {
                TextView t = new TextView(content.getContext());
                t.setText(R.string.programs_recomended);
                content.addView(t);
                addPrograms(content, recommendedPrograms);
            }

            // Show popular programs
            RestClient.getApi().getPopularPrograms(3).enqueue(new Callback<List<Program>>() {
                @Override
                public void onResponse(Response<List<Program>> response) {
                    if (listener != null) {
                        listener.onError(null);
                    }
                    if ((response.body() == null) || (response.body().size() == 0)) {
                        return;
                    }
                    ArrayList<ProgramInfo> popularPrograms = new ArrayList<ProgramInfo>(response.body().size());
                    for (Program program : response.body()) {
                        popularPrograms.add(new ProgramInfo(program));
                    }
                    TextView t = new TextView(content.getContext());
                    t.setText(R.string.programs_popular);
                    content.addView(t);
                    addPrograms(content, popularPrograms);
                }

                @Override
                public void onFailure(Throwable t) {
                    if (listener != null) {
                        listener.onError(t.getLocalizedMessage());
                    }
                    Log.d("JJJ", "fail " + t.getMessage());
                    t.printStackTrace();
                }
            });

        } else {
            ArrayList<ProgramInfo> filteredPrograms = new ArrayList<>(programs);
            filteredPrograms = getFilteredPrograms(filteredPrograms, matchActive, matchTopicId);
            filteredPrograms = getSortedPrograms(filteredPrograms, sorting);

            content.removeAllViews(); // Clear old content
            addPrograms(content, filteredPrograms);
        }
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
                if (sorting == Sorting.SORT_BY_TOPIC) {
                    int result = lhs.getTopic().compareToIgnoreCase(rhs.getTopic());
                    if (result != 0) {
                        return result; // If not same topic, else it falls through to comparing names below
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
                    if (listener != null) {
                        listener.onProgramSelected(p);
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
                if (listener != null) {
                    listener.onError(null);
                }
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
                    if (listener != null) {
                        listener.onError(t.getLocalizedMessage());
                    }
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
    }

    public interface OnFragmentInteractionListener extends PageFragment.OnFragmentInteractionListener {
        public void onProgramSelected(ProgramInfo program);
    }
}
