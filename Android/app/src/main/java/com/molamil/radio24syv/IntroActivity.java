package com.molamil.radio24syv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.molamil.radio24syv.view.RadioViewPager;

public class IntroActivity extends FragmentActivity {

    private RadioViewPager pager; // The pager widget, which handles animation and allows swiping horizontally to access side screens
    private int currentPage = 0;
    private int previousPage = -1;

    private FrameLayout[] dots;
    private FrameLayout[] selectedDots;

    private Button skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        skipButton = (Button) findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startMainActivity();
            }
        });

        pager = (RadioViewPager) findViewById(R.id.pager);
        pager.setAdapter(new IntroPagerAdapter(getSupportFragmentManager())); // The pager adapter, which provides the pages to the view pager widget
        //pager.setPageTransformer(false, pageTransformer);
        pager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER); // No feedback when trying to scroll but there are no next page (Android 4 blue edge tint)
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    updateOnPageChange();
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                previousPage = currentPage;
                currentPage = position;

            }

        });

        updateUI(0);

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
            startMainActivity();
        }
        else
        {
            settings.edit().putBoolean("returningUser", true).commit();
        }
    }

    private void updateOnPageChange()
    {
        if(previousPage == currentPage)
        {
            return;
        }

        //previousPage = currentPage;

        cleanupVideo(previousPage);
        updateUI(currentPage);

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
            playVideo(currentPage);
        }
    }

    /*UI*/
    private void updateUI(int page)
    {
        if(dots == null)
        {

            dots = new FrameLayout[5];
            selectedDots = new FrameLayout[5];
            dots[0] = (FrameLayout)findViewById(R.id.page_dot_0);
            dots[1] = (FrameLayout)findViewById(R.id.page_dot_1);
            dots[2] = (FrameLayout)findViewById(R.id.page_dot_2);
            dots[3] = (FrameLayout)findViewById(R.id.page_dot_3);
            dots[4] = (FrameLayout)findViewById(R.id.page_dot_4);
            selectedDots[0] = (FrameLayout)findViewById(R.id.selected_page_dot_0);
            selectedDots[1] = (FrameLayout)findViewById(R.id.selected_page_dot_1);
            selectedDots[2] = (FrameLayout)findViewById(R.id.selected_page_dot_2);
            selectedDots[3] = (FrameLayout)findViewById(R.id.selected_page_dot_3);
            selectedDots[4] = (FrameLayout)findViewById(R.id.selected_page_dot_4);
        }

        for(int i=0;i<5;i++)
        {
            boolean k = i>page;
            dots[i].setVisibility(k? View.VISIBLE:View.GONE);
            selectedDots[i].setVisibility(k? View.GONE:View.VISIBLE);
        }

        skipButton.setText(page<4?getResources().getString(R.string.onboarding_skip):getResources().getString(R.string.onboarding_done));
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
