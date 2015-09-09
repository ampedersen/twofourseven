package com.molamil.radio24syv;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;


public class MainFragment extends Fragment implements
    PlayerFragment.OnMyFragmentInteractionListener {

    OnMainFragmentInteractionListener mListener;
    FragmentTabHost mTabHost;

    public static final String TAG_TAB_LIVE = "LiveTab";
    public static final String TAG_TAB_PROGRAMS = "ProgramsTab";
    public static final String TAG_TAB_NEWS = "NewsTab";
    public static final String TAG_TAB_OFFLINE = "OfflineTab";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        mTabHost = (FragmentTabHost)v.findViewById(android.R.id.tabhost);
        mTabHost.setup(inflater.getContext(), getChildFragmentManager(), android.R.id.tabcontent);

        addTab(inflater, TAG_TAB_LIVE, R.string.tab_live, android.R.drawable.btn_star, LiveFragment.class);
        addTab(inflater, TAG_TAB_PROGRAMS, R.string.tab_programs, android.R.drawable.btn_star, ProgramsFragment.class);
        addTab(inflater, TAG_TAB_NEWS, R.string.tab_news, android.R.drawable.btn_star, NewsFragment.class);
        addTab(inflater, TAG_TAB_OFFLINE, R.string.tab_offline, android.R.drawable.btn_star, OfflineFragment.class);

        mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (mListener != null) {
                    mListener.onMainTabChanged(tabId);
                }
            }
        });

        mTabHost.setCurrentTab(0);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMainFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMainFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void addTab(LayoutInflater inflater, String tag, int textId, int iconId, Class<?> fragment) {
        View indicator = inflater.inflate(R.layout.tab_indicator, mTabHost.getTabWidget(), false);
        ((ImageView) indicator.findViewById(R.id.tab_indicator_icon)).setImageResource(android.R.drawable.btn_star);
        ((TextView) indicator.findViewById(R.id.tab_indicator_text)).setText(getResources().getText(textId));

        mTabHost.addTab(mTabHost.newTabSpec(tag).setIndicator(indicator), fragment, null);
    }

    @Override
    public void onPlayerFragmentInteraction(Uri uri) {

    }

    public interface OnMainFragmentInteractionListener {
        public void onMainTabChanged(String tabTag);
    }
}
