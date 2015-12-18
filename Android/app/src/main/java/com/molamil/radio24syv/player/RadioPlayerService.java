package com.molamil.radio24syv.player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.molamil.radio24syv.MainActivity;
import com.molamil.radio24syv.R;
import com.molamil.radio24syv.receiver.RemoteControlReceiver;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.PodcastInfo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

//import android.support.v4.app.NotificationCompat;

/**
 * Encapsulates MediaPlayer and makes it possible to keep playing audio even when app is sent to background.
 * Created by jens on 15/09/15.
 */
public class RadioPlayerService extends Service implements
//        MediaPlayer.OnBufferingUpdateListener,
//        MediaPlayer.OnCompletionListener,
//        MediaPlayer.OnInfoListener,
//        MediaPlayer.OnPreparedListener,
//        MediaPlayer.OnErrorListener,
//        MediaPlayer.OnSeekCompleteListener,
        AudioManager.OnAudioFocusChangeListener {

    // These are the Intent actions that we are prepared to handle. Notice that the fact these
    // constants exist in our class is a mere convenience: what really defines the actions our
    // service can handle are the <action> tags in the <intent-filters> tag for our service in
    // AndroidManifest.xml.
    public static final String ACTION_TOGGLE_PLAYBACK = "dk.radio24syv.android.action.TOGGLE_PLAYBACK";
    public static final String ACTION_PLAY = "dk.radio24syv.android.action.PLAY";
    public static final String ACTION_PAUSE = "dk.radio24syv.android.action.PAUSE";
    public static final String ACTION_STOP = "dk.radio24syv.android.action.STOP";
    public static final String ACTION_PREVIOUS = "dk.radio24syv.android.action.PREVIOUS";
    public static final String ACTION_NEXT = "dk.radio24syv.android.action.NEXT";

    /*
    public static final String ACTION_SKIP = "dk.radio24syv.android.action.SKIP";
    public static final String ACTION_REWIND = "dk.radio24syv.android.action.REWIND";
    public static final String ACTION_URL = "dk.radio24syv.android.action.URL";
    */

    // Broadcast IDs used when sending messages to connected clients
    public final static String BROADCAST_ID = "RadioPlayerServiceEvent";
    public final static String BROADCAST_STATE = "State";
    public final static String BROADCAST_URL = "Url";

    private final static int NOTIFICATION_ID = 1; // Lock screen notification ID. Must not be 0.

    private final IBinder binder = new RadioPlayerServiceBinder(); // Binder given to clients

    private int state = RadioPlayer.STATE_STOPPED;
    private String url = RadioPlayer.URL_UNASSIGNED;
    private String title;
    private String description;
    private String programTitle;
    private String topic;
    private String startTime;
    private String endTime;
    private int programId;

    private MediaPlayer player;
    private int action = RadioPlayer.ACTION_STOP;
    private PlayUrlTask task = null;
    private WifiManager.WifiLock wifiLock; // Used to keep wifi running while streaming

    /*
    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;
    */

    /*
    // The component name of MusicIntentReceiver, for use with media button and remote control
    // APIs
    ComponentName mMediaButtonReceiverComponent;

    // our RemoteControlClient object, which will use remote control APIs available in
    // SDK level >= 14, if they're available.
    RemoteControlClientCompat mRemoteControlClientCompat;
    */

    @Override
    public void onCreate() {
        // The service is being created
        // TODO start as foreground + notifications on lock screen
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "playerLock");
        wifiLock.setReferenceCounted(false); // For convenience, do not keep track of how many times the lock has been required. Release it when release() is called no matter what.


        //mMediaButtonReceiverComponent = new ComponentName(this, AudioNoisyReceiver.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if(action != null)
        {
            if (action.equals(ACTION_TOGGLE_PLAYBACK))
            {
                if(getAction() == RadioPlayer.ACTION_PLAY)
                {
                    if(isLiveUrl(url))
                    {
                        setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_STOP);
                    }
                    else
                    {
                        setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_PAUSE);
                    }

                 }
                else
                {
                    setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_PLAY);
                }
            }
            else if (action.equals(ACTION_PLAY))
            {
                setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_PLAY);
            }
            else if (action.equals(ACTION_PAUSE))
            {
                setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_PAUSE);
            }
            else if (action.equals(ACTION_STOP))
            {
                setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_STOP);
            }
            else if (action.equals(ACTION_PREVIOUS))
            {
                //Currently this is handled by RadioPlayer, not service...
                PodcastInfo info = getPrevious();
                if(info != null)
                {
                    //TODO: Correct start and endtimes
                    setAction(info.getAudioUrl(), info.getTitle(), info.getDescription(), programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_PLAY);
                }
            }
            else if (action.equals(ACTION_NEXT))
            {
                //Currently this is handled by RadioPlayer, not service...
                PodcastInfo info = getNext();
                if(info != null)
                {
                    //TODO: Correct start and endtimes
                    setAction(info.getAudioUrl(), info.getTitle(), info.getDescription(), programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_PLAY);
                }
            }
        }

        // The service is starting, due to a call to startService()
        return Service.START_REDELIVER_INTENT; // If the system kills the service after onStartCommand() returns, recreate the service and call onStartCommand() with the last intent that was delivered to the service
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return binder;
    }

    @Override
    public boolean onUnbind (Intent intent) {
        Log.d("JJJ", "service onUnbind");
//        setAction(url, RadioPlayer.ACTION_STOP);
//        cleanup();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        cleanup();
    }

    private void cleanup() {
        if (task != null) {
            task.cancel(true);
        }
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private boolean isConnectedToInternetIfNeeded(final String url, int action) {
        if (action == RadioPlayer.ACTION_STOP) {
            return true; // Does not need internet
        }

        if (RadioPlayer.isLocalUrl(url)) {
            return true; // Does not need internet
        } else {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true; // Return true, internet is happy
            } else {
                Log.d("JJJ", "Unable to play " + url + " because internet connection is down");
                setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_STOP);
                return false; // Return false, internet is not happy
            }
        }
    }

    public int getAction()
    {
        return action;
    }
    public void setAction(final String url, String title, String description, String programTitle, String topic, String startTime, String endTime, int programId, int newAction) {
        Log.d("JJJ", "service setAction " + RadioPlayer.getActionName(newAction) + " (was " + RadioPlayer.getActionName(action) + ") + audioId " + (player == null ? "NULL" : player.getAudioSessionId()));

        if (!isActionAllowed(newAction)) {
            Log.d("JJJ", "Unable to perform action " + newAction + " because it is not allowed while doing action " + action);
            return; // Return, action is not allowed
        }

        if (!isConnectedToInternetIfNeeded(url, newAction)) {
            Log.d("JJJ", "Unable to perform action " + newAction + " because it requires internet connection");
            return; // Return, action needs internet connection
        }

        this.url = url;
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.programTitle = programTitle;
        this.startTime = startTime;
        this.endTime = endTime;

        switch (newAction) {

            case RadioPlayer.ACTION_PLAY:
                if (action == RadioPlayer.ACTION_PAUSE) {
                    if (player != null) {
                        player.start();
                        setState(RadioPlayer.STATE_STARTED);
                    }
                }
                else {
                    if (task != null) {
                        task.cancel(true);
                    }
                    task = new PlayUrlTask();
                    String[] arguments = new String[3];
                    arguments[PlayUrlTask.ARGUMENT_URL] = url;
                    arguments[PlayUrlTask.ARGUMENT_TITLE] = title;
                    arguments[PlayUrlTask.ARGUMENT_DESCRIPTION] = description;
                    task.execute(arguments);
                }
                break;

            case RadioPlayer.ACTION_STOP:
                if (player != null) {
                    if (player.isPlaying()) {
                        player.stop();
                    }
                    player.release();
                    player = null;
                    setState(RadioPlayer.STATE_STOPPED);
                }
                break;

            case RadioPlayer.ACTION_PAUSE:
                if (player != null) {
                    player.pause();
                }
                setState(RadioPlayer.STATE_PAUSED);
                break;

            case RadioPlayer.ACTION_NEXT:
                Log.d("JJJ", "TODO implement ACTION_NEXT - ignoring");
                newAction = action; // Ignore new action
                break;

            case RadioPlayer.ACTION_PREVIOUS:
                Log.d("JJJ", "TODO implement ACTION_PREVIOUS - ignoring");
                newAction = action; // Ignore new action
                break;
        }

        action = newAction;

        updateWifiLock();

        //Different check. Media session?
        if(url != null) {
            if(updateLockScreenControls())
            {
                updateRunInForeground();
            }
        }
    }

    private boolean isActionAllowed(int newAction) {
        switch (newAction) {
            case RadioPlayer.ACTION_PLAY:
                return true;
            case RadioPlayer.ACTION_STOP:
                return (action == RadioPlayer.ACTION_PLAY) || (action == RadioPlayer.ACTION_PAUSE);
            case RadioPlayer.ACTION_PAUSE:
                return (action == RadioPlayer.ACTION_PLAY);
            case RadioPlayer.ACTION_NEXT:
                return (action == RadioPlayer.ACTION_PLAY) || (action == RadioPlayer.ACTION_STOP) || (action == RadioPlayer.ACTION_PAUSE);
            case RadioPlayer.ACTION_PREVIOUS:
                return (action == RadioPlayer.ACTION_PLAY) || (action == RadioPlayer.ACTION_STOP) || (action == RadioPlayer.ACTION_PAUSE);
            default:
                return true;
        }
    }

    private void updateWifiLock() {
        boolean isPlayingOnlineStream = (action == RadioPlayer.ACTION_PLAY) && (!RadioPlayer.isLocalUrl(url));
        if (isPlayingOnlineStream) {
            Log.d("PS", "Wifi lock on");
            wifiLock.acquire();
        } else {
            Log.d("PS", "Wifi lock off");
            wifiLock.release();
        }
    }

    private void updateRunInForeground() {
        //if (action == RadioPlayer.ACTION_PLAY) {
            Log.d("PS", "Run in foreground " + RadioPlayer.getActionName(action));
            // When running in the foreground, the service also must provide a status bar notification
            // to ensure that users are aware of the running service and allow them to open an activity that can interact with the service.

//            String audioInfo = null; // TODO audio info (not needed, remove it if unwanted)
            int smallIconId = R.drawable.status_bar_play_logo_white; // TODO icon for player state

            // Start MainActivity when notification is touched
            PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), MainActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            // Get app icon
            Bitmap largeIcon;
            try {
                largeIcon = ((BitmapDrawable) getPackageManager().getApplicationIcon(getApplicationContext().getPackageName())).getBitmap(); // Get app icon
            } catch (PackageManager.NameNotFoundException e) {
                largeIcon = null; // Null means only the action icon will be used (e.g. play symbol). This will not happen anyway, our app always exists.
                Log.d("PS", "Unable to getInstance app icon because of " + e.getMessage());
                e.printStackTrace();
            }

            NotificationCompat.Builder builder =  new NotificationCompat.Builder(getApplicationContext());

            builder.setOngoing(true)
                    .setShowWhen(false) // No timestamp (Android 5)
                    .setWhen(0) // No timestamp (Android 4)
                    .setVisibility(Notification.VISIBILITY_PUBLIC) // Show everywhere
                    .setColor(0x000000)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(smallIconId)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setContentIntent(intent);

            //Style. Depends on state of player
            //For now always show three controls. Disable/enable next / prev in actions
            builder.setStyle(new NotificationCompat.MediaStyle()
                    .setMediaSession(mSession.getSessionToken())// Attach our MediaSession token
                    .setShowActionsInCompactView(0, 1, 2));// Show our playback controls in the compat view

            //Prev action
            builder.addAction(getPrevious() == null ? R.drawable.prev_button_disabled : R.drawable.prev_button, "prev", getPlaybackAction(ACTION_PREVIOUS));

            //Play/pause/stop action.TODO: Stop button whith live content
            int playBtnDrawable = action == RadioPlayer.ACTION_PLAY ? R.drawable.button_pause_program : R.drawable.button_play_program;
            builder.addAction(playBtnDrawable, "pause", getPlaybackAction(ACTION_TOGGLE_PLAYBACK));

            //next action
            builder.addAction(getPrevious() == null ? R.drawable.next_button_disabled : R.drawable.next_button, "prev", getPlaybackAction(ACTION_PREVIOUS));

            Notification notification = builder.build();
            startForeground(NOTIFICATION_ID, notification);

        /*
        } else {
            Log.d("PS", "Run in background " + RadioPlayer.getActionName(action));
            stopForeground(true); // TODO keep notification if paused?
        }
        */
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getProgramTitle() {
        return programTitle;
    }

    public String getTopic() {
        return topic;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() { return endTime; }

    public int getProgramId() { return programId; }

    public float getProgress()
    {
        if(player == null)
        {
            return 0;
        }

        if (state == RadioPlayer.STATE_STARTED)
        {
            return (float)player.getCurrentPosition() / player.getDuration();
        }

        return 0;
        /*

        return (float)player.getCurrentPosition() / player.getDuration();
        */
    }

    public int getDuration()
    {
        if(player == null)
        {
            return 0;
        }

        if (state == RadioPlayer.STATE_STARTED)
        {
            return player.getDuration();
        }

        return 0;
    }

    public void seekTo(float pct)
    {
        if(player == null)
        {
            return;
        }

        if (state != RadioPlayer.STATE_UNASSIGNED && state != RadioPlayer.STATE_BUSY)
        {
            int millis = (int)(pct*player.getDuration());
            player.seekTo(millis);
        }
    }

    public int getState() {
        return state;
    }

    private void setState(int newState) {
        if (newState == state) {
            return; // Return, no change in state
        }

        state = newState;
        sendMessage();

        updateAudioFocus();
    }

    private void updateAudioFocus() {
        if (state == RadioPlayer.STATE_STARTED) {
            AudioManager a = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
            int result = a.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

            /*
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                a.registerMediaButtonEventReceiver(new ComponentName(getPackageName(), RemoteControlReceiver.class.getName()));
                // Start playback.
            }
            */
        }
    }

    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.d("PS", "Audio focus gained");
                // Resume playback
                if (action == RadioPlayer.ACTION_PAUSE) {
                    setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_PLAY);
                }
                if (player != null) {
                    player.setVolume(1, 1);
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                Log.d("PS", "Audio focus lost");
                AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                //mAudioManager.unregisterMediaButtonEventReceiver(new ComponentName(getPackageName(), RemoteControlReceiver.class.getName()));

                // Lost focus for an unbounded amount of time
                if (action == RadioPlayer.ACTION_PLAY) {
                    setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_PAUSE);
                } else {
                    setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_STOP);
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.d("PS", "Audio focus lost temporarily");
                // Lost focus for a short time, but we have to stop playback. Playback is likely to resume.
                setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_PAUSE);
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.d("PS", "Audio focus duck");
                // Lost focus for a short time, but it's ok to keep playing at an attenuated level
                if (player != null) {
                    player.setVolume(0.2f, 0.2f);
                }
                break;
        }
    }

    // Send an Intent with an action named BROADCAST_ID. The Intent sent can be received by connected clients (which is just the RadioPlayer for now).
    private void sendMessage() {
        Log.d("JJJ", "Sending message: " + RadioPlayerService.BROADCAST_STATE + " " + RadioPlayer.getStateName(state) + " " + RadioPlayerService.BROADCAST_URL + " " + url);
        Intent intent = new Intent(BROADCAST_ID);
        intent.putExtra(BROADCAST_STATE, state);
        intent.putExtra(BROADCAST_URL, url);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent); // Use LocalBroadcastManager to send messages to listeners within our own process. It is fast and we do not need to specify all sorts of intents & actions in the manifest when using this.
    }

    private class PlayUrlTask extends AsyncTask<String, Void, Void> {
        public final static int ARGUMENT_URL = 0;
        public final static int ARGUMENT_TITLE = 1;
        public final static int ARGUMENT_DESCRIPTION = 2;

        @Override
        protected Void doInBackground(String... arguments) {
            if ((arguments == null) || (arguments.length != 3) || (arguments[ARGUMENT_URL] == null)) {
                Log.e("JJJ", "Unable to play URL because it is empty " + url);
                setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_STOP);
                return null; // Return, no url to play
            }
            //Log.d("JJJ", "url " + arguments[ARGUMENT_URL]);
            //Log.d("JJJ", "title " + arguments[ARGUMENT_TITLE]);
            //Log.d("JJJ", "description " + arguments[ARGUMENT_DESCRIPTION]);

            setState(RadioPlayer.STATE_BUSY);
            url = arguments[ARGUMENT_URL];
            title = arguments[ARGUMENT_TITLE];
            description = arguments[ARGUMENT_DESCRIPTION];

            // Validate URL
            URL web;
            try {
                web = new URL(url);
            } catch (MalformedURLException e) {
                Log.d("JJJ", "Unable play URL because it is not valid " + url);
                e.printStackTrace();
                setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_STOP);
                return null; // Return, bad URL
            }

            // Prepare player
            if (player == null) {
                player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK); // Keep CPU awake while playback is started and release the CPU when stopped/paused
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.d("JJJ", "Playback completed " + url);
                        setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_STOP);
                    }
                });

            } else {
                if (player.isPlaying()) {
                    player.stop();
                }
                player.reset(); // Reset before changing data source
            }
            player.setVolume(1, 1); // Always play at full volume when pressing play
            try {
                player.setDataSource(web.toString());
            } catch (IOException e) {
                Log.e("JJJ", "Unable to play URL because of data source error " + url);
                e.printStackTrace();
                setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_STOP);
                return null; // Return, data source error
            }

            // Play URL
            if (action == RadioPlayer.ACTION_PLAY) {
                try {
                    player.prepare();
                    player.start();
                    setState(RadioPlayer.STATE_STARTED);
                } catch (IOException e) {
                    Log.e("JJJ", "Unable to play URL because of some playback error " + url);
                    e.printStackTrace();
                    setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_STOP);
                    return null; // Return, playback error
                } catch (IllegalStateException e) {
                    Log.e("JJJ", "Unable to play URL because player is in an unexpected state (maybe headphones got unplugged while buffering?) " + url);
                    e.printStackTrace();
                    setAction(url, title, description, programTitle, topic, startTime, endTime, programId, RadioPlayer.ACTION_STOP);
                    return null; // Return, playback error
                }
            }

            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Void nothing) {
            task = null;
        }
    }

    public interface RadioPlayerServiceListener {
        public void onStateChanged();
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class RadioPlayerServiceBinder extends Binder {
        public RadioPlayerService getService() {
            // Return this instance of RadioPlayerService so clients can call public methods
            return RadioPlayerService.this;
        }
    }

    public  boolean isLiveUrl(final String url) {
        return (url.equals(getString(R.string.url_live_radio)));
    }

    /**
     * Duplicate implementation. Also exists in RadioPlayer. TODO: Cleanup
     */

    //Not working from lock screen,
    public List<PodcastInfo> getPlayList()
    {
        return Storage.get().getPodcasts(programId);
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

    /**
     * REMOTE CONTROL FROM LOCK SCREEN, API <21.
     * API 21+ uses lock screen notifications with media styles & actions (see updateRunInForeground())
     */

    MediaSessionCompat mSession;

    private boolean updateLockScreenControls()
    {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(this,AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_GAIN) {
            return false; //Failed to gain audio focus
        }

        ComponentName mRemoteControlResponder = new ComponentName(getPackageName(), RemoteControlReceiver.class.getName());
        final Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(mRemoteControlResponder);

        //Log.i("PS", "getApplication(): " + getApplication());
        //Log.i("PS", "mRemoteControlResponder: "+mRemoteControlResponder);

        mSession = new MediaSessionCompat(getApplication(), "Radio24syv", mRemoteControlResponder, null);
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        PlaybackStateCompat playbackStateCompat = new PlaybackStateCompat.Builder()
                .setActions(
                                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_STOP
                )
                .setState(action == RadioPlayer.ACTION_PLAY ? PlaybackStateCompat.STATE_PLAYING : action == RadioPlayer.ACTION_PAUSE ? PlaybackStateCompat.STATE_PAUSED : PlaybackStateCompat.STATE_STOPPED, 0, 1.0f)
                .build();
        mSession.setPlaybackState(playbackStateCompat);
        //mSession.setCallback(mMediaSessionCallback);//For lollipop?

        mSession.setSessionActivity(null);

        mSession.setActive(true);
        updateMediaSessionMetaData();

        //TODO: Purpose?
       // mTransportController = mSession.getController().getTransportControls();

        return true;

    }

    private void updateMediaSessionMetaData()
    {
        String title = this.title;
        String album = this.description;//this.programTitle; //Live/podcast/offline
        //String artist = getResources().getString(R.string.app_name);
        Bitmap artwork = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        //builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist);
        builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album);
        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, title);
        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 5000); //DEV
        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, artwork);

        mSession.setMetadata(builder.build());

    }

    private PendingIntent getPlaybackAction(String action)
    {
        Intent intent = new Intent(this, RemoteControlReceiver.class);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
        return pendingIntent;
    }
}
