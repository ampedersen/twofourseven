package com.molamil.radio24syv;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class MainActivity extends FragmentActivity implements
        MainFragment.OnMainFragmentInteractionListener,
        PageFragment.OnFragmentInteractionListener,
        ScheduleFragment.OnFragmentInteractionListener,
        NewsFragment.OnFragmentInteractionListener,
        OfflineFragment.OnFragmentInteractionListener,
        PlayerFragment.OnFragmentInteractionListener {

    ViewPager pager; // The pager widget, which handles animation and allows swiping horizontally to access previous and next wizard steps
    SidePageTransformer pageTransformer;
    MainFragment mainFragment; // Keep the same main fragment across different page adaptors
    String selectedTabTag;
    int mainPagePosition; // The position of the main page changes depending on the selected tab

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFragment = new MainFragment();

        pageTransformer = new SidePageTransformer(SidePageTransformer.TransformType.SLIDE_OVER);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new LiveTabPagerAdapter(getSupportFragmentManager())); // The pager adapter, which provides the pages to the view pager widget
        pager.setPageTransformer(false, pageTransformer);
        pager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER); // No feedback when trying to scroll but there are no next page (Android 4 blue edge tint)
    }

    @Override
    public void onBackPressed() {
        boolean isViewingMainPage = (pager.getCurrentItem() == mainPagePosition);
        if (isViewingMainPage) {
            super.onBackPressed(); // Return to system
        } else {
            pager.setCurrentItem(mainPagePosition); // Back to main page
        }
    }

    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onShowSidePageInteraction(Side side) {
        Log.d("JJJ", "Show side page " + side);
        switch (side) {
            case HIDE:
                pager.setCurrentItem(mainPagePosition);
                break;
            case SHOW_LEFT:
                pager.setCurrentItem(mainPagePosition - 1);
                break;
            case SHOW_RIGHT:
                pager.setCurrentItem(mainPagePosition + 1);
                break;
        }
    }

    @Override
    public void onPlayerSizeChanged(PlayerFragment.PlayerSize newSize, PlayerFragment.PlayerSize oldSize) {
        if (newSize == PlayerFragment.PlayerSize.BIG) {
            mainFragment.setDimming(MainFragment.Dimming.DIM);
        } else {
            mainFragment.setDimming(MainFragment.Dimming.NONE);
        }
    }

    @Override
    public void onPlayerControl(PlayerFragment.PlayerAction action) {
        Log.d("JJJ", "onPlayerControl " + action);
        PlayerFragment playerFragment = (PlayerFragment)mainFragment.getChildFragmentManager().findFragmentById(R.id.player_fragment_container);
        if (action == PlayerFragment.PlayerAction.PLAY) {
            playerFragment.setPlaying(true);
        } else {
            playerFragment.setPlaying(false);
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
                    return new CategoriesFragment();
                case 1:
                    return mainFragment;
                case 2:
                    return new SearchFragment();
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

