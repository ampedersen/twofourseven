package com.molamil.radio24syv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.player.RadioPlayerService;

public class RemoteControlReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {

            Log.d("PS", "Headphones unplugged");

            Intent i = new Intent(context, RadioPlayerService.class);
            RadioPlayerService.RadioPlayerServiceBinder binder = (RadioPlayerService.RadioPlayerServiceBinder) peekService(context, i);
            if (binder != null) {
                Log.d("PS", "Pausing playback");
                RadioPlayerService player = binder.getService();
                //TODO: Choose stop or pause...
                player.setAction(player.getUrl(), player.getTitle(), player.getDescription(), player.getProgramTitle(), player.getTopic(), player.getStartTime(), player.getEndTime(), player.getProgramId(), RadioPlayer.ACTION_STOP);
                //player.setAction(player.getUrl(), player.getTitle(), player.getDescription(), player.getProgramTitle(), player.getTopic(), player.getStartTime(), player.getEndTime(), player.getProgramId(), RadioPlayer.ACTION_PAUSE);
            } else {
                Log.d("PS", "Unable to pause playback because the service is not started (null)");
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
                    context.startService(new Intent(RadioPlayerService.ACTION_TOGGLE_PLAYBACK));
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    Log.d("PS", "RemoteControlReceiver.Play");
                    context.startService(new Intent(RadioPlayerService.ACTION_PLAY));
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    Log.d("PS", "RemoteControlReceiver.Pause");
                    context.startService(new Intent(RadioPlayerService.ACTION_PAUSE));
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    Log.d("PS", "RemoteControlReceiver.Stop");
                    context.startService(new Intent(RadioPlayerService.ACTION_STOP));
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    Log.d("PS", "RemoteControlReceiver.NExt");
                    context.startService(new Intent(RadioPlayerService.ACTION_NEXT));
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    Log.d("PS", "RemoteControlReceiver.Prev");
                    context.startService(new Intent(RadioPlayerService.ACTION_PREVIOUS));
                    break;
            }
        }

        //Widget remote control
        else if(intent.getAction().equals(RadioPlayerService.ACTION_TOGGLE_PLAYBACK))
        {
            Log.i("PS", "Widget action: ACTION_TOGGLE_PLAYBACK");
            Intent serviceIntent = new Intent(context,RadioPlayerService.class);
            serviceIntent.setAction(RadioPlayerService.ACTION_TOGGLE_PLAYBACK);
            context.startService(serviceIntent);
        }
        else if(intent.getAction().equals(RadioPlayerService.ACTION_NEXT))
        {
            Log.i("PS", "Widget action: ACTION_NEXT");
            Intent serviceIntent = new Intent(context,RadioPlayerService.class);
            serviceIntent.setAction(RadioPlayerService.ACTION_NEXT);
            context.startService(serviceIntent);
        } else if(intent.getAction().equals(RadioPlayerService.ACTION_PREVIOUS))
        {
            Log.i("PS", "Widget action: ACTION_PREVIOUS");
            Intent serviceIntent = new Intent(context,RadioPlayerService.class);
            serviceIntent.setAction(RadioPlayerService.ACTION_PREVIOUS);
            context.startService(serviceIntent);
        }
        else if(intent.getAction().equals(RadioPlayerService.ACTION_PLAY))
        {
            Log.i("PS", "Widget action: ACTION_PLAY");
            Intent serviceIntent = new Intent(context,RadioPlayerService.class);
            serviceIntent.setAction(RadioPlayerService.ACTION_PLAY);
            context.startService(serviceIntent);
        }
        else if(intent.getAction().equals(RadioPlayerService.ACTION_STOP))
        {
            Log.i("PS", "Widget action: ACTION_STOP");
            Intent serviceIntent = new Intent(context,RadioPlayerService.class);
            serviceIntent.setAction(RadioPlayerService.ACTION_STOP);
            context.startService(serviceIntent);
        }
        else if(intent.getAction().equals(RadioPlayerService.ACTION_PAUSE))
        {
            Log.i("PS", "Widget action: ACTION_PAUSE");
            Intent serviceIntent = new Intent(context,RadioPlayerService.class);
            serviceIntent.setAction(RadioPlayerService.ACTION_PAUSE);
            context.startService(serviceIntent);
        }

    }
}