package com.molamil.radio24syv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.Program;
import com.molamil.radio24syv.api.model.RelatedProgram;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.receiver.DownloadNotificationReceiver;
import com.molamil.radio24syv.storage.model.TopicInfo;
import com.molamil.radio24syv.view.RadioViewPager;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

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
        ProgramSearchFragment.OnFragmentInteractionListener {

    RadioViewPager pager; // The pager widget, which handles animation and allows swiping horizontally to access side screens
    SidePageTransformer pageTransformer;
    MainFragment mainFragment; // Keep the same main fragment across different page adapters
    String selectedTabTag;
    int mainPagePosition; // The position of the main page changes depending on the selected tab
    RadioPlayer radioPlayer;

    private int selectedProgramCategory;
    private TopicInfo selectedProgramTopic;

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

        radioPlayer = new RadioPlayer(this);
        radioPlayer.addListener(this);

        // Hockeyapp
        checkForUpdates();

        // Initialize singletons
        Storage.initialize(this);
        RestClient.initialize(getResources().getString(R.string.url_api));

        // Start on "Offline" tab if started by DownloadNotificationReceiver
        Intent callingIntent = getIntent();
        long[] downloadIds = callingIntent.getLongArrayExtra(DownloadNotificationReceiver.EXTRA_DOWNLOAD_IDS);
        if (downloadIds != null) {
            Log.d("JJJ", "MainActivity got started with " + downloadIds.length + " downloadIds as a parameter");
            mainFragment.setStartupTab(MainFragment.TAG_TAB_OFFLINE);
        }
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
        // Hockeyapp
        if (BuildConfig.HOCKEYAPP_UPDATES_ENABLED) {
            UpdateManager.unregister();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Hockeyapp
        checkForCrashes();
    }

    @Override
    public void onBackPressed() {
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

    /**
     * Hide/show the on-screen keyboard. NOTE: Only works when called from an Activity. Does not work when called from a Fragment (this won't work because you'll be passing a reference to the Fragment's host Activity, which will have no focused control while the Fragment is shown)
     * http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
     */
    public void setKeyboardVisible(boolean show) {
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
                setSelectedProgramCategory(selectedProgramCategory, selectedProgramTopic);
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
        if (playerFragment.getSize() == PlayerFragment.PlayerSize.BIG) {
            playerFragment.setSize(PlayerFragment.PlayerSize.SMALL); // Small player
        }

        mainFragment.setTabSize(MainFragment.TabSize.NORMAL); // Normal tab size
        mainFragment.setError(null); // Clear error message

        // Show/hide keyboard
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null){
//            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
//        }
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

    /**
     * Hides the on-screen keyboard. NOTE: Only works when called from an Activity. Does not work when called from a Fragment (this won't work because you'll be passing a reference to the Fragment's host Activity, which will have no focused control while the Fragment is shown)
     * http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public RadioPlayer getRadioPlayer() {
        return radioPlayer;
    }

    @Override
    public void OnBusy(RadioPlayer player) {

    }

    @Override
    public void OnStarted(RadioPlayer player) {
        final int programId = player.getProgramId();

        // Store player history
        String date = DateTime.now().toString(RestClient.getDateFormat());
        Log.d("JJJ", "addPlayerHistory programId " + programId + " date " + date);
        Storage.get().addPlayerHistory(programId, date);

        // Store related programs
        RestClient.getApi().getProgram(programId).enqueue(new Callback<Program>() {
            @Override
            public void onResponse(Response<Program> response) {
                MainActivity.this.onError(null);
                Program program = response.body();
                if ((program != null) && (program.getRelatedPrograms() != null)) {
                    // TODO use this code when API returns something real instead of NULL for getVideoProgramId()
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
                    Log.d("JJJ", "relatedPrograms " + relatedProgramIds.size() + " for programId " + programId);
                    if (relatedProgramIds.size() > 0) {
                        Storage.get().addRelatedPrograms(programId, relatedProgramIds);
                    }

                    // BEGIN WORKAROUND
//                    // Slug is returned, but programId is null
//                    ArrayList<String> relatedProgramSlugs = new ArrayList<>(program.getRelatedPrograms().size());
//                    for (RelatedProgram p : program.getRelatedPrograms()) {
//                        relatedProgramSlugs.add(p.getSlug());
//                    }
//                    // We have to look up slug to get the programId. TODO if the workaround is here to stay: cache this - save slug in program table along with all the other ProgramInfo
//                    for (String slug : relatedProgramSlugs) {
//                        RestClient.getApi().getProgram(slug).enqueue(new Callback<Program>() {
//                            @Override
//                            public void onResponse(Response<Program> response) {
//                                MainActivity.this.onError(null);
//                                Program program = response.body();
//                                if (program != null) {
//                                    ArrayList<Integer> relatedProgramIds = new ArrayList<>(program.getRelatedPrograms().size());
//                                    relatedProgramIds.add(program.getVideoProgramId());
//                                    Log.d("JJJ", "relatedPrograms " + relatedProgramIds.size() + " for programId " + programId);
//                                    if (relatedProgramIds.size() > 0) {
//                                        Storage.get().addRelatedPrograms(programId, relatedProgramIds);
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Throwable t) {
//                                MainActivity.this.onError(t.getLocalizedMessage());
//                                Log.d("JJJ", "fail " + t.getMessage());
//                                t.printStackTrace();
//                            }
//                        });
//                    }
                    // END WORKAROUND
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

    }

    @Override
    public void OnPaused(RadioPlayer player) {

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

