package com.molamil.radio24syv;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.Broadcast;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.BroadcastInfo;
import com.molamil.radio24syv.view.DateLineView;
import com.molamil.radio24syv.view.ProgramScheduleButtonView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends PageFragment {

    private PageFragment.OnFragmentInteractionListener listener;
    private ProgramScheduleButtonView.OnProgramScheduleButtonViewListener buttonListener;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_schedule, container, false);

        RestClient.getApi().getNextBroadcasts(20, 0).enqueue(new Callback<List<Broadcast>>() {
            @Override
            public void onResponse(Response<List<Broadcast>> response) {
                if (listener != null) {
                    listener.onError(null);
                }
                showLoadingText(v, false);

                ViewGroup content = (ViewGroup) v.findViewById(R.id.content);
                content.removeAllViews();

                List<Broadcast> broadcasts = response.body();

                DateTime previousDate = null;
                for (int i = 0; i < broadcasts.size(); i++) {
                    BroadcastInfo b = new BroadcastInfo(broadcasts.get(i));

                    // Date line separator between days
                    if (previousDate == null) {
                        previousDate = new DateTime(b.getTimeBegin());
                    } else {
                        DateTime nextDate = new DateTime(b.getTimeBegin());
                        boolean isDifferentDay = (nextDate.getDayOfYear() != previousDate.getDayOfYear()) || (nextDate.getYear() != previousDate.getYear());
                        if (isDifferentDay) {
                            DateLineView dateLine = new DateLineView(v.getContext());
                            dateLine.setDate(previousDate, nextDate);
                            dateLine.setWhiteBackground(true);
                            content.addView(dateLine);
                        }
                        previousDate = nextDate;
                    }

                    // Scheduled program button
                    ProgramScheduleButtonView programButton = new ProgramScheduleButtonView(v.getContext());
                    programButton.setBroadcast(b);
                    programButton.setOnProgramScheduleButtonViewListener(buttonListener);
                    int alarmId = Storage.get().getAlarmId(b.getProgramSlug(), b.getTimeBegin()); // Check if there is an alarm for this program and time
                    boolean isNotificationEnabled = (alarmId != Storage.ALARM_ID_UNKNOWN);
                    programButton.setNotificationEnabled(isNotificationEnabled);
                    content.addView(programButton);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (listener != null) {
                    listener.onError(t.getLocalizedMessage());
                }
                showLoadingText(v, false);
                Log.d("JJJ", "fail " + t.getMessage());
                t.printStackTrace();
            }
        });

        showLoadingText(v, true);

        return v;
    }

    private void showLoadingText(View v, boolean show) {
        View loadingText = v.findViewById(R.id.loading_text);
        int visibility;
        if (show) {
            visibility = View.VISIBLE;
        } else {
            visibility = View.GONE;
        }
        loadingText.setVisibility(visibility);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ScheduleFragment.OnFragmentInteractionListener");
        }
        try {
            buttonListener = (ProgramScheduleButtonView.OnProgramScheduleButtonViewListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ProgramScheduleButtonView.OnProgramScheduleButtonViewListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        buttonListener = null;
    }

    public interface OnFragmentInteractionListener extends PageFragment.OnFragmentInteractionListener {
    }

}
