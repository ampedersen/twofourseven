package com.molamil.radio24syv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

import com.molamil.radio24syv.view.RadioViewPager;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

public class IntroActivity extends FragmentActivity {

    private RadioViewPager pager; // The pager widget, which handles animation and allows swiping horizontally to access side screens
    private int currentPage = 0;
    private int previousPage = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        pager = (RadioViewPager) findViewById(R.id.pager);
        pager.setAdapter(new IntroPagerAdapter(getSupportFragmentManager())); // The pager adapter, which provides the pages to the view pager widget
        //pager.setPageTransformer(false, pageTransformer);
        pager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER); // No feedback when trying to scroll but there are no next page (Android 4 blue edge tint)
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrollStateChanged(int state)
            {
                if(state == ViewPager.SCROLL_STATE_IDLE)
                {
                    updateOnPageChange();
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                previousPage = currentPage;
                currentPage = position;

            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        returningUserCheck();
    }

    private void startMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish(); // Stop this activity
    }

    private void returningUserCheck()
    {
        String name = getApplicationContext().getPackageName();
        SharedPreferences settings = getSharedPreferences(name, Context.MODE_PRIVATE);

        if (settings.getBoolean("returningUser", false))
        {
            //Un-comment for correct flow
            //startMainActivity();
        }
        else
        {
            settings.edit().putBoolean("returningUser", true).commit();
        }
    }

    private void updateOnPageChange()
    {
        cleanupVideo(previousPage);

        if(currentPage == 0)
        {
            //
        }
        else if (currentPage == pager.getAdapter().getCount()-1)
        {
            pager.setPagingEnabled(false);
            startMainActivity();
        }
        else
        {
            //prepare video
            playVideo(currentPage);
        }
    }

    /*
    Video
     */
    private void cleanupVideo(int position)
    {
        IntroPagerAdapter adapter = ((IntroPagerAdapter)pager.getAdapter());
        OnboardingPageFragment fragment = (OnboardingPageFragment)adapter.getRegisteredFragment(position);
        if(fragment != null)
        {
            fragment.cleanupVideo();
        }
    }

    private void playVideo(int position)
    {
        IntroPagerAdapter adapter = ((IntroPagerAdapter)pager.getAdapter());
        OnboardingPageFragment fragment = (OnboardingPageFragment)adapter.getRegisteredFragment(position);
        if(fragment != null)
        {
            fragment.playVideo();
        }
    }

    /*
    Custom page adaptors - one for each tab on the main screen.
    This is the best way to change contents of ViewPager on the fly.
     */
    private class IntroPagerAdapter extends FragmentStatePagerAdapter
    {
        SparseArray<OnboardingPageFragment> registeredFragments = new SparseArray<OnboardingPageFragment>();

        public IntroPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            OnboardingPageFragment fragment = new OnboardingPageFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            args.putString("packageName", getPackageName());
            fragment.setArguments(args);
            registeredFragments.put(position, fragment);

            return fragment;
        }

        @Override
        public int getCount()
        {
            return 6; //intro + 4x Video + empty that loads main
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position)
        {
            return registeredFragments.get(position);
        }
    }
}
