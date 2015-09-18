
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Breaking {

    @Expose
    private Boolean active;
    @Expose
    private String content;
    @Expose
    private String headline;
    @Expose
    private String subheadline;

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
     *     The content
     */
    public String getContent() {
        return content;
    }

    /**
     * 
     * @param content
     *     The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 
     * @return
     *     The headline
     */
    public String getHeadline() {
        return headline;
    }

    /**
     * 
     * @param headline
     *     The headline
     */
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    /**
     * 
     * @return
     *     The subheadline
     */
    public String getSubheadline() {
        return subheadline;
    }

    /**
     * 
     * @param subheadline
     *     The subheadline
     */
    public void setSubheadline(String subheadline) {
        this.subheadline = subheadline;
    }

}
