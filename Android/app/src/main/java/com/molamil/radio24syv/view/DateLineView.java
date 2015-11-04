package com.molamil.radio24syv.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.Image;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molamil.radio24syv.R;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.storage.Storage;


import org.joda.time.DateTime;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by jens on 21/09/15.
 */
public class DateLineView extends LinearLayout {

    private boolean whiteBackground;

    public DateLineView(Context context) {
        super(context);
        initializeViews(context);
    }

    public DateLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);

        // Apply attributes from XML
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RadioPlayerButton,
                0, 0);

        try {
            setWhiteBackground(a.getBoolean(R.styleable.DateLineView_whiteBackground, false));
        } finally {
            a.recycle();
        }
    }

    public DateLineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_date_line, this);
        updateColors();
    }

    public void setDate(String month, String year) {
        TextView v = (TextView) findViewById(R.id.date_text);
        v.setText(String.format(Locale.US, "%s %s", month, year));
    }

    public void setDate(DateTime previousDate, DateTime nextDate) {

        Locale locale = new Locale("da", "DK");
        TextView v = (TextView) findViewById(R.id.date_text);
        //String s = DateUtils.getRelativeTimeSpanString(nextDate.getMillis(), previousDate.getMillis(), DateUtils.DAY_IN_MILLIS, 0).toString();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(nextDate.getMillis());
        String s = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
        v.setText(s);
    }

    public boolean isWhiteBackground() {
        return whiteBackground;
    }

    public void setWhiteBackground(boolean whiteBackground) {
        this.whiteBackground = whiteBackground;
        updateColors();
    }

    private void updateColors() {
        updateColor(findViewById(R.id.line_left), R.color.radio_black, R.color.radio_gray);
        updateColor(findViewById(R.id.date_text), R.color.radio_black, R.color.radio_white);
        updateColor(findViewById(R.id.line_right), R.color.radio_black, R.color.radio_gray);
    }

    private void updateColor(View v, int whiteBackgroundColorId, int blackBackgroundColorId) {
        int colorId;
        if (isWhiteBackground()) {
            colorId = whiteBackgroundColorId;
        } else {
            colorId = blackBackgroundColorId;
        }
        if (v instanceof TextView) {
            ((TextView) v).setTextColor(getResources().getColor(colorId));
        } else if (v instanceof ImageView) {
            ((ImageView) v).setImageDrawable(getResources().getDrawable(colorId));
        }
        v.postInvalidate(); // Redraw
    }
}
