
package com.molamil.radio24syv.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Broadcast {

    @Expose
    private Integer videoProgramId;
    @Expose
    private List<Object> videoPodcastIds = new ArrayList<Object>();
    @Expose
    private List<Integer> daletBroadcastIds = new ArrayList<Integer>();
    @Expose
    private List<Object> podioIds = new ArrayList<Object>();
    @Expose
    private String descriptionText;
    @Expose
    private String hosts;
    @Expose
    private String programName;
    @Expose
    private String programSlug;
    @Expose
    private SocialInfo socialInfo;
    @Expose
    private String topic;
    @Expose
    private String coverColor;
    @Expose
    private CoverImage coverImage;
    @Expose
    private String imageUrl;
    @Expose
    private BroadcastTime broadcastTime;
    @Expose
    private AppImages appImages;

    /**
     * 
     * @return
     *     The videoProgramId
     */
    public Integer getVideoProgramId() {
        return videoProgramId;
    }

    /**
     * 
     * @param videoProgramId
     *     The videoProgramId
     */
    public void setVideoProgramId(Integer videoProgramId) {
        this.videoProgramId = videoProgramId;
    }

    /**
     * 
     * @return
     *     The videoPodcastIds
     */
    public List<Object> getVideoPodcastIds() {
        return videoPodcastIds;
    }

    /**
     * 
     * @param videoPodcastIds
     *     The videoPodcastIds
     */
    public void setVideoPodcastIds(List<Object> videoPodcastIds) {
        this.videoPodcastIds = videoPodcastIds;
    }

    /**
     * 
     * @return
     *     The daletBroadcastIds
     */
    public List<Integer> getDaletBroadcastIds() {
        return daletBroadcastIds;
    }

    /**
     * 
     * @param daletBroadcastIds
     *     The daletBroadcastIds
     */
    public void setDaletBroadcastIds(List<Integer> daletBroadcastIds) {
        this.daletBroadcastIds = daletBroadcastIds;
    }

    /**
     * 
     * @return
     *     The podioIds
     */
    public List<Object> getPodioIds() {
        return podioIds;
    }

    /**
     * 
     * @param podioIds
     *     The podioIds
     */
    public void setPodioIds(List<Object> podioIds) {
        this.podioIds = podioIds;
    }

    /**
     * 
     * @return
     *     The descriptionText
     */
    public String getDescriptionText() {
        return descriptionText;
    }

    /**
     * 
     * @param descriptionText
     *     The descriptionText
     */
    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    /**
     * 
     * @return
     *     The hosts
     */
    public String getHosts() {
        return hosts;
    }

    /**
     * 
     * @param hosts
     *     The hosts
     */
    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    /**
     * 
     * @return
     *     The programName
     */
    public String getProgramName() {
        return programName;
    }

    /**
     * 
     * @param programName
     *     The programName
     */
    public void setProgramName(String programName) {
        this.programName = programName;
    }

    /**
     * 
     * @return
     *     The programSlug
     */
    public String getProgramSlug() {
        return programSlug;
    }

    /**
     * 
     * @param programSlug
     *     The programSlug
     */
    public void setProgramSlug(String programSlug) {
        this.programSlug = programSlug;
    }

    /**
     * 
     * @return
     *     The socialInfo
     */
    public SocialInfo getSocialInfo() {
        return socialInfo;
    }

    /**
     * 
     * @param socialInfo
     *     The socialInfo
     */
    public void setSocialInfo(SocialInfo socialInfo) {
        this.socialInfo = socialInfo;
    }

    /**
     * 
     * @return
     *     The topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * 
     * @param topic
     *     The topic
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * 
     * @return
     *     The coverColor
     */
    public String getCoverColor() {
        return coverColor;
    }

    /**
     * 
     * @param coverColor
     *     The coverColor
     */
    public void setCoverColor(String coverColor) {
        this.coverColor = coverColor;
    }

    /**
     * 
     * @return
     *     The coverImage
     */
    public CoverImage getCoverImage() {
        return coverImage;
    }

    /**
     * 
     * @param coverImage
     *     The coverImage
     */
    public void setCoverImage(CoverImage coverImage) {
        this.coverImage = coverImage;
    }

    /**
     * 
     * @return
     *     The imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 
     * @param imageUrl
     *     The imageUrl
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * 
     * @return
     *     The broadcastTime
     */
    public BroadcastTime getBroadcastTime() {
        return broadcastTime;
    }

    /**
     * 
     * @param broadcastTime
     *     The broadcastTime
     */
    public void setBroadcastTime(BroadcastTime broadcastTime) {
        this.broadcastTime = broadcastTime;
    }
    /**
     *
     * @return
     *     The appImages
     */
    public AppImages getAppImages() {
        return appImages;
    }

    /**
     *
     * @param appImages
     *     The appImages
     */
    public void setAppImages(AppImages appImages) {
        this.appImages = appImages;
    }

}
