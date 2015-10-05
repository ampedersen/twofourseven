package com.molamil.radio24syv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molamil.radio24syv.R;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.Locale;

/**
 * Created by jens on 21/09/15.
 */
public class NewsDateView extends LinearLayout {

    public NewsDateView(Context context) {
        super(context);
        initializeViews(context);
    }

    public NewsDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public NewsDateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_news_date, this);
    }

    public void setDate(String time, String date) {
        TextView timeText = (TextView) findViewById(R.id.time_text);
        timeText.setText(time);

        TextView dateText = (TextView) findViewById(R.id.date_text);
        dateText.setText(date);
    }
}
