
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class AudioInfo {

    @Expose
    private Boolean exists;
    @Expose
    private Integer duration;
    @Expose
    private Integer fileSize;
    @Expose
    private String url;

    /**
     * 
     * @return
     *     The exists
     */
    public Boolean getExists() {
        return exists;
    }

    /**
     * 
     * @param exists
     *     The exists
     */
    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    /**
     * 
     * @return
     *     The duration
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * 
     * @param duration
     *     The duration
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * 
     * @return
     *     The fileSize
     */
    public Integer getFileSize() {
        return fileSize;
    }

    /**
     * 
     * @param fileSize
     *     The fileSize
     */
    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * 
     * @return
     *     The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
