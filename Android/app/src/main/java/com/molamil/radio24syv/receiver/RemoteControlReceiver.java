package com.molamil.radio24syv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.player.RadioPlayerService;

import java.util.logging.Logger;

public class RemoteControlReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        /*
        Log.d("PS", "RemoteControlReceiver.onReceive:");
        Log.d("PS", context.getPackageName());
        Log.d("PS", intent.getPackage());
        */
        if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {

            //Log.d("PS", "Headphones unplugged");

            Intent i = new Intent(context, RadioPlayerService.class);
            RadioPlayerService.RadioPlayerServiceBinder binder = (RadioPlayerService.RadioPlayerServiceBinder) peekService(context, i);
            if (binder != null) {
                //Log.d("PS", "Pausing playback");
                RadioPlayerService player = binder.getService();
                //TODO: Choose stop or pause...
                if(player.isLiveUrl(player.getUrl()))
                {
                    player.setAction(player.getUrl(), player.getTitle(), player.getDescription(), player.getProgramTitle(), player.getTopic(), player.getStartTime(), player.getEndTime(), player.getProgramId(), RadioPlayer.ACTION_STOP);
                }
                else
                {
                    player.setAction(player.getUrl(), player.getTitle(), player.getDescription(), player.getProgramTitle(), player.getTopic(), player.getStartTime(), player.getEndTime(), player.getProgramId(), RadioPlayer.ACTION_PAUSE);
                }

            } else {
                //Log.d("PS", "Unable to pause playback because the service is not started (null)");
            }
        }
        //Remote control
        else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    Log.d("PS", "RemoteControlReceiver.Play_Pause");
                    startService(context, RadioPlayerService.ACTION_TOGGLE_PLAYBACK);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    Log.d("PS", "RemoteControlReceiver.Play");
                    startService(context, RadioPlayerService.ACTION_PLAY);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    Log.d("PS", "RemoteControlReceiver.Pause");
                    startService(context, RadioPlayerService.ACTION_PAUSE);
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    Log.d("PS", "RemoteControlReceiver.Stop");
                    startService(context, RadioPlayerService.ACTION_STOP);
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    Log.d("PS", "RemoteControlReceiver.NExt");
                    startService(context, RadioPlayerService.ACTION_NEXT);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    Log.d("PS", "RemoteControlReceiver.Prev");
                    startService(context, RadioPlayerService.ACTION_PREVIOUS);
                    break;
            }
        }

        //Widget remote control
        else if(intent.getAction().equals(RadioPlayerService.ACTION_TOGGLE_PLAYBACK))
        {
            Log.i("PS", "Widget action: ACTION_TOGGLE_PLAYBACK");
            startService(context, RadioPlayerService.ACTION_TOGGLE_PLAYBACK);
        }
        else if(intent.getAction().equals(RadioPlayerService.ACTION_NEXT))
        {
            Log.i("PS", "Widget action: ACTION_NEXT");
            startService(context, RadioPlayerService.ACTION_NEXT);
        }
        else if(intent.getAction().equals(RadioPlayerService.ACTION_PREVIOUS))
        {
            Log.i("PS", "Widget action: ACTION_PREVIOUS");
            startService(context, RadioPlayerService.ACTION_PREVIOUS);
        }
        else if(intent.getAction().equals(RadioPlayerService.ACTION_PLAY))
        {
            Log.i("PS", "Widget action: ACTION_PLAY");
            startService(context, RadioPlayerService.ACTION_PLAY);
        }
        else if(intent.getAction().equals(RadioPlayerService.ACTION_STOP))
        {
            Log.i("PS", "Widget action: ACTION_STOP");
            startService(context, RadioPlayerService.ACTION_STOP);
        }
        else if(intent.getAction().equals(RadioPlayerService.ACTION_PAUSE))
        {
            Log.i("PS", "Widget action: ACTION_PAUSE");
            startService(context, RadioPlayerService.ACTION_PAUSE);
        }

    }

    // Explicit service intent (the class is set in the intent) is required for Android 5+ or else app crashes with error "IllegalArgumentException: Service Intent must be explicit"
    private static void startService(Context context, String action) {
        Intent serviceIntent = new Intent(context, RadioPlayerService.class);
        serviceIntent.setAction(action);
        context.startService(serviceIntent);
    }
}