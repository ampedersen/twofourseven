package com.molamil.radio24syv.storage.model;

import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.Broadcast;
import com.molamil.radio24syv.storage.Storage;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Information about a Podcast from the API containing only the stuff needed.
 * Dependency on the API is handled through this class. And it is also lighter to pass as serializable.
 * Created by jens on 24/09/15.
 */
public class BroadcastInfo implements Serializable {
    private int programId;
    private String programSlug;
    private String name;
    private String description;
    private String topic;
    private String timeBegin;
    private String timeEnd;

    public BroadcastInfo() {
    }

    public BroadcastInfo(Broadcast broadcast) {
        programId = RestClient.getIntegerSafely(broadcast.getVideoProgramId(), Storage.PROGRAM_ID_UNKNOWN);
        programSlug = broadcast.getProgramSlug();
        name = broadcast.getProgramName();
        description = broadcast.getDescriptionText();
        topic = broadcast.getTopic();
        timeBegin = broadcast.getBroadcastTime().getStart();
        timeEnd = broadcast.getBroadcastTime().getEnd();
    }

    public boolean isPlayingNow() {
        long now = DateTime.now().getMillis();
        long begin = new DateTime(timeBegin).getMillis();
        long end = new DateTime(timeEnd).getMillis();
        return (begin <= now) && (now < end);
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTimeBegin() {
        return timeBegin;
    }

    public void setTimeBegin(String timeBegin) {
        this.timeBegin = timeBegin;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getProgramSlug() {
        return programSlug;
    }

    public void setProgramSlug(String programSlug) {
        this.programSlug = programSlug;
    }
}
