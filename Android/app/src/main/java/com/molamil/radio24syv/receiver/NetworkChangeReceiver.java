package com.molamil.radio24syv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.molamil.radio24syv.player.RadioPlayerService;

/**
 * Created by patriksvensson on 14/12/15.
 */
public class NetworkChangeReceiver extends BroadcastReceiver
{
    /*
    public static final String WIFI_AVAILABLE_AND_CONNECTED = "wifi_state_available_and_connected";
    public static final String MOBILE_AVAILABLE_AND_CONNECTED = "mobile_state_available_and_connected";
    public static final String UNKNOWN = "availability_and_connection_unknown";
    */

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile =  connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        /*
        //Get radio player service to check if we need to take action
        Intent i = new Intent(context, RadioPlayerService.class);
        RadioPlayerService.RadioPlayerServiceBinder binder = (RadioPlayerService.RadioPlayerServiceBinder) peekService(context, i);
        if (binder == null) {
            return;
        }

        RadioPlayerService player = binder.getService();
        */

        //Log.i("PS", "NetworkChangeReceiver...");
        //Log.i("PS", "NetworkChangeReceiver, wifi: "+wifi.isAvailable()+", "+wifi.getState());
        //Log.i("PS", "NetworkChangeReceiver, mobile: "+mobile.isAvailable()+", "+mobile.getState());

        /**
         * CASES TO HANDLE:
         *
         * No connection -> wifi
         * No connection -> mobile
         * mobile -> wifi
         * wifi -> mobile
         *
         * when currently no connection we just wait...
         *
         */
    }

    private boolean needsRestart()
    {
        //return true if player is supposed to be playing but is not
        return false;
    }
}
