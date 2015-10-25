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

import com.molamil.radio24syv.api.model.Podcast;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.PodcastInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public final static int PLAYLIST_NONE = -1;
    public final static int PLAYLIST_PODCAST = 0;
    public final static int PLAYLIST_OFFLINE = 1;

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

    public final static String URL_UNASSIGNED = null; // URL value when unassigned
    public final static String TITLE_UNASSIGNED = null;
    public final static String DESCRIPTION_UNASSIGNED = null;

    private ArrayList<OnPlaybackListener> listenerList = new ArrayList<>();
    private String url = URL_UNASSIGNED; // Keeps track of the url that was most recently set an action with
    private String title = TITLE_UNASSIGNED;
    private String description = DESCRIPTION_UNASSIGNED;
    private String topic = null;
    private String programTitle = null;
    private String startTime = null;
    private String endTime = null;
    private int playlistType = PLAYLIST_NONE;
    private int programId = -1;
    private Context context;
    private RadioPlayerService service = null;
    private boolean isBoundToService;

    private int pendingAction = ACTION_UNASSIGNED; // If the service is not yet bound this action will getInstance performed once bound
    private String pendingUrl = URL_UNASSIGNED;
    private String pendingTitle = TITLE_UNASSIGNED;
    private String pendingDescription = DESCRIPTION_UNASSIGNED;
    private String pendingTopic = null;
    private String pendingProgramTitle = null;
    private String pendingStartTime = null;
    private String pendingEndTime = null;
    private int pendingPlaylistType = PLAYLIST_NONE;
    private int pendingProgramId = -1;

    //PS. Quick and dirty playlist for next / prev buttons.
    //private List<PodcastInfo> playList = null;

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

