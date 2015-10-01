package com.molamil.radio24syv.storage.model;

import android.content.Intent;
import android.database.Cursor;

import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.ConciseProgram;
import com.molamil.radio24syv.api.model.Program;
import com.molamil.radio24syv.api.model.RelatedProgram;
import com.molamil.radio24syv.storage.Storage;

import java.io.Serializable;

/**
 * Information about a ConciseProgram or Program from the API containing only the stuff needed.
 * Dependency on the API is handled through this class. And it is also lighter to pass as serializable.
 * Created by jens on 24/09/15.
 */
public class ProgramInfo implements Serializable {
    private int programId;
    private String name;
    private String topic;
    private String description;
    private String imageUrl;
    private boolean active;

    public ProgramInfo() {}

    public ProgramInfo(ConciseProgram conciseProgram) {
        programId = RestClient.getIntegerSafely(conciseProgram.getVideoProgramId(), Storage.PROGRAM_ID_UNKNOWN);
        name = conciseProgram.getName();
        topic = conciseProgram.getTopic();
        description = conciseProgram.getDescriptionText();
        imageUrl = conciseProgram.getImageUrl();
        active = conciseProgram.getActive();
    }

    public ProgramInfo(Program program) {
        programId = RestClient.getIntegerSafely(program.getVideoProgramId(), Storage.PROGRAM_ID_UNKNOWN);
        name = program.getName();
        topic = program.getTopic();
        description = RestClient.getTextWithoutHtmlTags(program.getDescriptionHtml());
        imageUrl = program.getImageUrl();
        active = program.getActive();
    }

    // TODO this is broken until the API returns an integer instead of null for relatedProgram.getVideoProgramId()
//    public ProgramInfo(RelatedProgram relatedProgram) {
//        programId = RestClient.getIntegerSafely((int) relatedProgram.getVideoProgramId(), Storage.PROGRAM_ID_UNKNOWN); // I suppose this is an integer
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

}
