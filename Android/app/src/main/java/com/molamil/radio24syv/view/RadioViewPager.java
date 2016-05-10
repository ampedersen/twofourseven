package com.molamil.radio24syv.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by jens on 08/09/15.
 */
public class RadioViewPager extends ViewPager {

    private boolean isPagingEnabled = true;

    public RadioViewPager(Context context) {
        super(context);
    }

    public RadioViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // This works, except for crash-bug in Android's ViewPager...
        //return this.isPagingEnabled && super.onTouchEvent(event);

        // ..so have to do it like this: https://github.com/chrisbanes/PhotoView/issues/31
        if (!this.isPagingEnabled) {
            return false;
        } else {
            try {
                return super.onTouchEvent(event);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace(); // Catch "IllegalArgumentException: pointerIndex out of range" MotionEvent.nativeGetAxisValue
            }
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // This works, except for crash-bug in Android's ViewPager...
        //return this.isPagingEnabled && super.onInterceptTouchEvent(event);

        // ..so have to do it like this: https://github.com/chrisbanes/PhotoView/issues/31#issuecomment-19803926
        if (!this.isPagingEnabled) {
            return false;
        } else {
            try {
                return super.onInterceptTouchEvent(event);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace(); // Catch "IllegalArgumentException: pointerIndex out of range" MotionEvent.nativeGetAxisValue
            }
            return false;
        }
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }

    public boolean isPagingEnabled() {
        return isPagingEnabled;
    }

}
