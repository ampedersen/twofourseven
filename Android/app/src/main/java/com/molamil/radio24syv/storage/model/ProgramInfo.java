package com.molamil.radio24syv.storage.model;

import android.database.Cursor;

import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.ConciseProgram;
import com.molamil.radio24syv.api.model.Program;

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
        programId = RestClient.getIntegerSafely(conciseProgram.getVideoProgramId(), 0);
        name = conciseProgram.getName();
        topic = conciseProgram.getTopic();
        description = conciseProgram.getDescriptionText();
        imageUrl = conciseProgram.getImageUrl();
        active = conciseProgram.getActive();
    }

    public ProgramInfo(Program program) {
        programId = RestClient.getIntegerSafely(program.getVideoProgramId(), 0);
        name = program.getName();
        topic = program.getTopic();
        description = getTextWithoutHtmlTags(program.getDescriptionHtml());
        imageUrl = program.getImageUrl();
        active = program.getActive();
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

    private static String getTextWithoutHtmlTags(String html) {
        html = html.replace("&amp;", "&"); // Ampersand instead of html code
        html = html.replace("<p", "\n<p"); // Line break before <p>
        html = html.trim();
        StringBuilder builder = new StringBuilder();
        int textStart = 0;
        do {
            int textEnd = html.indexOf("<", textStart);
            if (textEnd < 0) {
                textEnd = html.length() - 1;
            }
            if (textEnd > textStart) {
                builder.append(html.substring(textStart, textEnd)); // Second parameter is the end index, NOT the number of characters to copy
            }
            textStart = html.indexOf(">", textEnd) + 1;
        } while ((textStart > 0) && (textStart < html.length()));
        return builder.toString();
    }
}