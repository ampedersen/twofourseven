package com.molamil.radio24syv;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ProgramsFragment extends PageFragment {

    OnFragmentInteractionListener mListener;

    RadioViewPager pager; // The pager widget, which handles animation and allows swiping horizontally to access side screens
    SidePageTransformer pageTransformer;
    ProgramListFragment programListFragment;

    public ProgramsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_programs, container, false);

        programListFragment = new ProgramListFragment();

        pageTransformer = new SidePageTransformer(SidePageTransformer.TransformType.SLIDE_OVER);
        pager = (RadioViewPager) v.findViewById(R.id.pager);
        pager.setAdapter(new ListPagerAdapter(getChildFragmentManager())); // The pager adapter, which provides the pages to the view pager widget
        pager.setPageTransformer(false, pageTransformer);
        pager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER); // No feedback when trying to scroll but there are no next page (Android 4 blue edge tint)
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("JJJ", "position " + position + " offset " + positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        if (mListener != null) {
                            mListener.onEnableSidePageInteraction(true);
                        }
                        break;
                    case 1:
                        if (mListener != null) {
                            mListener.onEnableSidePageInteraction(false);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    switch (pager.getCurrentItem()) {
                        case 0:
                            if (pager.getAdapter().getCount() > 0) {
                                pager.setAdapter(new ListPagerAdapter(getChildFragmentManager())); // TODO animate and then change page adapter, this has no animation
                            }
                            break;
                        case 1:
                            break;
                    }
                }
            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public boolean isShowingDetails() {
        return (pager.getCurrentItem() == 1);
    }

    public void showList() {
        if (isShowingDetails()) {
            pager.setCurrentItem(0, true); // Back to list
        }
    }

    public void showDetails(String programId) {
        pager.setAdapter(new DetailsPagerAdapter(getChildFragmentManager(), programId));
        //pager.setCurrentItem(1, true); // This changes page instantly even though told otherwise. It happens when setCurrentItem() is called straight after changing adapter.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                pager.setCurrentItem(1, true); // This animates the page as expected. It is executed a jiffy after the adapter is changed, and probably works because the ViewPager has had time to instantiate the fragment's views.
            }
        });
    }

    // Adapter for program list page only
    private class ListPagerAdapter extends FragmentStatePagerAdapter {
        public ListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return programListFragment;
            }
            Log.e("JJJ", "Unable to determine a fragment for page " + position + " - returning null");
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

    // Adapter for shows program list page and program details page
    private class DetailsPagerAdapter extends FragmentStatePagerAdapter {
        final String programId;

        public DetailsPagerAdapter(FragmentManager fm, String programId) {
            super(fm);
            this.programId = programId;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return programListFragment;
                case 1:
                    return ProgramDetailsFragment.newInstance(programId);
            }
            Log.e("JJJ", "Unable to determine a fragment for page " + position + " - returning null");
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public interface OnFragmentInteractionListener extends PageFragment.OnFragmentInteractionListener {
        public void onEnableSidePageInteraction(boolean enable);
    }


}
