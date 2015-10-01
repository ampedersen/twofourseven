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
import com.molamil.radio24syv.storage.model.TopicInfo;

import org.w3c.dom.Text;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramSearchFragment extends PageFragment {

    private OnFragmentInteractionListener listener;
    private String query;

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
//        searchText.requestFocus();

        View searchButton = v.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(searchText.getText().toString());
            }
        });

        return v;
    }

    private void search(final String query) {
        Log.d("JJJ", "Search " + query);
        this.query = query;

        RestClient.getApi().search(query).enqueue(new Callback<Search>() {
            @Override
            public void onResponse(Response<Search> response) {
                if (isQueryStillRelevant(query)) {
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

        for (Result r : results) {
            TextView t = new TextView(v.getContext());
            t.setText(r.getName() + " " + r.getType() + " " + r.getId());
            content.addView(t);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ProgramCategoriesFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnFragmentInteractionListener extends PageFragment.OnFragmentInteractionListener {
    }
}
