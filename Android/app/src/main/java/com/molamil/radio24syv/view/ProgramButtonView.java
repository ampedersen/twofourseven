package com.molamil.radio24syv.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molamil.radio24syv.R;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.storage.ImageLibrary;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.storage.model.TopicInfo;

/**
 * Created by jens on 21/09/15.
 */
public class ProgramButtonView extends LinearLayout {

    private OnProgramButtonViewListener listener;
    private ProgramInfo program;

    public ProgramButtonView(Context context) {
        super(context);
        initializeViews(context);
    }

    public ProgramButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ProgramButtonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(final Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_program_button, this);

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
    }

    public void setProgram(ProgramInfo program) {
        this.program = program;

        TextView nameText = (TextView) findViewById(R.id.name_text);
        nameText.setText(program.getName());
        TextView descriptionText = (TextView) findViewById(R.id.description_text);
        descriptionText.setText(program.getDescription());
        TextView topicText = (TextView) findViewById(R.id.topic_text);
        topicText.setText(program.getTopic());

        View programButton = findViewById(R.id.program_button);
        programButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnProgramButtonViewClicked(ProgramButtonView.this);
                }
            }
        });

        TopicInfo topic = Storage.get().getTopic(program.getTopicId());
        ProgramImageView image = (ProgramImageView) findViewById(R.id.image);
        image.setImageUrl(program.getImageUrl());
        image.setTintColor(topic.getColorValue());

        RadioPlayerButton playButton = (RadioPlayerButton) findViewById(R.id.play_button);
        playButton.setProgramId(program.getProgramId());
    }

    public ProgramInfo getProgram() {
        return program;
    }

    public void setAudioUrl(String audioUrl) {
        RadioPlayerButton playButton = (RadioPlayerButton) findViewById(R.id.play_button);
        playButton.setUrl(audioUrl);
    }

    public void setRadioPlayer(RadioPlayer player) {
        RadioPlayerButton playButton = (RadioPlayerButton) findViewById(R.id.play_button);
        playButton.setRadioPlayer(player);
    }

    public void setOnProgramButtonViewListener(OnProgramButtonViewListener listener) {
        this.listener = listener;
    }

    public interface OnProgramButtonViewListener {
        void OnProgramButtonViewClicked(ProgramButtonView view);
    }
}