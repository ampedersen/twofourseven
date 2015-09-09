package com.molamil.radio24syv;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class MainActivity extends FragmentActivity implements
        MainFragment.OnMainFragmentInteractionListener,
        LiveFragment.OnFragmentInteractionListener,
        LiveScheduleFragment.OnFragmentInteractionListener,
        ProgramsFragment.OnFragmentInteractionListener,
        NewsFragment.OnFragmentInteractionListener,
        OfflineFragment.OnFragmentInteractionListener {



    final int PAGE_INDEX_LEFT = 0;
    final int PAGE_INDEX_MAIN = 1;
    final int PAGE_INDEX_RIGHT = 2;

    String selectedTabTag;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private RadioViewPager pager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;

    SidePageTransformer pageTransformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        selectedTabTag = MainFragment.TAG_TAB_LIVE;

        // Instantiate a ViewPager and a PagerAdapter.
        pager = (RadioViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

//        pageTransformer = new SidePageTransformer(SidePageTransformer.TransformType.INSTANT);
//        pager.setPageTransformer(false, pageTransformer);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int MANDATORY_PAGE_LOCATION = 1;
                if (position == MANDATORY_PAGE_LOCATION) {// && positionOffset > 0.5) {
//                    pageTransformer.setTransformType(SidePageTransformer.TransformType.INSTANT);
                    pager.setCurrentItem(MANDATORY_PAGE_LOCATION, true);
                } else {
//                    pageTransformer.setTransformType(SidePageTransformer.TransformType.INSTANT);
                }

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        pager.setCurrentItem(PAGE_INDEX_MAIN);

    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == PAGE_INDEX_MAIN) {
            super.onBackPressed(); // Return to system if viewing main page
        } else {
            pager.setCurrentItem(PAGE_INDEX_MAIN); // Back to main page if viewing a sub page
        }
    }

    @Override
    public void onMainTabChanged(String tabTag) {
        Log.d("JJJ", "gay "+ tabTag);
        selectedTabTag = tabTag;

        switch (selectedTabTag) {
            case MainFragment.TAG_TAB_LIVE:
                pager.setPagingEnabled(true);
                break;
            case MainFragment.TAG_TAB_PROGRAMS:
                pager.setPagingEnabled(true);
                break;
            case MainFragment.TAG_TAB_NEWS:
                pager.setPagingEnabled(false); "skrot det her, lad spring-back være løsningen for nu, lav senere med fin PageTransformerRubberbandBackAnimationThingy(tm)"
                break;
            case MainFragment.TAG_TAB_OFFLINE:
                pager.setPagingEnabled(false);
                break;
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        String currentTabTag = "";

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case PAGE_INDEX_LEFT:
                    return new LiveFragment();
                case PAGE_INDEX_MAIN:
                    return new MainFragment();
                case PAGE_INDEX_RIGHT:
                    return new ProgramsFragment();
                default:
                    Log.e("JJJ", "Unable to determine a fragment for page " + position + " returning null");
                    return null;
            }

//            switch (selectedTabTag) {
//                case MainFragment.TAG_TAB_LIVE:
//                    switch (position) {
//                        case 0:
//                            return new MainFragment();
//                        case 1:
//                            return new LiveScheduleFragment();
//                    }
//                case MainFragment.TAG_TAB_PROGRAMS:
//                    switch (position) {
//                        case 0:
//                            return new LiveScheduleFragment(); // TODO
//                        case 1:
//                            return new MainFragment();
//                        case 2:
//                            return new LiveScheduleFragment(); // TODO
//                    }
//                case MainFragment.TAG_TAB_NEWS:
//                    return new MainFragment();
//                case MainFragment.TAG_TAB_OFFLINE:
//                    return new MainFragment();
//
//            }
//            Log.e("JJJ", "Unable to determine a fragment for " + selectedTabTag + " page " + position + " - returning null");
//            return null;
        }

        @Override
        public int getCount() {
            //return (page0Fragment != null ? 1 : 0) + (page1Fragment != null ? 1 : 0) + (page2Fragment != null ? 1 : 0); // Number of non-null fragments

            // Does not work - confusing the ViewPager, shows wrong pages
//            boolean isTabChanged = (selectedTabTag != currentTabTag);
//            if (isTabChanged) {
//                currentTabTag = selectedTabTag;
//                notifyDataSetChanged();
//                Log.d("JJJ", "nogitft getCount");
//            }
//
//            switch (selectedTabTag) {
//                case MainFragment.TAG_TAB_LIVE:
//                    if (isTabChanged) {
//                        pager.setCurrentItem(0);
//                    }
//                    return 2;
//                case MainFragment.TAG_TAB_PROGRAMS:
//                    if (isTabChanged) {
//                        pager.setCurrentItem(1);
//                    }
//                    return 3;
//                case MainFragment.TAG_TAB_NEWS:
//                    if (isTabChanged) {
//                        pager.setCurrentItem(0);
//                    }
//                    return 1;
//                case MainFragment.TAG_TAB_OFFLINE:
//                    if (isTabChanged) {
//                        pager.setCurrentItem(0);
//                    }
//                    return 1;
//
//            }
//            Log.e("JJJ", "Unable to determine number of pages for " + selectedTabTag + " - returning null");
//            return 0;

            return 3;
        }
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
//        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
//
//        addTab("tab1", R.string.tab_live, android.R.drawable.btn_star, LiveFragment.class);
//        addTab("tab2", R.string.tab_programs, android.R.drawable.btn_star, ProgramsFragment.class);
//        addTab("tab3", R.string.tab_news, android.R.drawable.btn_star, NewsFragment.class);
//        addTab("tab4", R.string.tab_offline, android.R.drawable.btn_star, OfflineFragment.class);
//
//        mTabHost.getTabWidget().setDividerDrawable(null);
//    }
//
//    private void addTab(String tag, int textId, int iconId, Class<?> fragment) {
//        View indicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, mTabHost.getTabWidget(), false);
//        ((ImageView) indicator.findViewById(R.id.tab_indicator_icon)).setImageResource(android.R.drawable.btn_star);
//        ((TextView) indicator.findViewById(R.id.tab_indicator_text)).setText(getResources().getText(textId));
//
//        mTabHost.addTab(mTabHost.newTabSpec(tag).setIndicator(indicator), fragment, null);
//    }

    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onShowSidePageInteraction(Side side, Class<?> fragment) {
        Log.d("JJJ", "Show side page " + fragment + " on the " + side);
    }
}

