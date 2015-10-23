package com.molamil.radio24syv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molamil.radio24syv.R;
import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.storage.model.BroadcastInfo;

import java.util.Locale;

/**
 * Created by jens on 21/09/15.
 */
public class ProgramScheduleButton extends LinearLayout {

    private OnProgramScheduleButtonViewListener listener;
    private BroadcastInfo broadcast;

    public ProgramScheduleButton(Context context) {
        super(context);
        initializeViews(context);
    }

    public ProgramScheduleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ProgramScheduleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(final Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_program_schedule_button, this);

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

    public void setBroadcast(BroadcastInfo broadcast) {
        this.broadcast = broadcast;


        TextView timeText = (TextView) findViewById(R.id.time_text);
        if (broadcast.isPlayingNow()) {
            timeText.setText(R.string.playing_now);
        } else {
            timeText.setText(String.format(Locale.US, "%s - %s", RestClient.getLocalTime(broadcast.getTimeBegin()), RestClient.getLocalTime(broadcast.getTimeEnd())));
        }

        TextView nameText = (TextView) findViewById(R.id.name_text);
        nameText.setText(broadcast.getName());
        TextView topicText = (TextView) findViewById(R.id.topic_text);
        topicText.setText(broadcast.getTopic());

        /*
        View programButton = findViewById(R.id.program_button);
        programButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnProgramScheduleButtonClicked(ProgramScheduleButton.this);
                }
            }
        });
        */

        CheckBox notificationButton = (CheckBox) findViewById(R.id.notification_button);
        if (broadcast.isPlayingNow()) {
            notificationButton.setVisibility(View.INVISIBLE); // Invisible will not render the graphics but still take up layout space
        } else {
            notificationButton.setVisibility(View.VISIBLE);
            notificationButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnProgramScheduleNotificationButtonClicked(ProgramScheduleButton.this, (CheckBox) v);
                    }
                }
            });
        }
    }

    public BroadcastInfo getBroadcast() {
        return broadcast;
    }

    public void setNotificationEnabled(boolean enabled) {
        CheckBox notificationButton = (CheckBox) findViewById(R.id.notification_button);
        notificationButton.setChecked(enabled);
    }

    public boolean getNotificationEnabled() {
        CheckBox notificationButton = (CheckBox) findViewById(R.id.notification_button);
        return notificationButton.isChecked();
    }

    public void setOnProgramScheduleButtonViewListener(OnProgramScheduleButtonViewListener listener) {
        this.listener = listener;
    }

    public interface OnProgramScheduleButtonViewListener {
        void OnProgramScheduleButtonClicked(ProgramScheduleButton view);
        void OnProgramScheduleNotificationButtonClicked(ProgramScheduleButton view, CheckBox clickedView);
    }
}