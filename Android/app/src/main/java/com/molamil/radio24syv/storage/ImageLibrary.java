package com.molamil.radio24syv.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * This is just a wrapper to make sure things are initialized.
 * Uses Universal Image Loader library for download and caching: https://github.com/nostra13/Android-Universal-Image-Loader
 * Created by jens on 07/10/15.
 */
public class ImageLibrary {

    private static ImageLibrary instance = null;

    // Set up image loader. More details here: https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Useful-Info
    public static void initialize(Context context) {
        instance = new ImageLibrary();

        // Create default options which will be used for every displayImage() call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false) // Do not cache in memory, it eats up everything
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565) // Uses half memory of 888 and difference is not noticeable in our case
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .displayer(new FadeInBitmapDisplayer(1000)) // Fade in when image is loaded, delay in milliseconds
                .resetViewBeforeLoading(true)

                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .denyCacheImageMultipleSizesInMemory()
                .threadPriority(Thread.MIN_PRIORITY)
                .threadPoolSize(2)
                .build();

        ImageLoader.getInstance().init(config);
    }

    public static ImageLoader get() {
        return ImageLoader.getInstance(); // Return the ImageLoader singleton directly
    }

}
