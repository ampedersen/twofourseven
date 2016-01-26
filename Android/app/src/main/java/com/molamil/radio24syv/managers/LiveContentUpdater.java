package com.molamil.radio24syv.managers;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.molamil.radio24syv.R;
import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.Broadcast;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.util.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;

/**
 * Created by patriksvensson on 25/01/16.
 * Running handler in background and checking if it necessary to update live content
 */
public class LiveContentUpdater
{
    private static LiveContentUpdater mInstance = null;
    private int mInterval = 5 *1000; // N seconds (* 1000 for millis)
    private boolean isLoadingCurrentBroadcast = false;
    private Handler mHandler;
    private RadioPlayer.RadioPlayerProvider radioPlayerProvider;

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            checkContent();
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };
    private boolean started = false;

    private ArrayList<OnUpdateListener> listenerList = new ArrayList<>();
    public interface OnUpdateListener {
        void OnUpdate(Broadcast broadcast);
    }

    public static LiveContentUpdater getInstance(){
        if(mInstance == null)
        {
            mInstance = new LiveContentUpdater();
        }
        return mInstance;
    }

    private LiveContentUpdater()
    {
        mHandler = new Handler();
    }

    public void start()
    {
        if(started)
        {
            return;
        }
        started = true;
        mStatusChecker.run();
    }

    public void stop()
    {
        if(!started)
        {
            return;
        }
        started = false;
        mHandler.removeCallbacks(mStatusChecker);
    }

    private void checkContent()
    {
        if(radioPlayerProvider == null)
        {
            return;
        }
        RadioPlayer player = radioPlayerProvider.getRadioPlayer();
        if(player != null)
        {
            if(player.isLive())
            {
                float progress = getProgress();
                if(progress >= 0.995 && !isLoadingCurrentBroadcast)
                {
                    loadBroadcast();
                }
            }
        }
    }

    private void loadBroadcast()
    {
        isLoadingCurrentBroadcast = true;
        RestClient.getApi().getCurrentBroadcast().enqueue(new Callback<List<Broadcast>>()
        {
            @Override
            public void onResponse(Response<List<Broadcast>> response)
            {
                if (response == null)
                {
                    isLoadingCurrentBroadcast = false;
                    return;
                }

                RadioPlayer player = radioPlayerProvider.getRadioPlayer();
                List<Broadcast> body = response.body();
                if (body != null && player.isLive())
                {
                    Broadcast b = body.get(0);

                    //Update player data. Updated data will stop manager from trying to reload new content since progress will get reset
                    player.updateData(
                            player.getUrl(),
                            b.getProgramName(),
                            b.getDescriptionText(),
                            b.getProgramName(),
                            b.getTopic(),
                            RestClient.getLocalTime(b.getBroadcastTime().getStart()),
                            RestClient.getLocalTime(b.getBroadcastTime().getEnd()),
                            -1,
                            player.getProgramId());
                    
                    //Update even if there's no change
                    for (OnUpdateListener l : listenerList)
                    {
                        l.OnUpdate(b);
                    }

                }

                isLoadingCurrentBroadcast = false;
            }

            @Override
            public void onFailure(Throwable t)
            {
                isLoadingCurrentBroadcast = false;
            }
        });
    }

    private float getProgress()
    {
        float pct = -1;

        RadioPlayer player = radioPlayerProvider.getRadioPlayer();
        String start = player.getStartTime();
        String end = player.getEndTime();
        Date now = new Date();
        SimpleDateFormat sdfr = new SimpleDateFormat("HH:mm");
        Date curr = DateUtils.timeStringToDate(sdfr.format(now));

        Date startDate = DateUtils.timeStringToDate(start);
        Date endDate = DateUtils.timeStringToDate(end);


        if(startDate != null && endDate != null && curr != null) {
            long t0 = startDate.getTime();
            long t1 = endDate.getTime();
            long t = curr.getTime();

            float duration = t1 - t0;
            float time = t - t0;

            pct = time / duration;
        }

        return pct;
    }

    public void setRadioPlayerProvider(RadioPlayer.RadioPlayerProvider radioPlayerProvider) {
        this.radioPlayerProvider = radioPlayerProvider;
    }

    public void addListener(OnUpdateListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public void removeListener(OnUpdateListener listener) {
        if (listenerList.contains(listener)) {
            listenerList.remove(listener);
        }
    }
}
