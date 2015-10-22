package com.molamil.radio24syv.storage.model;

import android.content.Context;

import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.*;
import com.molamil.radio24syv.storage.Storage;

import java.io.Serializable;
import java.util.ArrayList;
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
    private String imageUrl;
    private boolean active;

    private List<Host> hosts = new ArrayList<Host>();
    private com.molamil.radio24syv.api.model.BroadcastInfo broadcastInfo;
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
        hosts = program.getHosts();
        broadcastInfo = program.getBroadcastInfo();
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
        if(hosts.size() == 0)
        {
            return "";
        }
        String result = "";
        String delimiter = hosts.size() == 2 ? " og " : ", ";

        //for host in hosts
        for(int i = 0 ; i < hosts.size() ; i++)
        {
            result += hosts.get(i).getName();//hosts[i].name!

            if(i < hosts.size()-1)
            {
                result += delimiter;
            }
        }

        return result;
    }

    private String getBroadcastTime(Context c)
    {
        String frequency = broadcastInfo.getWeekly() ? "weekly" : "daily";
        String packageName = c.getPackageName();
        int resId = c.getResources().getIdentifier(frequency, "string", packageName);

        return broadcastInfo.getDay()+", "+broadcastInfo.getTime()+", "+c.getResources().getString(resId);
    }

}
