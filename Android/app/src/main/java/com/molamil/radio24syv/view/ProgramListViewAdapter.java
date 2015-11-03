package com.molamil.radio24syv.view;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.molamil.radio24syv.R;
import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.Podcast;
import com.molamil.radio24syv.api.model.Program;
import com.molamil.radio24syv.player.RadioPlayer;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.PodcastInfo;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.storage.model.TopicInfo;
import com.molamil.radio24syv.view.ProgramButtonView;
import com.molamil.radio24syv.view.ProgramImageView;
import com.molamil.radio24syv.view.RadioPlayerButton;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.Response;

/**
 * Created by jens on 08/10/15.
 */
public class ProgramListViewAdapter extends ArrayAdapter<ProgramInfo> {
    private final Activity context;
    private final ProgramInfo[] programs;
    private RadioPlayer radioPlayer;

    private static class ViewHolder {
        public TextView name;
        public TextView description;
        public TextView topic;
        public ProgramImageView image;
        public RadioPlayerButton playButton;
    }

    public ProgramListViewAdapter(Activity context, ProgramInfo[] programs, RadioPlayer radioPlayer) {
        super(context, R.layout.view_program_button, programs);
        this.context = context;
        this.programs = programs;
        this.radioPlayer = radioPlayer;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final ViewHolder holder;

        // Reuse views
        if (v == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            v = inflater.inflate(R.layout.view_program_button, null);
            // Configure view holder
            holder = new ViewHolder();
            holder.name = (TextView) v.findViewById(R.id.name_text);
            holder.description = (TextView) v.findViewById(R.id.description_text);
            holder.topic = (TextView) v.findViewById(R.id.topic_text);
            holder.image = (ProgramImageView) v.findViewById(R.id.image);
            holder.playButton = (RadioPlayerButton) v.findViewById(R.id.play_button);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag(); // Get existing view holder
        }

        // Fill data
        final ProgramInfo program = programs[position];
        holder.name.setText(program.getName());
        holder.description.setText(program.getDescription());
        holder.topic.setText(program.getTopic());
        TopicInfo topic = Storage.get().getTopic(program.getTopicId());
        if (topic != null) {
            holder.image.setTintColor(topic.getColorValue());
        }

        holder.image.setImageUrl(program.getAppImageOverviewUrl());

        holder.playButton.setTitle(program.getName());
        holder.playButton.setDescription(program.getDescription());
        holder.playButton.setRadioPlayer(radioPlayer);

        holder.playButton.setTopic(program.getTopic());
        holder.playButton.setStartTime(program.getStartTime());
        holder.playButton.setEndTime(program.getEndTime());

        holder.playButton.setRadioPlayButtonListener(new RadioPlayerButton.OnRadioPlayerButtonListener()
        {
            @Override
            public void onClick(int action)
            {
                playLatestPodcastForListItem(program, holder.playButton);
            }
        });
        return v;
    }

    //TODO: Cancel previous calls
    private void playLatestPodcastForListItem(ProgramInfo program, final RadioPlayerButton button)
    {
        RestClient.getApi().getPodcasts(program.getProgramId(), 1).enqueue(new Callback<List<Podcast>>() {
            @Override
            public void onResponse(Response<List<Podcast>> response) {

                if(response.body().size() > 0)
                {
                    PodcastInfo podcast = new PodcastInfo(response.body().get(0));
                    Storage.get().addPodcast(podcast);
                    button.setUrl(context.getString(R.string.url_offline_radio) + podcast.getAudioUrl());
                    button.clickAction();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                //Log.d("PS", "fail " + t.getMessage());
                //t.printStackTrace();
            }
        });

    }

}