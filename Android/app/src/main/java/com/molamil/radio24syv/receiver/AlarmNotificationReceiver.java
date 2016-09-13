package com.molamil.radio24syv.receiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.molamil.radio24syv.MainActivity;
import com.molamil.radio24syv.R;

import java.util.IllegalFormatException;

/**
 * Created by jens on 06/10/15.
 */
public class AlarmNotificationReceiver extends BroadcastReceiver {
    public static final String EXTRA_PROGRAM_NAME = "program_name";

    @Override
    public void onReceive(Context context, Intent i) {
        String programName = i.getStringExtra(EXTRA_PROGRAM_NAME);
        int alarmMinutes = MainActivity.NOTIFICATION_ALARM_MINUTES;
        Log.d("JJJ", "Alarm notification received for " + programName);

        // Start MainActivity when notification is touched
        PendingIntent intent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Get app icon as resource ID

        /*

        int smallIconId;
        String packageName = context.getApplicationContext().getPackageName();
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0);
            smallIconId = info.icon;
        } catch (PackageManager.NameNotFoundException e) {
            smallIconId = 0;
        }

        */

        int smallIconId = R.drawable.icon_bell_white;

        // Get app icon as bitmap
        Bitmap largeIcon;
        try {
            largeIcon = ((BitmapDrawable) context.getPackageManager().getApplicationIcon(context.getPackageName())).getBitmap(); // Get app icon
        } catch (PackageManager.NameNotFoundException e) {
            largeIcon = null; // Null means only the action icon will be used (e.g. play symbol). This will not happen anyway, our app always exists.
            Log.d("JJJ", "Unable to getInstance app icon because of " + e.getMessage());
            e.printStackTrace();
        }

        Resources r = context.getResources();
        String title = r.getString(R.string.app_name);
        String message = r.getString(R.string.program_alarm_notification);
        try {
            message = String.format(message, programName, alarmMinutes);
        } catch (IllegalFormatException e) {
            Log.d("JJJ", "Unable to show number of minutes in alarm notification tooltip, probably because the resource string is missing formatting: " + message);
        }

        // Create lock screen widget thingy
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(smallIconId)
                .setLargeIcon(largeIcon)
                .setColor(ContextCompat.getColor(context, R.color.radio_gray_dark))
                .setVisibility(Notification.VISIBILITY_PUBLIC) // Show everywhere
                .setPriority(Notification.PRIORITY_MAX) // Show in top of list
                .setContentIntent(intent)
                .setTicker(message)
                .setAutoCancel(true)
                .setCategory(Notification.CATEGORY_ALARM)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .build();

        NotificationManagerCompat.from(context).notify(0, notification);
    }
}
