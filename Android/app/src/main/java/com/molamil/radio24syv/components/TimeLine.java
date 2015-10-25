package com.molamil.radio24syv.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;

import org.joda.time.DateTime;

/**
 * Created by patriksvensson on 23/10/15.
 */
public class TimeLine extends ProgressBar
{

    public TimeLine(Context context)
    {
        super(context);
    }

    public TimeLine(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TimeLine(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void updateProgress(DateTime start, DateTime end)
    {
        long t0 = start.getMillis();
        long t1 = end.getMillis();
        long t = DateTime.now().getMillis();

        float pct = t / (t1 - t0);
        Log.i("PS", "Progress: " + pct);
    }
}
