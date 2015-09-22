package com.molamil.radio24syv.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.molamil.radio24syv.MainActivity;

/**
 * Created by jens on 22/09/15.
 */
public class DownloadReceiver extends BroadcastReceiver {
    public static final String EXTRA_DOWNLOAD_IDS = DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
            long[] downloadIds = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
            Log.d("JJJ", "Download notification touched downloadId " + (downloadIds == null ? "null" : downloadIds[0]));
            Intent i = new Intent(context, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(EXTRA_DOWNLOAD_IDS, downloadIds);
            context.startActivity(i);
        }
    }
}
