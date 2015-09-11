package com.molamil.radio24syv;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ProgramDetailsFragment extends PageFragment {

    public static final String ARGUMENT_PROGRAM_ID = "ProgramId";

    OnFragmentInteractionListener mListener;
    String programId;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        public void onBackButtonPressed();
    }
}
