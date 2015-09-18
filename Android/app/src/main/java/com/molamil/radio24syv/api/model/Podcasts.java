
package com.molamil.radio24syv.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Podcasts {

    @Expose
    private Integer videoPodcastId;
    @Expose
    private String slug;
    @Expose
    private String title;
    @Expose
    private String oauthToken;
    @Expose
    private Boolean ltn;
    @Expose
    private Description description;
    @Expose
    private ProgramInfo programInfo;
    @Expose
    private PublishInfo publishInfo;
    @Expose
    private AudioInfo audioInfo;
    @Expose
    private List<String> tags = new ArrayList<String>();

    /**
     * 
     * @return
     *     The videoPodcastId
     */
    public Integer getVideoPodcastId() {
        return videoPodcastId;
    }

    /**
     * 
     * @param videoPodcastId
     *     The videoPodcastId
     */
    public void setVideoPodcastId(Integer videoPodcastId) {
        this.videoPodcastId = videoPodcastId;
    }

    /**
     * 
     * @return
     *     The slug
     */
    public String getSlug() {
        return slug;
    }

    /**
     * 
     * @param slug
     *     The slug
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * 
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The oauthToken
     */
    public String getOauthToken() {
        return oauthToken;
    }

    /**
     * 
     * @param oauthToken
     *     The oauthToken
     */
    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    /**
     * 
     * @return
     *     The ltn
     */
    public Boolean getLtn() {
        return ltn;
    }

    /**
     * 
     * @param ltn
     *     The ltn
     */
    public void setLtn(Boolean ltn) {
        this.ltn = ltn;
    }

    /**
     * 
     * @return
     *     The description
     */
    public Description getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    public void setDescription(Description description) {
        this.description = description;
    }

    /**
     * 
     * @return
     *     The programInfo
     */
    public ProgramInfo getProgramInfo() {
        return programInfo;
    }

    /**
     * 
     * @param programInfo
     *     The programInfo
     */
    public void setProgramInfo(ProgramInfo programInfo) {
        this.programInfo = programInfo;
    }

    /**
     * 
     * @return
     *     The publishInfo
     */
    public PublishInfo getPublishInfo() {
        return publishInfo;
    }

    /**
     * 
     * @param publishInfo
     *     The publishInfo
     */
    public void setPublishInfo(PublishInfo publishInfo) {
        this.publishInfo = publishInfo;
    }

    /**
     * 
     * @return
     *     The audioInfo
     */
    public AudioInfo getAudioInfo() {
        return audioInfo;
    }

    /**
     * 
     * @param audioInfo
     *     The audioInfo
     */
    public void setAudioInfo(AudioInfo audioInfo) {
        this.audioInfo = audioInfo;
    }

    /**
     * 
     * @return
     *     The tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * 
     * @param tags
     *     The tags
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

}
