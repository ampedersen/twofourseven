package com.molamil.radio24syv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molamil.radio24syv.R;
import com.molamil.radio24syv.RadioPlayer;
import com.molamil.radio24syv.storage.model.ProgramInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jens on 21/09/15.
 */
public class ProgramListView extends LinearLayout {

    private OnProgramListViewListener listener;
    private ArrayList<ProgramButtonView> programButtons;
    private RadioPlayer player;

    public ProgramListView(Context context) {
        super(context);
        initializeViews(context);
    }

    public ProgramListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ProgramListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(final Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_program_list, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // This is never called when instantiating with: myView = new MyView();
        // It is only called when instantiating in XML: <com.bla.bla.MyView />
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // View is now attached
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // View is now detached, and about to be destroyed
        player = null;
        programButtons = null;
    }

    public void setTitle(String title) {
        TextView titleText = (TextView) findViewById(R.id.title_text);
        if ((title != null) && !title.equals("")) {
            titleText.setText(title);
            titleText.setVisibility(View.VISIBLE); // Show title text
        } else {
            titleText.setVisibility(View.GONE); // No title
        }
    }
    public void setPrograms(List<ProgramInfo> programs) {
        programButtons = new ArrayList<>(programs.size());

        ViewGroup content = (ViewGroup) findViewById(R.id.content);
        for (ProgramInfo p : programs) {
            ProgramButtonView v = new ProgramButtonView(getContext());
            v.setProgram(p);
            v.setOnProgramButtonViewListener(onProgramButtonClicked);
            v.setRadioPlayer(player);
            programButtons.add(v);
            content.addView(v);
        }
    }

    private ProgramButtonView.OnProgramButtonViewListener onProgramButtonClicked = new ProgramButtonView.OnProgramButtonViewListener() {
        @Override
        public void OnProgramButtonViewClicked(ProgramButtonView view) {
            if (listener != null) {
                listener.OnProgramButtonViewClicked(view); // Pass on the click
            }
        }
    };

    public void setRadioPlayer(RadioPlayer player) {
        this.player = player;
        if (programButtons != null) {
            for (ProgramButtonView b : programButtons) {
                b.setRadioPlayer(player); // Assign player to buttons
            }
        }
    }

    public void setOnProgramListViewListener(OnProgramListViewListener listener) {
        this.listener = listener;
    }

    public interface OnProgramListViewListener {
        void OnProgramButtonViewClicked(ProgramButtonView view);
    }
}