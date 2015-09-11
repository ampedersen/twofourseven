package com.molamil.radio24syv;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class MainFragment extends Fragment {

    public static final String TAG_TAB_LIVE = "LiveTab";
    public static final String TAG_TAB_PROGRAMS = "ProgramsTab";
    public static final String TAG_TAB_NEWS = "NewsTab";
    public static final String TAG_TAB_OFFLINE = "OfflineTab";

    public enum Dimming { NONE, DIM }
    static final long DIMMING_DURATION_MS = 500;
    Dimming dimming = Dimming.NONE;

    OnMainFragmentInteractionListener listener;
    FragmentTabHost tabHost;
    PlayerFragment playerFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        tabHost = (FragmentTabHost)v.findViewById(android.R.id.tabhost);
        tabHost.setup(inflater.getContext(), getChildFragmentManager(), android.R.id.tabcontent);

        addTab(inflater, TAG_TAB_LIVE, R.string.tab_live, android.R.drawable.btn_star, LiveFragment.class);
        addTab(inflater, TAG_TAB_PROGRAMS, R.string.tab_programs, android.R.drawable.btn_star, ProgramsFragment.class);
        addTab(inflater, TAG_TAB_NEWS, R.string.tab_news, android.R.drawable.btn_star, NewsFragment.class);
        addTab(inflater, TAG_TAB_OFFLINE, R.string.tab_offline, android.R.drawable.btn_star, OfflineFragment.class);

        tabHost.getTabWidget().setDividerDrawable(null);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (listener != null) {
                    listener.onMainTabChanged(tabId);
                }
            }
        });

        tabHost.setCurrentTab(0);

//        if (playerFragment == null) {
//            playerFragment = new PlayerFragment();
//        }

        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
//        if (savedInstanceState == null) {
            // Create a new Fragment to be placed in the activity layout
//            PlayerFragment playerFragment = new PlayerFragment();

            // Add the fragment to the container
//            getChildFragmentManager().beginTransaction()
//                    .add(R.id.player_fragment_container, playerFragment)
//                    .commit();
//        }

        if (savedInstanceState != null) {
            playerFragment = (PlayerFragment) getChildFragmentManager().findFragmentByTag(PlayerFragment.class.getName());
        } else {
            if (playerFragment == null) {
                playerFragment = new PlayerFragment();
            }
        }
        getChildFragmentManager().beginTransaction().replace(R.id.player_fragment_container, playerFragment, PlayerFragment.class.getName()).commit();

        // create fragments to use
//        if (savedInstanceState != null) {
//            playerFragment = (PlayerFragment) getChildFragmentManager().getFragment(
//                    savedInstanceState, PlayerFragment.class.getName());
//        }
//        if (playerFragment == null)
//            playerFragment = new PlayerFragment();
//
//        getChildFragmentManager().beginTransaction().replace(R.id.player_fragment_container, playerFragment, PlayerFragment.class.getName()).commit();

        updateDimming(v, false);

        return v;
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//
//        super.onSaveInstanceState(savedInstanceState);
//        getChildFragmentManager()
//                .putFragment(savedInstanceState, PlayerFragment.class.getName(), playerFragment);
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnMainFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMainFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void addTab(LayoutInflater inflater, String tag, int textId, int iconId, Class<?> fragment) {
        View indicator = inflater.inflate(R.layout.tab_indicator, tabHost.getTabWidget(), false);
        ((ImageView) indicator.findViewById(R.id.tab_indicator_icon)).setImageResource(android.R.drawable.btn_star);
        ((TextView) indicator.findViewById(R.id.tab_indicator_text)).setText(getResources().getText(textId));

        tabHost.addTab(tabHost.newTabSpec(tag).setIndicator(indicator), fragment, null);
    }

    public void setDimming(Dimming dimming) {
        Log.d("JJJ", "setdim " + dimming + " was " + this.dimming);
        if (dimming == this.dimming) {
            return; // Return, already dimmed like that
        }

        this.dimming = dimming;

        updateDimming(getView(), true);
    }

    private void updateDimming(View parentView, boolean isAnimated) {
        View dimmer = parentView.findViewById(R.id.dimmer);

        long targetAlpha;
        if (dimming == Dimming.NONE) {
            targetAlpha = 0; // No dimming
        } else {
            targetAlpha = 1; // Full dimming
        }

        Log.d("JJJ", "dim from " + dimmer.getAlpha() + " to " + targetAlpha);
//        boolean isAnimated = (this.dimming != null); // Animate if dimming has been assigned before
        if (isAnimated) {
            dimmer.animate().alpha(targetAlpha).setDuration(DIMMING_DURATION_MS);
        } else {
            dimmer.setAlpha(targetAlpha);
        }
    }

    public interface OnMainFragmentInteractionListener {
        public void onMainTabChanged(String tabTag);
    }
}
