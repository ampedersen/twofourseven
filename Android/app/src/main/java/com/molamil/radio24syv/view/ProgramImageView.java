package com.molamil.radio24syv.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.molamil.radio24syv.R;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.storage.ImageLibrary;
import com.molamil.radio24syv.storage.Storage;

/**
 * Shows a tinted image downloaded (or cached) from a URL.
 * Created by jens on 07/10/15.
 */
public class ProgramImageView extends ImageView {

    private String imageUrl;
    private int color;

    public ProgramImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Apply attributes from XML
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ProgramImageView,
                0, 0);

        try {
            setImageUrl(a.getString(R.styleable.ProgramImageView_imageUrl));
            setColor(a.getInteger(R.styleable.ProgramImageView_tintColor, Color.WHITE));
        } finally {
            a.recycle();
        }
    }

    public void setColor(int color) {
        this.color = color;
        setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    public void setImageUrl(String imageUrl) {
        boolean isImageChanged = (imageUrl != null) && !imageUrl.equals(this.imageUrl);
        this.imageUrl = imageUrl;
        if (isImageChanged) {
            ImageLibrary.get().displayImage(imageUrl, this);
        }
    }
}
