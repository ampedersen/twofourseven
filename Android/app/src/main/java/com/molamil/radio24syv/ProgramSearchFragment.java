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
import android.widget.EditText;
import android.widget.TextView;

import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.Result;
import com.molamil.radio24syv.api.model.Search;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.view.ProgramButtonView;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramSearchFragment extends PageFragment {

    private OnFragmentInteractionListener listener;
    private String query;
    private RadioPlayer.RadioPlayerProvider radioPlayerProvider;
    private int results = 0;

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

        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        MainActivity a = getMainActivity();
        if (a != null) {
            boolean isKeyboardNedeed = isVisibleToUser && (results == 0);
            a.setKeyboardVisible(isKeyboardNedeed);
        }
    }

    private void updateStatus() {
        View v = getView();
        if (v == null) {
            return;
        }

        TextView statusText = (TextView) v.findViewById(R.id.status_text);
        if (results > 0) {
            statusText.setVisibility(View.GONE);
            getMainActivity().setKeyboardVisible(false); // Hide keyboard to show results
        } else {
            statusText.setVisibility(View.VISIBLE);
            if ((query != null) && (query.length() > 0)) {
                statusText.setText(R.string.search_no_results);
            } else {
                statusText.setText(R.string.search_instructions);
            }
        }
    }

    private void search(final String query) {
        Log.d("JJJ", "Search " + query);
        this.query = query;
        results = 0;
        showSearchResults(null);

        if ((query == null) || (query.length() == 0)) {
            return; // Return, the search API does not like empty queries
        }

        RestClient.getApi().search(query, "program", 50, 0).enqueue(new Callback<Search>() {
            @Override
            public void onResponse(Response<Search> response) {
                if (isQueryStillRelevant(query)) {
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
                    if (listener != null) {
                        listener.onError(t.getLocalizedMessage());
                    }
                    Log.d("JJJ", "fail " + t.getMessage());
                    t.printStackTrace();
                }
            }
        });
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
            this.results = 0;
        } else {
            for (Result r : results) {
                boolean isProgram = r.getType().equalsIgnoreCase("program");
                if (isProgram) {
                    //ProgramInfo program = Storage.get().getProgram(r.getId()); // This ID is from an external search provider and does not make sense to us and the API
                    String programSlug = RestClient.getProgramSlugFromUrl(r.getUrl()); // According to Radio24syv this is the way to get the program identifier (slug) from a search result
                    ProgramInfo program = Storage.get().getProgram(programSlug);
                    if (program != null) {
                        ProgramButtonView button = new ProgramButtonView(content.getContext());
                        button.setProgram(program);
                        button.setRadioPlayer(radioPlayerProvider.getRadioPlayer());
                        button.setOnProgramButtonViewListener(new ProgramButtonView.OnProgramButtonViewListener() {
                            @Override
                            public void OnProgramButtonViewClicked(ProgramButtonView view) {
                                if (listener != null) {
                                    listener.onProgramSelected(view.getProgram());
                                }
                            }
                        });
                        content.addView(button);
                    } else {
                        Log.d("JJJ", "Program not found for slug " + programSlug); // Search can return programs that getPrograms() does not..!
                    }
                }
            }
            this.results = results.size();
        }

        updateStatus();
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
    }

    public interface OnFragmentInteractionListener extends PageFragment.OnFragmentInteractionListener {
        public void onProgramSelected(ProgramInfo program);
    }
}
