package com.molamil.radio24syv.settings.model;

import com.molamil.radio24syv.api.model.Program;

import java.io.Serializable;

/**
 * Information about a Program from the API containing only the stuff needed.
 * Dependency on the API is handled through this class. And it is also lighter to pass as serializable.
 * Created by jens on 24/09/15.
 */
public class ProgramInfo implements Serializable {
    private int programId;
    private String name;
    private String topic;
    private String description;
    private String imageUrl;

    public ProgramInfo() {}

    public ProgramInfo(Program program) {
        programId = program.getVideoProgramId();
        name = program.getName();
        topic = program.getTopic();
        description = program.getDescriptionText();
        imageUrl = program.getImageUrl();
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

    public String getTopic() {
        return topic;
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
}