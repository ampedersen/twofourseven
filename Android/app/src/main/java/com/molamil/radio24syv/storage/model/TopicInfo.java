package com.molamil.radio24syv.storage.model;

import android.graphics.Color;

import java.io.Serializable;

/**
 * Created by jens on 28/09/15.
 */
public class TopicInfo implements Serializable {
    private String topic;
    private Color color;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
