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
        LiveFragment.OnFragmentInteractionListener,
        LiveScheduleFragment.OnFragmentInteractionListener,
        ProgramsFragment.OnFragmentInteractionListener,
        NewsFragment.OnFragmentInteractionListener,
        OfflineFragment.OnFragmentInteractionListener {

    RadioViewPager pager; // The pager widget, which handles animation and allows swiping horizontally to access previous and next wizard steps
    SidePageTransformer pageTransformer;
    MainFragment mainFragment; // Keep the same main fragment across different page adaptors
    String selectedTabTag;
    int mainPagePosition; // The position of the main page changes depending on the selected tab

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = (RadioViewPager) findViewById(R.id.pager);
        mainFragment = new MainFragment();
        pager.setAdapter(new LiveTabPagerAdapter(getSupportFragmentManager())); // The pager adapter, which provides the pages to the view pager widget
        pageTransformer = new SidePageTransformer(SidePageTransformer.TransformType.SLIDE_OVER);
        pager.setPageTransformer(false, pageTransformer);
        pager.setCurrentItem(0); // Start on the first page
        pager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER); // No feedback when trying to scroll but there are no next page
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    @Override
    public void onMainTabChanged(String tabTag) {
        //Log.d("JJJ", "onMainTabChanged "+ tabTag);
        if (tabTag.equals(selectedTabTag)) {
            return; // Return, nothing changed
        }

        selectedTabTag = tabTag;

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

    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onShowSidePageInteraction(Side side, Class<?> fragment) {
        Log.d("JJJ", "Show side page " + fragment + " on the " + side);
    }

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
                    return new LiveScheduleFragment();
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
                    return new LiveScheduleFragment(); //TODO
                case 1:
                    return mainFragment;
                case 2:
                    return new LiveScheduleFragment(); //TODO
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

