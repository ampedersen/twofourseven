
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Host {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("intro")
    @Expose
    private String intro;
    @SerializedName("topic")
    @Expose
    private Object topic;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("descriptionHtml")
    @Expose
    private String descriptionHtml;

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
     *     The topic
     */
    public Object getTopic() {
        return topic;
    }

    /**
     * 
     * @param topic
     *     The topic
     */
    public void setTopic(Object topic) {
        this.topic = topic;
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

}
