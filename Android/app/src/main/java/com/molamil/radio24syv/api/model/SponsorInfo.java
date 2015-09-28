
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class SponsorInfo {

    @SerializedName("imageUrl")
    @Expose
    private Object imageUrl;
    @SerializedName("url")
    @Expose
    private Object url;

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
     *     The url
     */
    public Object getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    public void setUrl(Object url) {
        this.url = url;
    }

}
