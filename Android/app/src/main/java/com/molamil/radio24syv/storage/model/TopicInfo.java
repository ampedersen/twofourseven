package com.molamil.radio24syv.storage.model;

import android.graphics.Color;

import java.io.Serializable;

/**
 * Created by jens on 28/09/15.
 */
public class TopicInfo implements Serializable {
    private String topicId;
    private String color;
    private int colorValue;
    private String topicText;

    public TopicInfo() {}

    public TopicInfo(String topicId, String color) {
        setTopicId(topicId);
        setColor(color);
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
        if (topicId.length() > 0) {
            topicText = topicId.substring(0, 1).toUpperCase() + topicId.substring(1).toLowerCase(); // Uppercase first letter
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        colorValue = Color.parseColor(color);
    }

    public int getColorValue() {
        return colorValue;
    }

    public String getTopicText() {
        return topicText;
    }
}
