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
            Log.d("JJJ", "Headphones unplugged");
            Intent i = new Intent(context, RadioPlayerService.class);
            RadioPlayerService.RadioPlayerServiceBinder binder = (RadioPlayerService.RadioPlayerServiceBinder) peekService(context, i);
            if (binder != null) {
                Log.d("JJJ", "Pausing playback");
                RadioPlayerService player = binder.getService();
                player.setAction(player.getUrl(), player.getTitle(), player.getDescription(), player.getProgramTitle(), player.getTopic(), player.getStartTime(), player.getEndTime(), player.getProgramId(), RadioPlayer.ACTION_PAUSE);
            } else {
                Log.d("JJJ", "Unable to pause playback because the service is not started (null)");
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
                    context.startService(new Intent(RadioPlayerService.ACTION_TOGGLE_PLAYBACK));
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    context.startService(new Intent(RadioPlayerService.ACTION_PLAY));
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    context.startService(new Intent(RadioPlayerService.ACTION_PAUSE));
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    context.startService(new Intent(RadioPlayerService.ACTION_STOP));
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    context.startService(new Intent(RadioPlayerService.ACTION_NEXT));
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    context.startService(new Intent(RadioPlayerService.ACTION_PREVIOUS));
                    break;
            }
        }
    }
}