//    public int getProgramId() {
//        if (isBoundToService) {
//            return service.getProgramId(); // The actual playing url. Might be different from this.url if the call has not gone through yet.
//        } else {
//            Log.d("JJJ", "Unable to getInstance program ID from service because it is not connected");
//            return Storage.PROGRAM_ID_UNKNOWN;
//        }
//    }

    public String getUrl() {
        if (isBoundToService) {
            return service.getUrl(); // The actual playing url. Might be different from this.url if the call has not gone through yet.
        } else {
            Log.d("JJJ", "Unable to get URL from service because it is not connected");
            return URL_UNASSIGNED;
        }
    }

    public String getTitle() {
        if (isBoundToService) {
            return service.getTitle(); // The actual playing url. Might be different from this.url if the call has not gone through yet.
        } else {
            Log.d("JJJ", "Unable to get title from service because it is not connected");
            return TITLE_UNASSIGNED;
        }
    }

    public String getDescription() {
        if (isBoundToService) {
            return service.getDescription(); // The actual playing url. Might be different from this.url if the call has not gone through yet.
        } else {
            Log.d("JJJ", "Unable to get description from service because it is not connected");
            return DESCRIPTION_UNASSIGNED;
        }
    }

    public String getTopic() {
        if (isBoundToService) {
            return service.getTopic();
        } else {
            return null;
        }
    }

    public String getProgramTitle() {
        if (isBoundToService) {
            return service.getProgramTitle();
        } else {
            return null;
        }
    }

    public String getStartTime() {
        if (isBoundToService) {
            return service.getStartTime();
        } else {
            return null;
        }
        //return startTime;
    }

    public String getEndTime() {
        if (isBoundToService) {
            return service.getEndTime();
        } else {
            return null;
        }
        //return endTime;
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

    public static boolean isLocalUrl(final String url) {
        return (url == RadioPlayer.URL_UNASSIGNED) || (!url.startsWith("http://"));
    }

    //TODO: Remove
    /*
    public void play(String url, String title, String description) {
        this.play(url, title, description, null, null);
    }
    */

    public void play(String url, String title, String description, String programTitle, String topic, String startTime, String endTime, int playlistType, int programId) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.programTitle = programTitle;
        this.topic = topic;
        this.startTime = startTime;
        this.endTime = endTime;
        this.playlistType = playlistType;
        this.programId = programId;

        setAction(url, title, description, programTitle, topic, startTime, endTime, playlistType, programId, ACTION_PLAY);
    }

    public void stop() {
        setAction(url, title, description, programTitle, topic, startTime, endTime, playlistType, programId, ACTION_STOP);
    }

    public void pause() {
        setAction(url, title, description, programTitle, topic, startTime, endTime, playlistType, programId, ACTION_PAUSE);
    }

    public void next() {
        //Hacked. Implement in service?
        PodcastInfo next = getNext();
        if(next == null)
        {
            return;
        }

        //TODO: Update times
        play(next.getAudioUrl(), next.getTitle(), next.getDescription(), programTitle, topic, startTime, endTime, playlistType, programId);

        //OBSOLETE
        //setAction(url, title, description, programTitle, topic, startTime, endTime, playlistType, programId, ACTION_NEXT);
    }

    public void previous() {
        //Hacked. Implement in service?
        PodcastInfo previous = getPrevious();
        if(previous == null)
        {
            return;
        }

        //TODO: Update times
        play(previous.getAudioUrl(), previous.getTitle(), previous.getDescription(), programTitle, topic, startTime, endTime, playlistType, programId);

        //OBSOLETE
        //setAction(url, title, description, programTitle, topic, startTime, endTime, playlistType, programId, ACTION_PREVIOUS);
    }

    private void setAction(final String url, String title, String description, String programTitle, String topic, String startTime, String endTime, int playlistType, int programId, int action) {
        //Log.d("JJJ", "setAction " + action + " isBound " + isBoundToService + " " + service + " url " + url + " title " + title + " description " + description);
        if (isBoundToService) {

            /*
            // Get audio description
            String audioDescription;
            if (url != null) {
                if (url.equals(getString(R.string.url_live_radio))) {
                    audioDescription = getString(R.string.audio_description_live);
                } else {
                    audioDescription = getString(R.string.app_name); // TODO get podcast date
                }
            } else {
                audioDescription = getString(R.string.app_name);
            }

            // Get audio title
            String audioTitle;
            ProgramInfo program = Storage.get().getProgram(programId);
            if (program != null) {
                audioTitle = program.getName();
            } else {
                audioTitle = audioDescription; // TODO download program info
            }
            */
            service.setAction(url, title, description, programTitle, topic, startTime, endTime, action);
            clearPendingAction(); // We got hole through to the service, clear pending action
        } else {
            Log.d("JJJ", "Unable to set action for service because service is not bound - will set the action when it getInstance bound");
            setPendingAction(url, title, description, programTitle, topic, startTime, endTime, playlistType, programId, action);
        }
    }

    private void clearPendingAction() {
        setPendingAction(URL_UNASSIGNED, TITLE_UNASSIGNED, DESCRIPTION_UNASSIGNED, null, null, null, null, PLAYLIST_NONE, -1, ACTION_UNASSIGNED);
    }

    private boolean isPendingAction() {
        return (pendingUrl != null) && (!pendingUrl.equals(URL_UNASSIGNED)) && (pendingAction != ACTION_UNASSIGNED);
    }

    private void setPendingAction(String url, String title, String description, String programTitle, String topic, String startTime, String endTime, int playlistType, int programId, int action) {
        //Log.d("JJJ", "set pending action " + getActionName(action) + " url " + url + " title " + title);
        pendingUrl = url;
        pendingAction = action;
        pendingTitle = title;
        pendingDescription = description;
        pendingProgramTitle = programTitle;
        pendingTopic = topic;
        pendingStartTime = startTime;
        pendingEndTime = endTime;
        pendingPlaylistType = playlistType;
        pendingProgramId = programId;
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
                setAction(pendingUrl, pendingTitle, pendingDescription, pendingProgramTitle, pendingTopic, pendingStartTime, pendingEndTime, playlistType, pendingProgramId, pendingAction);
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
        void OnBusy(RadioPlayer player);
        void OnStarted(RadioPlayer player);
        void OnStopped(RadioPlayer player);
        void OnPaused(RadioPlayer player);
    }

    public interface RadioPlayerProvider {
        RadioPlayer getRadioPlayer();
    }

    //playlist stuff
    //TODO: Sorted? Cached?
    public List<PodcastInfo> getPlayList() {
        if(playlistType == PLAYLIST_PODCAST)
        {
            return Storage.get().getPodcasts(programId);
        }
        else if(playlistType == PLAYLIST_OFFLINE)
        {
            //Never used. Offline items are also set to PLAYLIST_PODCAST for now.
            return Storage.get().getPodcastsInLibrary(programId);
        }

        return null;
    }

    /*
    public void setPlayList(List<PodcastInfo> playlist) {
        //Cached playlist
    }
    */

    public boolean hasPrevious()
    {
        //Log.i("PS", "hasPrevious: program id: "+programId);
        switch (playlistType)
        {
            case PLAYLIST_NONE:
                return false;
            case PLAYLIST_OFFLINE:
                return false;//TODO: Activate
            case PLAYLIST_PODCAST:

                //TODO: check if there is a next one or if this is the last in the list.
                return true;
        }

        return false;
    }

    public boolean hasNext()
    {
        //Log.i("PS", "hasNext: program id: "+programId);
        switch (playlistType)
        {
            case PLAYLIST_NONE:
                return false;
            case PLAYLIST_OFFLINE:
                return false;//TODO: Activate
            case PLAYLIST_PODCAST:
                //TODO: check if there is a next one or if this is the last in the list.
                return true;
        }

        return false;
    }

    private PodcastInfo getNext()
    {
        List<PodcastInfo> playlist = getPlayList();
        if(playlist == null)
        {
            return null;
        }

        int i=0;
        for(PodcastInfo p: playlist)
        {
            if(i<playlist.size()-2 && getUrl().toLowerCase().contains(p.getAudioUrl().toLowerCase()))
            {
                return playlist.get(i+1);
            }
            i++;
        }
        return null;
    }

    private PodcastInfo getPrevious()
    {
        List<PodcastInfo> playlist = getPlayList();
        if(playlist == null)
        {
            return null;
        }

        int i=0;
        for(PodcastInfo p: playlist)
        {
            //if(p.getAudioUrl() == getUrl() && i>0)
            if(i>0 && getUrl().toLowerCase().contains(p.getAudioUrl().toLowerCase()))
            {
                return playlist.get(i-1);
            }
            i++;
        }
        return null;
    }
}
