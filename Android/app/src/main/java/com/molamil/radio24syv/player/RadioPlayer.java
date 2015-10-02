package com.molamil.radio24syv.player;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.molamil.radio24syv.storage.Storage;

import java.util.ArrayList;

/**
 * Plays audio and manages the RadioPlayerService. Supports playback of URL, standard play/pause/etc actions, multiple listener callbacks for player states.
 * Created by jens on 14/09/15.
 */
public class RadioPlayer {

    // Actions that can be performed by the player
    public final static int ACTION_UNASSIGNED = -1;
    public final static int ACTION_PLAY = 0;
    public final static int ACTION_STOP = 1;
    public final static int ACTION_PAUSE = 2;
    public final static int ACTION_NEXT = 3;
    public final static int ACTION_PREVIOUS = 4;

    public static String getActionName(int action) {
        final String[] names = new String[] { "play", "stop", "pause", "next", "previous" };
        try {
            return names[action];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "";
        }
    }

    // States the player can be in
    public final static int STATE_UNASSIGNED = -1;
    public final static int STATE_STOPPED = 0;
    public final static int STATE_STARTED = 1;
    public final static int STATE_PAUSED = 2;
    public final static int STATE_BUSY = 3;

    public static String getStateName(int state) {
        final String[] names = new String[] { "stopped", "started", "paused", "busy" };
        try {
            return names[state];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "";
        }
    }

    // URL value when unassigned
    public final static String URL_UNASSIGNED = null;

    ArrayList<OnPlaybackListener> listenerList = new ArrayList<>();
    private String url = URL_UNASSIGNED; // Keeps track of the url that was most recently set an action with
    private int programId = Storage.PROGRAM_ID_UNKNOWN;
    Context context;
    RadioPlayerService service = null;
    boolean isBoundToService;

    int pendingAction = ACTION_UNASSIGNED; // If the service is not yet bound this action will getInstance performed once bound
    String pendingUrl = URL_UNASSIGNED;
    int pendingProgramId = Storage.PROGRAM_ID_UNKNOWN;

    public RadioPlayer(Context context) {
        Log.d("JJJ", "Create RadioPlayer - starting radio service");
        this.context = context;

        Intent i = new Intent(context, RadioPlayerService.class);
        context.startService(i); // Because the service is started using startService() it will keep running even when the host activity is destroyed. It would usually auto-cleanup because we are using bindService().
        context.bindService(i, serviceConnection, Context.BIND_AUTO_CREATE); // ServiceConnection will assign service reference to our "service" variable

        // Register to receive messages.
        // We are registering an observer (messageReceiver) to receive Intents
        // with actions named BROADCAST_ID.
        LocalBroadcastManager.getInstance(context).registerReceiver(messageReceiver, new IntentFilter(RadioPlayerService.BROADCAST_ID));
    }

    public void cleanup() {
        Log.d("JJJ", "Cleanup RadioPlayer - stopping radio service");
        if (isBoundToService) {
            context.unbindService(serviceConnection);
            if (getState() != STATE_STARTED) {
                Log.d("JJJ", "calling stopService() because nothing is playing");
                Intent i = new Intent(context, RadioPlayerService.class);
                context.stopService(i);
            }
        }

        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(context).unregisterReceiver(messageReceiver);

        listenerList.clear();
    }

    public int getProgramId() {
        if (isBoundToService) {
            return service.getProgramId(); // The actual playing url. Might be different from this.url if the call has not gone through yet.
        } else {
            Log.d("JJJ", "Unable to getInstance program ID from service because it is not connected");
            return Storage.PROGRAM_ID_UNKNOWN;
        }
    }

    public String getUrl() {
        if (isBoundToService) {
            return service.getUrl(); // The actual playing url. Might be different from this.url if the call has not gone through yet.
        } else {
            Log.d("JJJ", "Unable to getInstance URL from service because it is not connected");
            return URL_UNASSIGNED;
        }
    }

    public int getState() {
        if (isBoundToService) {
            return service.getState();
        } else {
            Log.d("JJJ", "Unable to getInstance state from service because it is not connected");
            return STATE_UNASSIGNED;
        }
    }

