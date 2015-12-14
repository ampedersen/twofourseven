package com.molamil.radio24syv;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.Broadcast;
import com.molamil.radio24syv.api.model.Program;
import com.molamil.radio24syv.api.model.RelatedProgram;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.receiver.AlarmNotificationReceiver;
import com.molamil.radio24syv.storage.ImageLibrary;
import com.molamil.radio24syv.storage.RadioLibrary;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.BroadcastInfo;
import com.molamil.radio24syv.storage.model.PodcastInfo;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.receiver.DownloadNotificationReceiver;
import com.molamil.radio24syv.storage.model.TopicInfo;
import com.molamil.radio24syv.view.ProgramScheduleButton;
import com.molamil.radio24syv.view.RadioViewPager;
import com.molamil.radio24syv.view.Tooltip;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;

public class MainActivity extends FragmentActivity implements
        RadioPlayer.RadioPlayerProvider,
        RadioPlayer.OnPlaybackListener,
        MainFragment.OnMainFragmentInteractionListener,
        PageFragment.OnFragmentInteractionListener,
        ScheduleFragment.OnFragmentInteractionListener,
        ProgramsFragment.OnFragmentInteractionListener,
        ProgramListFragment.OnFragmentInteractionListener,
        ProgramDetailsFragment.OnFragmentInteractionListener,
        ProgramCategoriesFragment.OnFragmentInteractionListener,
        NewsFragment.OnFragmentInteractionListener,
        OfflineFragment.OnFragmentInteractionListener,
        PlayerFragment.OnFragmentInteractionListener,
        ProgramSearchFragment.OnFragmentInteractionListener,
        ProgramScheduleButton.OnProgramScheduleButtonViewListener,
        ProgramDetailsFragment.OnProgramNotificationToggleListener {

    public static final int NOTIFICATION_ALARM_MINUTES = 5; // How many minutes before program start the notification should be shown
    private static final int NOTIFICATION_ALARM_MILLISECONDS = 1000 * 60 * NOTIFICATION_ALARM_MINUTES;
    private static final int NOTIFICATION_TOOLTIP_DURATION_MILLISECONDS = 4000; // How many milliseconds the tooltip should be visible when setting an alarm

    RadioViewPager pager; // The pager widget, which handles animation and allows swiping horizontally to access side screens
    SidePageTransformer pageTransformer;
    MainFragment mainFragment; // Keep the same main fragment across different page adapters
    String selectedTabTag;
    int mainPagePosition; // The position of the main page changes depending on the selected tab
    RadioPlayer radioPlayer;

    private int selectedProgramCategory;
    private TopicInfo selectedProgramTopic;

    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                if(radioPlayer != null)
                {
                    radioPlayer.stop();
                }
            } else if(state == TelephonyManager.CALL_STATE_IDLE) {

            } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {

            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFragment = new MainFragment();

        pageTransformer = new SidePageTransformer(SidePageTransformer.TransformType.SLIDE_OVER);
        pager = (RadioViewPager) findViewById(R.id.pager);
        pager.setAdapter(new LiveTabPagerAdapter(getSupportFragmentManager())); // The pager adapter, which provides the pages to the view pager widget
        pager.setPageTransformer(false, pageTransformer);
        pager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER); // No feedback when trying to scroll but there are no next page (Android 4 blue edge tint)
        //pager.setPageMargin(30);
        pager.setClipToPadding (false);

        radioPlayer = new RadioPlayer(this);
        radioPlayer.addListener(this);

        // Hockeyapp
        checkForUpdates();

        // Initialize singletons
        Storage.initialize(this);
        RestClient.initialize(getResources().getString(R.string.url_api));
        ImageLibrary.initialize(this);

        // Start on "Offline" tab if started by DownloadNotificationReceiver
        Intent callingIntent = getIntent();
        long[] downloadIds = callingIntent.getLongArrayExtra(DownloadNotificationReceiver.EXTRA_DOWNLOAD_IDS);
        if (downloadIds != null) {
            Log.d("JJJ", "MainActivity got started with " + downloadIds.length + " downloadIds as a parameter");
            mainFragment.setStartupTab(MainFragment.TAG_TAB_OFFLINE);
        }

        //addAlarmNotification(0, "PS TEST", "2015-11-04T20:35:00.000Z");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            if (radioPlayer != null) {
                radioPlayer.cleanup();
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        RadioLibrary.getInstance().disableDownloadReceiver();

        // Hockeyapp
        if (BuildConfig.HOCKEYAPP_UPDATES_ENABLED) {
            UpdateManager.unregister();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        RadioLibrary.getInstance().resumeDownloadReceiver();

        //Update Program notifications
        UpdateProgramNotifications();


        // Hockeyapp
        checkForCrashes();
    }

    @Override
    public void onBackPressed() {
        //close player if it is big
        if(mainFragment.getPlayerFragment() != null)
        {
            if(mainFragment.getPlayerFragment().getSize() == PlayerFragment.PlayerSize.BIG) {

                mainFragment.getPlayerFragment().setSize(PlayerFragment.PlayerSize.SMALL);
                return;
            }
        }

        boolean isSidePageInteractionEnabled = pager.isPagingEnabled();
        if (isSidePageInteractionEnabled) {
            boolean isViewingMainPage = (pager.getCurrentItem() == mainPagePosition);
            if (isViewingMainPage) {
                super.onBackPressed(); // Return to system
            } else {
                pager.setCurrentItem(mainPagePosition); // Back to main page
            }
        } else {
            ProgramsFragment f = (ProgramsFragment) mainFragment.getChildFragmentManager().findFragmentByTag(MainFragment.TAG_TAB_PROGRAMS);
            if (f == null) {
                Log.d("JJJ", "OMG no programs fragment");
                super.onBackPressed(); // Return to system
                return;
            }

            if (f.isShowingDetails()) {
                f.showList(); // Back to list page
            } else {
                super.onBackPressed(); // Return to system
            }
        }
    }

    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onBackButtonPressed() {
        onBackPressed(); // React as if the physical back button was pressed
    }

    public void setKeyboardVisible(boolean show) {
        /**
         * Hides the on-screen keyboard. NOTE: Only works when called from an Activity. Does not work when called from a Fragment (this won't work because you'll be passing a reference to the Fragment's host Activity, which will have no focused control while the Fragment is shown)
         * http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
         */
        View v = getCurrentFocus(); // Find the currently focused view, so we can grab the correct window token from it.
        if (v == null) {
            v = new View(MainActivity.this); // If no view currently has focus, create a new one, just so we can grab a window token from it
        }

        InputMethodManager manager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (show) {
            manager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT); // Show keyboard
        }
        else {
            manager.hideSoftInputFromWindow(v.getWindowToken(), 0); // Hide keyboard
        }
    }

    @Override
    public void onShowSidePage(Side side) {
        Log.d("JJJ", "Show side page " + side);
        switch (side) {
            case HIDE:
                pager.setCurrentItem(mainPagePosition);
                mainFragment.setTabSize(MainFragment.TabSize.NORMAL);
                break;
            case SHOW_LEFT:
                pager.setCurrentItem(mainPagePosition - 1);
                mainFragment.setTabSize(MainFragment.TabSize.NORMAL);
                break;
            case SHOW_RIGHT:
                pager.setCurrentItem(mainPagePosition + 1);
                mainFragment.setTabSize(MainFragment.TabSize.NORMAL);
                break;
            case SHOW_SUB_PAGE:
                mainFragment.setTabSize(MainFragment.TabSize.SMALL);
                break;
        }


    }

    @Override
    public void onError(String message) {
        mainFragment.setError(message);
    }

    @Override
    public void onEnableSidePageInteraction(boolean enable) {
        Log.d("JJJ", "Enable side page interaction " + enable);
        pager.setPagingEnabled(enable);
    }

    @Override
    public void onProgramSelected(ProgramInfo program) {
        showProgramDetails(program);
    }

    private void showProgramDetails(ProgramInfo program) {

        //Only needed if you need to go to program details form different tab than Podcasts
        //mainFragment.tabHost.setCurrentTabByTag(MainFragment.TAG_TAB_PROGRAMS);

        ProgramsFragment f = (ProgramsFragment) mainFragment.getChildFragmentManager().findFragmentByTag(MainFragment.TAG_TAB_PROGRAMS);
        if (f == null) {
            Log.d("JJJ", "OMG no programs fragment but a program was selected in its child ProgramListFragment.?!?!! " + program.getProgramId() + " " + program.getName());
            return;
        }
        f.showDetails(program);

    }

    @Override
    public void OnProgramCategorySelected(int categoryId, TopicInfo topic) {
        Log.d("JJJ", "Category " + categoryId + " topid " + topic);
        pager.setCurrentItem(mainPagePosition);
        setSelectedProgramCategory(categoryId, topic);
    }

    private void setSelectedProgramCategory(int category, TopicInfo topic) {
        selectedProgramCategory = category;
        selectedProgramTopic = topic;
        // Show category in program list (if visible)
        ProgramsFragment f = (ProgramsFragment) mainFragment.getChildFragmentManager().findFragmentByTag(MainFragment.TAG_TAB_PROGRAMS);
        if (f == null) {
            return;
        }
        f.showProgramCategory(selectedProgramCategory, selectedProgramTopic);
    }


    @Override
    public void OnProgramScheduleButtonClicked(ProgramScheduleButton view) {
        String programSlug = view.getBroadcast().getProgramSlug(); // ACHTUNG programId is sometimes PROGRAM_ID_UNKNOWN --> use programSlug to look up the program info !

        // Try to get program instantly from local storage
        ProgramInfo program = Storage.get().getProgram(programSlug);

        if (program == null) {
            // Get program using API
            RestClient.getApi().getProgram(programSlug).enqueue(new Callback<Program>() {
                @Override
                public void onResponse(Response<Program> response) {
                    onError(null);
                    if (response.body() == null) {
                        return;
                    }
                    ProgramInfo program = new ProgramInfo(response.body());
                    Storage.get().addProgram(program); // Add to database
                    showProgramDetails(program);
                }

                @Override
                public void onFailure(Throwable t) {
                    onError(t.getLocalizedMessage());
                    Log.d("JJJ", "fail " + t.getMessage());
                    t.printStackTrace();
                }
            });
        } else {
            showProgramDetails(program);
        }
    }


    //Program Notifications
    @Override
    public void OnProgramNotificationButtonClicked(CheckBox view, String slug)
    {
        boolean checked = view.isChecked();

        if (checked) {
            int alarmId = Storage.get().addProgramAlarm(slug); // Store alarm in database and get unique alarm ID that can be used to distinguish alarm notifications
            if(alarmId != Storage.ALARM_ID_UNKNOWN)
            {
                UpdateProgramNotificationsForProgram(slug);
            }
            else
            {
            }
        } else {
            int alarmId = Storage.get().getProgramAlarmId(slug);
            if ((alarmId != Storage.ALARM_ID_UNKNOWN) && removeAlarmNotification(alarmId)) {

                // Success
                Storage.get().removeProgramAlarm(alarmId);
                List<Integer> idsToRemove = Storage.get().getAlarmIdsForSlug(slug);
                for(int alarmIdToRemove : idsToRemove )
                {
                    Storage.get().removeAlarm(alarmIdToRemove);
                }
            } else {
            }
        }
    }

    private void UpdateProgramNotifications()
    {
        List<String> slugs = Storage.get().getAllProgramsWithAlarm();
        for (String slug : slugs) {
            UpdateProgramNotificationsForProgram(slug);
        }
    }

    private void UpdateProgramNotificationsForProgram(final String slug)
    {
        RestClient.getApi().getNextBroadcasts(slug).enqueue(new Callback<List<Broadcast>>() {
            @Override
            public void onResponse(Response<List<Broadcast>> response) {
                if (response.body() == null) {
                    return;
                }
                List<Broadcast> broadcasts = response.body();
                for (int i = 0; i < broadcasts.size(); i++) {
                    Broadcast broadcast = broadcasts.get(i);
                    String programName = broadcast.getProgramName();
                    String broadcastTime = broadcast.getBroadcastTime().getStart();
                    int alarmId = Storage.get().getAlarmId(slug, broadcastTime); // Check if there is an alarm for this program and time
                    if(alarmId == Storage.ALARM_ID_UNKNOWN)
                    {
                        String message;
                        alarmId = Storage.get().addAlarm(slug, broadcastTime); // Store alarm in database and get unique alarm ID that can be used to distinguish alarm notifications
                        if (addAlarmNotification(alarmId, programName, broadcastTime)) {


                        } else {
                            Storage.get().removeAlarm(alarmId);
                        }
                    }
                    else
                    {
                    }
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("PS", "failed to get broadcasts for program to create notifications " + t.getMessage());
                //t.printStackTrace();
            }
        });
    }

    //Schedule Notifications
    @Override
    public void OnProgramScheduleNotificationButtonClicked(ProgramScheduleButton view, CheckBox clickedView) {
        BroadcastInfo broadcast = view.getBroadcast();
        String programSlug = broadcast.getProgramSlug(); // ACHTUNG programId is sometimes PROGRAM_ID_UNKNOWN --> use programSlug to look up the program info !
        String programTime = broadcast.getTimeBegin();
        String programName = broadcast.getName();

        boolean isEnabled = view.getNotificationEnabled();
        if (isEnabled) {
            String message;
            long programTimeMilliseconds = DateTime.parse(programTime).getMillis();
            long systemTime = SystemClock.elapsedRealtime();
            long waitingTime = programTimeMilliseconds - systemTime;
            if (waitingTime < 0) {
                // Already started
                message = getResources().getString(R.string.program_already_started);
            } else if (waitingTime < NOTIFICATION_ALARM_MILLISECONDS) {
                // Less than 5 minutes to start
                message = getResources().getString(R.string.program_starts_too_soon);
            } else {
                // More than 5 minutes to start
                int alarmId = Storage.get().addAlarm(programSlug, programTime); // Store alarm in database and get unique alarm ID that can be used to distinguish alarm notifications
                if (addAlarmNotification(alarmId, programName, programTime)) {
                    // Alarm added
                    message = getResources().getString(R.string.program_alarm_added);
                    try {
                        message = String.format(message, NOTIFICATION_ALARM_MINUTES);
                    } catch (IllegalFormatException e) {
                        Log.d("JJJ", "Unable to show number of minutes in alarm notification tooltip, make sure the string has the formatting needed: " + message);
                    }

                } else {
                    // Alarm could not be added
                    Storage.get().removeAlarm(alarmId);
                    view.setNotificationEnabled(false);
                    message = getResources().getString(R.string.program_alarm_failed);
                }
            }

            // Show message in tooltip
            final Tooltip tooltip = new Tooltip(MainActivity.this, message);
            tooltip.show(clickedView);
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tooltip.dismiss(); // Hide delayed
                }
            }, NOTIFICATION_TOOLTIP_DURATION_MILLISECONDS);
        } else {
            int alarmId = Storage.get().getAlarmId(programSlug, programTime);
            if ((alarmId != Storage.ALARM_ID_UNKNOWN) && removeAlarmNotification(alarmId)) {
                // Success
                Storage.get().removeAlarm(alarmId);
            }
        }
    }

    //Schedule notifications
    private boolean addAlarmNotification(int alarmId, String programName, String programTime) {
        PendingIntent alarmIntent = getAlarmNotificationIntent(alarmId, programName);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        DateTime alarmTime = DateTime.parse(programTime);
        long fireTimeInMillis = alarmTime.getMillis() - 1000 * 60 * NOTIFICATION_ALARM_MINUTES;

        long millisLeft = fireTimeInMillis - System.currentTimeMillis();

        boolean isAlarmInThePast = (millisLeft < 0);
        if (isAlarmInThePast) {
            Log.d("PS_ALARM", "Unable to set alarm because it is in the past");
            return false;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + millisLeft, alarmIntent);
        } else {
            manager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + millisLeft, alarmIntent);
        }
        return true;
    }

    private boolean removeAlarmNotification(int alarmId) {
        PendingIntent alarmIntent = getAlarmNotificationIntent(alarmId, ""); // Program name does not matter
        Log.d("JJJ", "Removing alarm notification for alarmId " + alarmId);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(alarmIntent);
        return true;
    }

    private PendingIntent getAlarmNotificationIntent(int alarmId, String programName) {
        Intent intent = new Intent("PROGRAM_ALARM");
        intent.putExtra(AlarmNotificationReceiver.EXTRA_PROGRAM_NAME, programName);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return alarmIntent;
    }

    @Override
    public void onPlayerSizeChanged(PlayerFragment.PlayerSize newSize, PlayerFragment.PlayerSize oldSize) {
        if (newSize == PlayerFragment.PlayerSize.BIG) {
            pager.setPagingEnabled(false);
            mainFragment.setDimming(MainFragment.Dimming.DIM);
        } else {
            pager.setPagingEnabled(true);
            mainFragment.setDimming(MainFragment.Dimming.NONE);
        }
    }

    @Override
    public void onDimmingChanged(MainFragment.Dimming newDimming, MainFragment.Dimming oldDimming) {
        boolean isRemoved = (oldDimming == MainFragment.Dimming.DIM) && (newDimming == MainFragment.Dimming.NONE);
        if (isRemoved) {
            PlayerFragment playerFragment = (PlayerFragment)mainFragment.getChildFragmentManager().findFragmentByTag(PlayerFragment.class.getName());
            if (playerFragment.getSize() == PlayerFragment.PlayerSize.BIG) {
                playerFragment.setSize(PlayerFragment.PlayerSize.SMALL);
            }
        }
    }

    @Override
    public void onMainTabChanged(String tabTag) {
        //Log.d("JJJ", "onMainTabChanged "+ tabTag);
        if (tabTag.equals(selectedTabTag)) {
            return; // Return, nothing changed
        }

        selectedTabTag = tabTag;

        // Change page contents depending on the selected tab
        switch (selectedTabTag) {
            case MainFragment.TAG_TAB_LIVE:
                pager.setAdapter(new LiveTabPagerAdapter(getSupportFragmentManager()));
                mainPagePosition = 0;
                break;
            case MainFragment.TAG_TAB_PROGRAMS:
                pager.setAdapter(new ProgramsTabPagerAdapter(getSupportFragmentManager()));
                mainPagePosition = 1;
                break;
            case MainFragment.TAG_TAB_NEWS:
                pager.setAdapter(new NewsTabPagerAdapter(getSupportFragmentManager()));
                mainPagePosition = 0;
                break;
            case MainFragment.TAG_TAB_OFFLINE:
                pager.setAdapter(new OfflineTabPagerAdapter(getSupportFragmentManager()));
                mainPagePosition = 0;
                break;
        }

        pager.setCurrentItem(mainPagePosition, false);


        PlayerFragment playerFragment = (PlayerFragment)mainFragment.getChildFragmentManager().findFragmentByTag(PlayerFragment.class.getName());
        if(playerFragment != null)
        {
            if (playerFragment.getSize() == PlayerFragment.PlayerSize.BIG) {
                playerFragment.setSize(PlayerFragment.PlayerSize.SMALL); // Small player
            }
            playerFragment.updatePlayer();
        }

        mainFragment.setTabSize(MainFragment.TabSize.NORMAL); // Normal tab size
        mainFragment.setError(null); // Clear error message
    }

    // Hockeyapp
    private void checkForCrashes() {
        CrashManager.register(this, BuildConfig.HOCKEYAPP_APP_ID);
    }

    // Hockeyapp
    private void checkForUpdates() {
        // Remove this for store / production builds!
        if (BuildConfig.HOCKEYAPP_UPDATES_ENABLED) {
            UpdateManager.register(this, BuildConfig.HOCKEYAPP_APP_ID);
        }
    }

    @Override
    public RadioPlayer getRadioPlayer() {
        return radioPlayer;
    }

    @Override
    public void OnBusy(RadioPlayer player) {

    }

    @Override
    public void OnStarted(final RadioPlayer player) {
        AddPhoneStateListener();
        // This determines the programId by looking at the player's URL. Maybe this should be used other places (and put in the RadioPlayer class) to figure out what is playing. Or maybe something else. This is very WIP.
        String url = player.getUrl();
        String liveUrlStartsWith = getString(R.string.url_live_radio);
        String streamingUrlStartsWith = getString(R.string.url_offline_radio);
        String localUrlStartsWith = "file";

        if (url != RadioPlayer.URL_UNASSIGNED) {
            if (url.startsWith(liveUrlStartsWith)) {
                // Live stream
                RestClient.getApi().getCurrentBroadcast().enqueue(new Callback<List<Broadcast>>() {
                    @Override
                    public void onResponse(Response<List<Broadcast>> response) {
                        onError(null);
                        if (response.body() == null) {
                            return;
                        }
                        List<Broadcast> body = response.body();
                        if (body != null) {
                            Broadcast broadcast = body.get(0);
                            //TODO: videoProgramId is sometimes null. Should use slug here instead?
                            addToPlayerHistory(broadcast.getVideoProgramId()); // Add program ID to player history

                            PlayerFragment playerFragment = (PlayerFragment)mainFragment.getChildFragmentManager().findFragmentByTag(PlayerFragment.class.getName());
                            if (playerFragment != null) {
                                /*
                                String url = broadcast.getImageUrl();
                                if(broadcast.getAppImages() != null)
                                {
                                    if(broadcast.getAppImages().getPlayer() != null)
                                    {
                                        url = broadcast.getAppImages().getPlayer();
                                    }
                                    else if(broadcast.getAppImages().getOverview() != null)
                                    {
                                        url = broadcast.getAppImages().getOverview();
                                    }
                                    else if(broadcast.getAppImages().getLive() != null)
                                    {
                                        url = broadcast.getAppImages().getLive();
                                    }

                                }

                                playerFragment.setImageUrl(url);
                                */
                                playerFragment.setImageUrl(broadcast.getAppImages().getPlayer());
                            }
                        } else {
                            onError(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("JJJ", "fail " + t.getMessage());
                        t.printStackTrace();
                        onError(t.getLocalizedMessage());
                    }
                });
            } else {
                int programId = Storage.PROGRAM_ID_UNKNOWN;
                if (url.startsWith(streamingUrlStartsWith)) {
                    // Online stream
                    url = url.substring(getString(R.string.url_offline_radio).length()); // Podcast URL = cut off server part of the path
                    PodcastInfo podcast = Storage.get().getPodcast(url);
                    if (podcast != null) {
                        programId = podcast.getProgramId(); // Program ID from database
                    } else {
                        //TODO: FIX THIS CASE. LOAD DATA FROM SERVER
                        Log.w("JJJ", "Unable to get podcast's programId because we are playing an online podcast that is not in the database - this should be impossible?!");
                    }
                } else if (url.startsWith(localUrlStartsWith)) {
                    // Local stream
                    int podcastId;
                    try {
                        podcastId = Integer.parseInt(url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."))); // Podcast ID = filename without extension. This is a bit dangerous but we are in charge of naming our downloaded files.
                    } catch (NumberFormatException e) {
                        podcastId = Storage.PODCAST_ID_UNKNOWN;
                    }
                    if (podcastId != Storage.PODCAST_ID_UNKNOWN) {
                        PodcastInfo podcast = Storage.get().getPodcast(podcastId);
                        if (podcast != null) {
                            programId = podcast.getProgramId(); // Program ID from database
                        }
                    } else {
                        Log.w("JJJ", "Unable to get podcast's programId because we are playing a podcast from a file without podcastId in the filename - this should be impossible?!");
                    }
                }

                Log.d("JJJ", "programId" + programId);
                if (programId != Storage.PROGRAM_ID_UNKNOWN) {
                    addToPlayerHistory(programId);

                    ProgramInfo program = Storage.get().getProgram(programId); // TODO download program if not in database
                    if (program != null) {
                        PlayerFragment playerFragment = (PlayerFragment) mainFragment.getChildFragmentManager().findFragmentByTag(PlayerFragment.class.getName());
                        if (playerFragment != null) {
                            playerFragment.setImageUrl(program.getAppImagePlayerUrl());
                        }
                    }
                }
            }
        }



    }

    private void addToPlayerHistory(final int programId) {
        // Store player history
        String date = DateTime.now().toString(RestClient.getDateFormat());
        Log.d("JJJ", "addPlayerHistory programId " + programId + " date " + date);
        Storage.get().addPlayerHistory(programId, date);

        // Store related programs
        RestClient.getApi().getProgram(programId).enqueue(new Callback<Program>() {
            @Override
            public void onResponse(Response<Program> response) {
                MainActivity.this.onError(null);
                if (response.body() == null) {
                    return;
                }
                Program program = response.body();
                if ((program != null) && (program.getRelatedPrograms() != null)) {
                    ArrayList<Integer> relatedProgramIds = new ArrayList<>(program.getRelatedPrograms().size());
                    for (RelatedProgram p : program.getRelatedPrograms()) {
                        Object id = p.getVideoProgramId();
                        if (id instanceof Double) {
                            int programId = ((Double) id).intValue(); // Read program ID directly
                            relatedProgramIds.add(programId);
                        } else {
                            ProgramInfo relatedProgram = Storage.get().getProgram(p.getSlug()); // Look up program ID using slug
                            if (relatedProgram != null) {
                                relatedProgramIds.add(relatedProgram.getProgramId());
                            }
                        }
                    }
                    //Log.d("JJJ", "relatedPrograms " + relatedProgramIds.size() + " for programId " + programId + " " + program.getName());
                    if (relatedProgramIds.size() > 0) {
                        Storage.get().addRelatedPrograms(programId, relatedProgramIds);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                MainActivity.this.onError(t.getLocalizedMessage());
                Log.d("JJJ", "fail " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    public void OnStopped(RadioPlayer player) {

        RemovePhoneStateListener();
    }

    @Override
    public void OnPaused(RadioPlayer player) {

    }

    /*
    PhoneStateListener stuff
     */
    private void AddPhoneStateListener()
    {
        RemovePhoneStateListener();
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
    private void RemovePhoneStateListener()
    {
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    /*
    Custom page adaptors - one for each tab on the main screen.
    This is the best way to change contents of ViewPager on the fly.
     */
    private class LiveTabPagerAdapter extends FragmentStatePagerAdapter {
        public LiveTabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mainFragment;
                case 1:
                    return new ScheduleFragment();
            }
            Log.e("JJJ", "Unable to determine a fragment for " + selectedTabTag + " page " + position + " - returning null");
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private class ProgramsTabPagerAdapter extends FragmentStatePagerAdapter {
        public ProgramsTabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ProgramCategoriesFragment.newInstance(selectedProgramCategory, selectedProgramTopic);
                case 1:
                    return mainFragment;
                case 2:
                    return new ProgramSearchFragment();
            }
            Log.e("JJJ", "Unable to determine a fragment for " + selectedTabTag + " page " + position + " - returning null");
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private class NewsTabPagerAdapter extends FragmentStatePagerAdapter {
        public NewsTabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mainFragment;
            }
            Log.e("JJJ", "Unable to determine a fragment for " + selectedTabTag + " page " + position + " - returning null");
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

    private class OfflineTabPagerAdapter extends FragmentStatePagerAdapter {
        public OfflineTabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mainFragment;
            }
            Log.e("JJJ", "Unable to determine a fragment for " + selectedTabTag + " page " + position + " - returning null");
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

}

