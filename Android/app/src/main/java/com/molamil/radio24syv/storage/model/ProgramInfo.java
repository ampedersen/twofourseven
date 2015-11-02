package com.molamil.radio24syv.storage.model;

import android.content.Context;
import android.text.TextUtils;

import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.*;
import com.molamil.radio24syv.storage.Storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Information about a ConciseProgram or Program from the API containing only the stuff needed.
 * Dependency on the API is handled through this class. And it is also lighter to pass as serializable.
 * Created by jens on 24/09/15.
 */
public class ProgramInfo implements Serializable {
    private int programId;
    private String programSlug;
    private String name;
    private String topic;
    private String description;
    private String startTime;
    private String endTime;
    private String imageUrl;
    private boolean active;

    /*
    //TODO: Make BroadcastInfo Serializable
    //private com.molamil.radio24syv.api.model.BroadcastInfo broadcastInfo;
    private boolean broadcastWeekly;
    private String broadcastDay;
    private String broadcastTime;

    private List<String> hosts = new ArrayList<String>();
    */

    public ProgramInfo() {}

    public ProgramInfo(ConciseProgram conciseProgram) {
        programId = RestClient.getIntegerSafely(conciseProgram.getVideoProgramId(), Storage.PROGRAM_ID_UNKNOWN);
        programSlug = conciseProgram.getSlug();
        name = conciseProgram.getName();
        topic = conciseProgram.getTopic();
        description = conciseProgram.getDescriptionText();
        imageUrl = conciseProgram.getImageUrl();
        active = conciseProgram.getActive();

    }

    public ProgramInfo(Program program) {
        programId = RestClient.getIntegerSafely(program.getVideoProgramId(), Storage.PROGRAM_ID_UNKNOWN);
        programSlug = program.getSlug();
        name = program.getName();
        topic = program.getTopic();
        description = RestClient.getTextWithoutHtmlTags(program.getDescriptionHtml());
        imageUrl = program.getImageUrl();
        active = program.getActive();

        /*
        //hosts = program.getHosts();
        hosts = new ArrayList<String>();
        for(int i = 0 ; i < program.getHosts().size() ; i++)
        {
            hosts.add(program.getHosts().get(i).getName());
        }

        broadcastWeekly = program.getBroadcastInfo().getWeekly();
        broadcastDay = program.getBroadcastInfo().getDay();
        broadcastTime = program.getBroadcastInfo().getTime();

        //broadcastInfo = program.getBroadcastInfo();
        */
    }

    // TODO this is broken until the API returns an integer instead of null for relatedProgram.getVideoProgramId()
//    public ProgramInfo(RelatedProgram relatedProgram) {
//        programId = RestClient.getIntegerSafely((int) relatedProgram.getVideoProgramId(), Storage.PROGRAM_ID_UNKNOWN); // I suppose this is an integer
//        programSlug = program.getProgramSlug();
//        name = relatedProgram.getName();
//        topic = relatedProgram.getTopic();
//        description = relatedProgram.getDescriptionText();
//        imageUrl = relatedProgram.getImageUrl();
//        active = relatedProgram.getActive();
//    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public String getProgramSlug() {
        return programSlug;
    }

    public void setProgramSlug(String programSlug) {
        this.programSlug = programSlug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public String getTopicId() {
        return topic.toLowerCase();
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /*
    public String getHostsAndTime(Context c)
    {
        if(!active)
        {
            String packageName = c.getPackageName();
            int resId = c.getResources().getIdentifier("old_programs", "string", packageName);
            return c.getResources().getString(resId);
        }

        String result = "";
        String hostStr = getHostNames();
        if(hostStr != "")
        {
            result = hostStr + "\n" + getBroadcastTime(c);
        }
        else
        {
            result = getBroadcastTime(c);
        }

        return result;
    }

    private String getHostNames()
    {
        return TextUtils.join(", ", hosts);
    }

    private String getBroadcastTime(Context c)
    {
        //TODO: Implement using basic data types instead of using BroadcastInfo
        if(startTime == null || endTime == null)
        {
            return "";
        }

        String frequency = broadcastWeekly ? "weekly" : "daily";
        String packageName = c.getPackageName();
        int resId = c.getResources().getIdentifier(frequency, "string", packageName);
        return broadcastDay+", "+broadcastTime+", "+c.getResources().getString(resId);

    }
    */

    public String getFormattedStartTime()
    {
        return getFormattedTime(startTime);
    }

    public String getFormattedEndTime()
    {
        return getFormattedTime(endTime);
    }

    private String getFormattedTime(String time)
    {
        if(time == null)
        {
            return "";
        }

        return time;
    }

}
