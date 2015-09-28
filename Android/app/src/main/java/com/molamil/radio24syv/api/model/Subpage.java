
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Subpage {

    @SerializedName("custom")
    @Expose
    private Object custom;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("descriptionHtml")
    @Expose
    private String descriptionHtml;
    @SerializedName("imageUrl")
    @Expose
    private Object imageUrl;
    @SerializedName("intro")
    @Expose
    private String intro;
    @SerializedName("metaInfo")
    @Expose
    private MetaInfo metaInfo;

    /**
     * 
     * @return
     *     The custom
     */
    public Object getCustom() {
        return custom;
    }

    /**
     * 
     * @param custom
     *     The custom
     */
    public void setCustom(Object custom) {
        this.custom = custom;
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
     *     The imageUrl
     */
    public Object getImageUrl() {
        return imageUrl;
    }

    /**
     * 
     * @param imageUrl
     *     The imageUrl
     */
    public void setImageUrl(Object imageUrl) {
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

}
