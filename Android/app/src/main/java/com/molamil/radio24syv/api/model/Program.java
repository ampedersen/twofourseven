
package com.molamil.radio24syv.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Program {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("intro")
    @Expose
    private String intro;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("duration")
    @Expose
    private Object duration;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("descriptionHtml")
    @Expose
    private String descriptionHtml;
    @SerializedName("videoProgramId")
    @Expose
    private Integer videoProgramId;
    @SerializedName("coverColor")
    @Expose
    private String coverColor;
    @SerializedName("podcastTree")
    @Expose
    private String podcastTree;
    @SerializedName("broadcastInfo")
    @Expose
    private BroadcastInfo broadcastInfo;
    @SerializedName("coverImages")
    @Expose
    private List<CoverImage> coverImages = new ArrayList<CoverImage>();
    @SerializedName("hosts")
    @Expose
    private List<Host> hosts = new ArrayList<Host>();
    @SerializedName("metaInfo")
    @Expose
    private MetaInfo metaInfo;
    @SerializedName("rebroadcastInfo")
    @Expose
    private RebroadcastInfo rebroadcastInfo;
    @SerializedName("subpages")
    @Expose
    private List<Subpage> subpages = new ArrayList<Subpage>();
    @SerializedName("socialInfo")
    @Expose
    private SocialInfo socialInfo;
    @SerializedName("relatedPrograms")
    @Expose
    private List<RelatedProgram> relatedPrograms = new ArrayList<RelatedProgram>();
    @SerializedName("sponsorInfo")
    @Expose
    private SponsorInfo sponsorInfo;

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
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
     *     The intro
     */
    public String getIntro() {
        return intro;
    }

    /**
     * 
     * @param intro
     *     The intro
     */
    public void setIntro(String intro) {
        this.intro = intro;
    }

    /**
     * 
     * @return
     *     The active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * 
     * @param active
     *     The active
     */
    public void setActive(Boolean active) {
        this.active = active;
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
     *     The duration
     */
    public Object getDuration() {
        return duration;
    }

    /**
     * 
     * @param duration
     *     The duration
     */
    public void setDuration(Object duration) {
        this.duration = duration;
    }

    /**
     * 
     * @return
     *     The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     *     The createdAt
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 
     * @return
     *     The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 
     * @param updatedAt
     *     The updatedAt
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 
     * @return
     *     The descriptionHtml
     */
    public String getDescriptionHtml() {
        return descriptionHtml;
    }

    /**
     * 
     * @param descriptionHtml
     *     The descriptionHtml
     */
    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

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
     *     The podcastTree
     */
    public String getPodcastTree() {
        return podcastTree;
    }

    /**
     * 
     * @param podcastTree
     *     The podcastTree
     */
    public void setPodcastTree(String podcastTree) {
        this.podcastTree = podcastTree;
    }

    /**
     * 
     * @return
     *     The broadcastInfo
     */
    public BroadcastInfo getBroadcastInfo() {
        return broadcastInfo;
    }

    /**
     * 
     * @param broadcastInfo
     *     The broadcastInfo
     */
    public void setBroadcastInfo(BroadcastInfo broadcastInfo) {
        this.broadcastInfo = broadcastInfo;
    }

    /**
     * 
     * @return
     *     The coverImages
     */
    public List<CoverImage> getCoverImages() {
        return coverImages;
    }

    /**
     * 
     * @param coverImages
     *     The coverImages
     */
    public void setCoverImages(List<CoverImage> coverImages) {
        this.coverImages = coverImages;
    }

    /**
     * 
     * @return
     *     The hosts
     */
    public List<Host> getHosts() {
        return hosts;
    }

    /**
     * 
     * @param hosts
     *     The hosts
     */
    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }

    /**
     * 
     * @return
     *     The metaInfo
     */
    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

    /**
     * 
     * @param metaInfo
     *     The metaInfo
     */
    public void setMetaInfo(MetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

    /**
     * 
     * @return
     *     The rebroadcastInfo
     */
    public RebroadcastInfo getRebroadcastInfo() {
        return rebroadcastInfo;
    }

    /**
     * 
     * @param rebroadcastInfo
     *     The rebroadcastInfo
     */
    public void setRebroadcastInfo(RebroadcastInfo rebroadcastInfo) {
        this.rebroadcastInfo = rebroadcastInfo;
    }

    /**
     * 
     * @return
     *     The subpages
     */
    public List<Subpage> getSubpages() {
        return subpages;
    }

    /**
     * 
     * @param subpages
     *     The subpages
     */
    public void setSubpages(List<Subpage> subpages) {
        this.subpages = subpages;
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
     *     The relatedPrograms
     */
    public List<RelatedProgram> getRelatedPrograms() {
        return relatedPrograms;
    }

    /**
     * 
     * @param relatedPrograms
     *     The relatedPrograms
     */
    public void setRelatedPrograms(List<RelatedProgram> relatedPrograms) {
        this.relatedPrograms = relatedPrograms;
    }

    /**
     * 
     * @return
     *     The sponsorInfo
     */
    public SponsorInfo getSponsorInfo() {
        return sponsorInfo;
    }

    /**
     * 
     * @param sponsorInfo
     *     The sponsorInfo
     */
    public void setSponsorInfo(SponsorInfo sponsorInfo) {
        this.sponsorInfo = sponsorInfo;
    }

}
