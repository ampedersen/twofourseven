package com.molamil.radio24syv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molamil.radio24syv.R;


import java.util.Locale;

/**
 * Created by jens on 21/09/15.
 */
public class DateLineView extends LinearLayout {

    public DateLineView(Context context) {
        super(context);
        initializeViews(context);
    }

    public DateLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public DateLineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_date_line, this);
    }

    public void setDate(String month, String year) {
        TextView v = (TextView) findViewById(R.id.date_text);
        v.setText(String.format(Locale.US, "%s %s", month, year));
    }
}
