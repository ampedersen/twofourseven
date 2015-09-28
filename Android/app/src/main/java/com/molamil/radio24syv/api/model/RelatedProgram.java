
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class RelatedProgram {

    @SerializedName("videoProgramId")
    @Expose
    private Object videoProgramId;
    @SerializedName("descriptionText")
    @Expose
    private String descriptionText;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("active")
    @Expose
    private Boolean active;

    /**
     * 
     * @return
     *     The videoProgramId
     */
    public Object getVideoProgramId() {
        return videoProgramId;
    }

    /**
     * 
     * @param videoProgramId
     *     The videoProgramId
     */
    public void setVideoProgramId(Object videoProgramId) {
        this.videoProgramId = videoProgramId;
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

}