    public void addListener(OnPlaybackListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
            callback(listener); // Fire events to reflect current state
        }
    }

    public void removeListener(OnPlaybackListener listener) {
        if (listenerList.contains(listener)) {
            listenerList.remove(listener);
        }
    }

    private void callback() {
        for (OnPlaybackListener l : listenerList) {
            callback(l);
        }
    }

    private void callback(OnPlaybackListener listener) {
        if (isBoundToService) {
            switch (service.getState()) {
                case STATE_STOPPED:
                    listener.OnStopped(this);
                    break;
                case STATE_STARTED:
                    listener.OnStarted(this);
                    break;
                case STATE_PAUSED:
                    listener.OnPaused(this);
                    break;
                case STATE_BUSY:
                    listener.OnBusy(this);
                    break;
                default:
                    Log.d("JJJ", "Unable to fire callback event because none is defined for state:" + getStateName(service.getState()));
                    break;
            }
        } else {
            Log.d("JJJ", "Unable to fire callback event in because service is not bound");
        }
    }

    public void play(int programId, String url) {
        this.programId = programId;
        this.url = url;
        setAction(programId, url, ACTION_PLAY);
    }

    public void stop() {
        setAction(programId, url, ACTION_STOP);
    }

    public void pause() {
        setAction(programId, url, ACTION_PAUSE);
    }

    public void next() { setAction(programId, url, ACTION_NEXT); }

    public void previous() { setAction(programId, url, ACTION_PREVIOUS); }

    private void setAction(int programId, final String url, int action) {
        Log.d("JJJ", "setAction " + action + " isBound " + isBoundToService + " " + service + " programId " + programId + " url " + url);
        if (isBoundToService) {
            service.setAction(programId, url, action);
            clearPendingAction(); // We got hole through to the service, clear pending action
        } else {
            Log.d("JJJ", "Unable to set action for service because service is not bound - will set the action when it getInstance bound");
            setPendingAction(programId, url, action);
        }
    }

    private void clearPendingAction() {
        setPendingAction(Storage.PROGRAM_ID_UNKNOWN, URL_UNASSIGNED, ACTION_UNASSIGNED);
    }

    private boolean isPendingAction() {
        return (pendingUrl != null) && (!pendingUrl.equals(URL_UNASSIGNED)) && (pendingAction != ACTION_UNASSIGNED);
    }

    private void setPendingAction(int programId, String url, int action) {
        Log.d("JJJ", "set pending action " + getActionName(action) + " url " + url + " programId " + programId);
        pendingProgramId = programId;
        pendingUrl = url;
        pendingAction = action;
    }

    // Defines callbacks for service binding, passed to bindService()
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("JJJ", "Service bound");
            // We've bound to RadioPlayerService, cast the IBinder and getInstance RadioPlayerService instance
            RadioPlayerService.RadioPlayerServiceBinder binder = (RadioPlayerService.RadioPlayerServiceBinder) service;
            RadioPlayer.this.service = binder.getService();
            isBoundToService = true;

            if (isPendingAction()) {
                Log.d("JJJ", "Executing pending action " + getActionName(pendingAction) + " url " + pendingUrl);
                setAction(pendingProgramId, pendingUrl, pendingAction);
            } else {
                callback(); // Make a callback to all listeners about the state of the player service
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("JJJ", "Service unbound");
            isBoundToService = false;
        }
    };

    // Our handler for received Intents. This will be called whenever an Intent with an action named BROADCAST_ID is broadcasted.
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Get extra data included in the Intent
            int state = intent.getIntExtra(RadioPlayerService.BROADCAST_STATE, STATE_UNASSIGNED);
            String url = intent.getStringExtra(RadioPlayerService.BROADCAST_URL);
            if (url == null) {
                url = URL_UNASSIGNED;
            }
            Log.d("JJJ", "Got message: " + RadioPlayerService.BROADCAST_STATE + " " + getStateName(state) + " " + RadioPlayerService.BROADCAST_URL + " " + url);

            callback(); // Callback to listeners that something happened
        }
    };

    public interface OnPlaybackListener {
        public void OnBusy(RadioPlayer player);
        public void OnStarted(RadioPlayer player);
        public void OnStopped(RadioPlayer player);
        public void OnPaused(RadioPlayer player);
    }

    public interface RadioPlayerProvider {
        public RadioPlayer getRadioPlayer();
    }
}