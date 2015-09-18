
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class PublishInfo {

    @Expose
    private Boolean published;
    @Expose
    private Boolean sectioned;
    @Expose
    private String createdAt;

    /**
     * 
     * @return
     *     The published
     */
    public Boolean getPublished() {
        return published;
    }

    /**
     * 
     * @param published
     *     The published
     */
    public void setPublished(Boolean published) {
        this.published = published;
    }

    /**
     * 
     * @return
     *     The sectioned
     */
    public Boolean getSectioned() {
        return sectioned;
    }

    /**
     * 
     * @param sectioned
     *     The sectioned
     */
    public void setSectioned(Boolean sectioned) {
        this.sectioned = sectioned;
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

}
