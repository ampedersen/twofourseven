package com.molamil.radio24syv;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.Result;
import com.molamil.radio24syv.api.model.Search;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.view.ProgramButtonView;
import com.molamil.radio24syv.view.ProgramListView;

import org.apache.http.auth.BasicUserPrincipal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramSearchFragment extends PageFragment {

    private enum State { NONE, READY, BUSY }

    private OnFragmentInteractionListener listener;
    private String query;
    private RadioPlayer.RadioPlayerProvider radioPlayerProvider;
    private int resultCount = 0;
    private State state;

    public ProgramSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_program_search, container, false);

        final EditText searchText = (EditText) v.findViewById(R.id.search_text);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(v.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });
        searchText.requestFocus();

        View searchButton = v.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(searchText.getText().toString());
            }
        });

        state = State.READY;

        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        MainActivity a = getMainActivity();
        if (a != null) {
            boolean isKeyboardNedeed = isVisibleToUser && (resultCount == 0); // Hide keyboard when the fragment is not visible and no search results are showing
            a.setKeyboardVisible(isKeyboardNedeed);
        }
    }

    private void search(final String query) {
        Log.d("JJJ", "Search " + query);
        this.query = query;

        boolean isQueryInvalid = (query == null) || (query.length() == 0); // The search API does not like empty queries
        if (isQueryInvalid) {
            state = State.READY;
            showSearchResults(null);
        } else {
            state = State.BUSY;

            RestClient.getApi().search(query, "program", 50, 0).enqueue(new Callback<Search>() {
                @Override
                public void onResponse(Response<Search> response) {
                    if (response.body() == null) {
                        return;
                    }
                    if (isQueryStillRelevant(query)) {
                        state = State.READY;
                        if (listener != null) {
                            listener.onError(null);
                        }
                        Search search = response.body();
                        showSearchResults(search.getResults());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    if (isQueryStillRelevant(query)) {
                        state = State.READY;
                        if (listener != null) {
                            listener.onError(t.getLocalizedMessage());
                        }
                        showSearchResults(null);
                        Log.d("JJJ", "fail " + t.getMessage());
                        t.printStackTrace();
                    }
                }
            });
        }

        updateStatus();
    }

    private boolean isQueryStillRelevant(String query) {
        return ProgramSearchFragment.this.query.equalsIgnoreCase(query); // Is the user still looking for this?
    }

    private void showSearchResults(List<Result> results) {
        View v = getView();
        if (v == null) {
            return;
        }

        ViewGroup content = (ViewGroup) v.findViewById(R.id.content);
        content.removeAllViews();

        if (results == null) {
            resultCount = 0;
        } else {
            ArrayList<ProgramInfo> programs = new ArrayList<>();
            for (Result r : results) {
                boolean isProgram = r.getType().equalsIgnoreCase("program");
                if (isProgram) {
                    //ProgramInfo program = Storage.get().getProgram(r.getId()); // This ID is from an external search provider and does not make sense neither to us nor the API
                    String programSlug = RestClient.getProgramSlugFromUrl(r.getUrl()); // According to Radio24syv this is the way to get the program identifier (slug) from a search result
                    ProgramInfo program = Storage.get().getProgram(programSlug);
                    if (program != null) {
                        programs.add(program);
                    } else {
                        Log.d("JJJ", "Program not found for slug " + programSlug); // Search can return programs that getPrograms() does not..!
                    }
                }
            }
            addPrograms(content, programs);
            resultCount = programs.size();
        }

        updateStatus();
    }

    private void addPrograms(ViewGroup content, List<ProgramInfo> programs) {
        ProgramListView list = new ProgramListView(getActivity(), programs, radioPlayerProvider.getRadioPlayer());
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.onProgramSelected((ProgramInfo) parent.getAdapter().getItem(position));
                }
            }
        });
        content.addView(list);
    }

    private void updateStatus() {
        View v = getView();
        if (v == null) {
            return;
        }

        TextView statusText = (TextView) v.findViewById(R.id.status_text);
        boolean isShowingResults = (resultCount > 0);
        switch (state) {

            case READY:
                if (isShowingResults) {
                    statusText.setVisibility(View.GONE);
                    getMainActivity().setKeyboardVisible(false); // Hide keyboard to show search results
                } else {
                    statusText.setVisibility(View.VISIBLE);
                    boolean isSearchFinished = (query != null) && (query.length() > 0);
                    if (isSearchFinished) {
                        statusText.setText(R.string.search_no_results);
                    } else {
                        statusText.setText(R.string.search_instructions);
                    }
                }
                break;

            case BUSY:
                if (isShowingResults) {
                    statusText.setVisibility(View.GONE);
                } else {
                    statusText.setVisibility(View.VISIBLE);
                    statusText.setText(R.string.search_busy);
                }
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ProgramSearchFragment.OnFragmentInteractionListener");
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

    public interface OnFragmentInteractionListener extends PageFragment.OnFragmentInteractionListener {
        public void onProgramSelected(ProgramInfo program);
    }
}
