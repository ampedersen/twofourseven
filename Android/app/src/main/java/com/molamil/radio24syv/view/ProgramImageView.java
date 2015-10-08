package com.molamil.radio24syv.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.molamil.radio24syv.R;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.storage.ImageLibrary;
import com.molamil.radio24syv.storage.Storage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Shows a tinted image downloaded (or cached) from a URL.
 * Created by jens on 07/10/15.
 */
public class ProgramImageView extends ImageView {

    private String imageUrl;
    private int tintColor;

    public ProgramImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Apply attributes from XML
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ProgramImageView,
                0, 0);

        try {
            setImageUrl(a.getString(R.styleable.ProgramImageView_imageUrl));
            setTintColor(a.getInteger(R.styleable.ProgramImageView_tintColor, getResources().getColor(R.color.radio_gray)));
        } finally {
            a.recycle();
        }
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // View is now detached, and about to be destroyed
        setImageUrl(null);
    }

    public void setTintColor(int tintColor) {
        this.tintColor = tintColor;
        setColorFilter(tintColor, PorterDuff.Mode.OVERLAY);
    }

    // This does not work anyway...
//    @Override
//    protected void onDisplayHint(int hint) {
//        if (hint == View.VISIBLE) {
//            setImageUrl(imageUrl);
//        } else {
//            setImageUrl(null);
//        }
//    }


    public void setImageUrl(String imageUrl) {
        boolean isImageChanged = ((this.imageUrl == null) && (imageUrl != null)) || ((this.imageUrl != null) && (imageUrl == null)) || ((this.imageUrl != null) && !this.imageUrl.equals(imageUrl));
        this.imageUrl = imageUrl;

        if (isImageChanged) {
            if (imageUrl != null) {
                loadImage();
            } else {
                ImageLibrary.get().cancelDisplayTask(this);
                setImageDrawable(null);
                setImageBitmap(null);
            }
        }
    }

    private void loadImage() {
        // Load half resolution if low memory
        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        boolean isLowMemory = (maxMemory < 64*1024*1024); // 64 MB
        if (isLowMemory) {
            ProgramImageView.this.setScaleType(ScaleType.CENTER_INSIDE); // This will load images in half resolution. The image loader library determines the pixel size of the image to load by reading the layout size of the ImageView.
        }

        ImageLibrary.get().displayImage(imageUrl, this, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                ProgramImageView.this.setScaleType(ScaleType.CENTER_CROP); // Fill entire view once downloaded
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }
}
