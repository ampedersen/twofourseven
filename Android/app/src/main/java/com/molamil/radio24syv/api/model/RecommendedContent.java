
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class RecommendedContent {

    @Expose
    private Boolean active;
    @Expose
    private String link;
    @Expose
    private Object imageUrl;
    @Expose
    private String tooltip;
    @Expose
    private Boolean external;

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
     *     The link
     */
    public String getLink() {
        return link;
    }

    /**
     * 
     * @param link
     *     The link
     */
    public void setLink(String link) {
        this.link = link;
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
     *     The tooltip
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * 
     * @param tooltip
     *     The tooltip
     */
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * 
     * @return
     *     The external
     */
    public Boolean getExternal() {
        return external;
    }

    /**
     * 
     * @param external
     *     The external
     */
    public void setExternal(Boolean external) {
        this.external = external;
    }

}
