package com.molamil.radio24syv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by patriksvensson on 14/12/15.
 */
public class NetworkChangeReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile =  connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        //Context.getSystemService(Context.CONNECTIVITY_SERVICE).isActiveNetworkMetered()
        Log.i("PS", "NetworkChangeReceiver...");
        Log.i("PS", "NetworkChangeReceiver, wifi: "+wifi.isAvailable()+", "+wifi.getState());
        Log.i("PS", "NetworkChangeReceiver, mobile: "+mobile.isAvailable()+", "+mobile.getState());

        /*
        if (wifi.isAvailable()) {
            //Do something
            if (mobile.isAvailable()) {
                //Do something else
            }
            */
    }
}
