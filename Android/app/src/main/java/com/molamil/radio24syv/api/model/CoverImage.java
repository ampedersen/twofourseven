
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class CoverImage {

    @SerializedName("wideUrl")
    @Expose
    private String wideUrl;
    @SerializedName("mobileUrl")
    @Expose
    private String mobileUrl;

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

}
