package com.molamil.radio24syv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molamil.radio24syv.R;

import org.w3c.dom.Text;

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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Sets the images for the previous and next buttons. Uses
        // built-in images so you don't need to add images, but in
        // a real application your images should be in the
        // application package so they are always available.
//        mPreviousButton = (Button) this
//                .findViewById(R.id.sidespinner_view_previous);
//        mPreviousButton
//                .setBackgroundResource(android.R.drawable.ic_media_previous);
//
//        mNextButton = (Button)this
//                .findViewById(R.id.sidespinner_view_next);
//        mNextButton
//                .setBackgroundResource(android.R.drawable.ic_media_next);
    }

    public void setDate(String month, int year) {
        TextView v = (TextView) findViewById(R.id.date_text);
        v.setText(String.format(Locale.US, "%s %d", month, year));
    }
}
