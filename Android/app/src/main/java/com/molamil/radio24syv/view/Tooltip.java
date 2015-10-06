package com.molamil.radio24syv.view;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.molamil.radio24syv.R;


/**
 * Based on: http://shardulprabhu.blogspot.ro/2012/08/blog-post_29.html
 * Created by jens on 06/10/15.
 */
public class Tooltip {
    protected WindowManager mWindowManager;
    protected Context mContext;
    protected PopupWindow mWindow;
    private TextView mHelpTextView;
    private ImageView mUpImageView;
    private ImageView mDownImageView;
    protected View mView;
    protected Drawable mBackgroundDrawable = null;
    protected ShowListener showListener;
    private boolean isDimissed = false;

    public Tooltip(Context context, String text, int viewResource) {
        mContext = context;
        mWindow = new PopupWindow(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(layoutInflater.inflate(viewResource, null));
        mHelpTextView = (TextView) mView.findViewById(R.id.text);
        mUpImageView = (ImageView) mView.findViewById(R.id.arrow_up);
        mDownImageView = (ImageView) mView.findViewById(R.id.arrow_down);
        mHelpTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public Tooltip(Context context) {
        this(context, "", R.layout.view_tooltip);
    }

    public Tooltip(Context context, String text) {
        this(context);
        setText(text);
    }

    public void show(View anchor) {
        preShow();
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] + anchor.getHeight());
        //mView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); // This does not work
        int measureSpec = View.MeasureSpec.makeMeasureSpec(0 /* any */, View.MeasureSpec.UNSPECIFIED);
        mView.measure(measureSpec, measureSpec);
        int rootHeight = mView.getMeasuredHeight();
        int rootWidth = mView.getMeasuredWidth();
        Point screenSize = new Point();
        mWindowManager.getDefaultDisplay().getSize(screenSize);
        final int screenWidth = screenSize.x;
        final int screenHeight = screenSize.y;
        boolean onTop = (anchorRect.top > screenHeight / 2);
        int yPos;
        if (onTop) {
            yPos = anchorRect.top - rootHeight;
        } else {
            yPos = anchorRect.bottom;
        }
        int whichArrow, requestedX;
        whichArrow = ((onTop) ? R.id.arrow_down : R.id.arrow_up);
        requestedX = anchorRect.centerX();
        View arrow = whichArrow == R.id.arrow_up ? mUpImageView : mDownImageView;
        View hideArrow = whichArrow == R.id.arrow_up ? mDownImageView : mUpImageView;
        final int arrowWidth = arrow.getMeasuredWidth();
        arrow.setVisibility(View.VISIBLE);
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) arrow.getLayoutParams();
        hideArrow.setVisibility(View.INVISIBLE);
        int xPos = 0;
        // ETXTREME RIGHT CLIKED
        if (anchorRect.left + rootWidth > screenWidth) {
            xPos = (screenWidth - rootWidth);
        } // ETXTREME LEFT CLIKED
        else if (anchorRect.left - (rootWidth / 2) < 0) {
            xPos = anchorRect.left;
        } // INBETWEEN
        else {
            xPos = (anchorRect.centerX() - (rootWidth / 2));
        }
        param.leftMargin = (requestedX - xPos) - (arrowWidth / 2);
        if (onTop) {
            mHelpTextView.setMaxHeight(anchorRect.top - anchorRect.height());
        } else {
            mHelpTextView.setMaxHeight(screenHeight - yPos);
        }
        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);

        int animationId;
        if (onTop) {
            animationId = R.anim.tooltip_appear_up;
        } else  {
            animationId = R.anim.tooltip_appear_down;
        }
        mView.clearAnimation();
        mView.setAnimation(AnimationUtils.loadAnimation(mContext, animationId));
    }

    protected void preShow() {
        if (mView == null) throw new IllegalStateException("view undefined");
        if (showListener != null) {
            showListener.onPreShow();
            showListener.onShow();
        }
        if (mBackgroundDrawable == null) mWindow.setBackgroundDrawable(new BitmapDrawable());
        else mWindow.setBackgroundDrawable(mBackgroundDrawable);
        mWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mWindow.setTouchable(true);
        mWindow.setFocusable(false);
        mWindow.setOutsideTouchable(true);
        mWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return false; // Return false, pass on the event
            }
        });

        mWindow.setContentView(mView);
    }

    public void setBackgroundDrawable(Drawable background) {
        mBackgroundDrawable = background;
    }

    public void setContentView(View root) {
        mView = root;
        mWindow.setContentView(root);
    }

    public void setContentView(int layoutResID) {
        LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(inflator.inflate(layoutResID, null));
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        mWindow.setOnDismissListener(listener);
    }

    public void dismiss() {
        if (isDimissed) {
            return; // Return, has already been dismissed but is waiting for disappear animation to end
        }
        isDimissed = true;

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.tooltip_disappear);

        // Waiting for the animation to end must be done like this. Android's AnimationListener.OnAnimationEnded() is completely unreliable.
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mWindow.dismiss();
                if (showListener != null) {
                    showListener.onDismiss();
                }
            }
        }, animation.getDuration());

        mView.clearAnimation();
        mView.setAnimation(animation);
    }

    public void setText(String text) {
        mHelpTextView.setText(text);
    }

    public interface ShowListener {
        void onPreShow();

        void onDismiss();

        void onShow();
    }

    public void setShowListener(ShowListener showListener) {
        this.showListener = showListener;
    }
}