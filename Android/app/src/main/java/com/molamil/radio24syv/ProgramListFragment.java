package com.molamil.radio24syv;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ProgramListFragment extends PageFragment {

    OnFragmentInteractionListener mListener;

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
        someProgramButton.setTag(R.id.action_bar, "3843763"); // TODO custom button for this instead of tag ugliness
        someProgramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onProgramSelected((String) v.getTag(R.id.action_bar));
                }
            }
        });

        return v;
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

    public interface OnFragmentInteractionListener extends PageFragment.OnFragmentInteractionListener {
        public void onProgramSelected(String programId);
    }
}
