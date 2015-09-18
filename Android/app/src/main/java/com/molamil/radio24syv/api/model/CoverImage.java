
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class CoverImage {

    @Expose
    private String wideUrl;
    @Expose
    private String mobileUrl;
    @Expose
    private Object photographer;

    /**
     * 
     * @return
     *     The wideUrl
     */
    public String getWideUrl() {
        return wideUrl;
    }

    /**
     * 
     * @param wideUrl
     *     The wideUrl
     */
    public void setWideUrl(String wideUrl) {
        this.wideUrl = wideUrl;
    }

    /**
     * 
     * @return
     *     The mobileUrl
     */
    public String getMobileUrl() {
        return mobileUrl;
    }

    /**
     * 
     * @param mobileUrl
     *     The mobileUrl
     */
    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    /**
     * 
     * @return
     *     The photographer
     */
    public Object getPhotographer() {
        return photographer;
    }

    /**
     * 
     * @param photographer
     *     The photographer
     */
    public void setPhotographer(Object photographer) {
        this.photographer = photographer;
    }

}
