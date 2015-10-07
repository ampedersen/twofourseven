package com.molamil.radio24syv.storage;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * This is just a wrapper to make sure things are initialized.
 * Uses Universal Image Loader library for download and caching: https://github.com/nostra13/Android-Universal-Image-Loader
 * Created by jens on 07/10/15.
 */
public class ImageLibrary {

    private static ImageLibrary instance = null;

    public static void initialize(Context context) {
        instance = new ImageLibrary();

        // Create default options which will be used for every
        // displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(1000))
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
            .defaultDisplayImageOptions(defaultOptions)
            .build();

        ImageLoader.getInstance().init(config);
    }

    public static ImageLoader get() {
        return ImageLoader.getInstance(); // Return the ImageLoader singleton directly
    }

}
