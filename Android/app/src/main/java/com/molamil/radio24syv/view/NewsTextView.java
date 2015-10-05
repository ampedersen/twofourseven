package com.molamil.radio24syv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molamil.radio24syv.R;

/**
 * Created by jens on 21/09/15.
 */
public class NewsTextView extends LinearLayout {

    public NewsTextView(Context context) {
        super(context);
        initializeViews(context);
    }

    public NewsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public NewsTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_news_text, this);
    }

    public void setText(int textId) {
        setText(getResources().getString(textId));
    }

    public void setText(String text) {
        TextView v = (TextView) findViewById(R.id.news_text);
        v.setText(text);
    }
}
