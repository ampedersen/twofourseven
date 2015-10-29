package com.molamil.radio24syv.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

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

    public void setProgress(DateTime start, DateTime end)
    {
        long t0 = start.getMillis();
        long t1 = end.getMillis();
        long t = DateTime.now().getMillis();

        float duration = t1 - t0;
        float time = t - t0;
        if(duration == 0)
        {
            setProgress(0);
        }

        float pct = time / duration;
        setProgress(pct);


    }

    public void setProgress(float pct)
    {
        int progress =  Math.max(0, Math.min(getMax(), (int)(pct * 100)));
        setProgress(progress);
    }
}
