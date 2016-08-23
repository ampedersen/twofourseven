package com.molamil.radio24syv.storage.model;

import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.Podcast;

import java.io.Serializable;

/**
 * Information about a Podcast from the API containing only the stuff needed.
 * Dependency on the API is handled through this class. And it is also lighter to pass as serializable.
 * Created by jens on 24/09/15.
 */
public class PodcastInfo implements Serializable {
    private int podcastId;
    private int programId;
    private String title;
    private String description;
    private String date;
    private String audioUrl;
    private String rating;


   // private boolean isRated;

    public PodcastInfo() {
    }

    public PodcastInfo(Podcast podcast) {
        podcastId = podcast.getVideoPodcastId();
        programId = podcast.getProgramInfo().getId();
        title = podcast.getTitle();
        setDescriptionHtml(podcast.getDescription().getHtml());
        date = podcast.getPublishInfo().getCreatedAt();
        audioUrl = podcast.getAudioInfo().getUrl();
        rating = podcast.getRating();
    }

    public Float getRatingFloat() {
        try {
            Float result = Float.parseFloat(getRating());
            return result;
        } catch (NumberFormatException exception) {}
        return 0.0f;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getPodcastId() {
        return podcastId;
    }

    public void setPodcastId(int podcastId) {
        this.podcastId = podcastId;
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescriptionText(String text) {
        this.description = text;
    }

    public void setDescriptionHtml(String html) {
        this.description = RestClient.getTextWithoutHtmlTags(html);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

}
