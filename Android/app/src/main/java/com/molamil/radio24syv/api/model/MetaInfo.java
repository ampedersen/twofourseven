
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class MetaInfo {

    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("keywords")
    @Expose
    private Object keywords;
    @SerializedName("title")
    @Expose
    private Object title;
    @SerializedName("redirect")
    @Expose
    private Object redirect;

    /**
     * 
     * @return
     *     The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * @return
     *     The keywords
     */
    public Object getKeywords() {
        return keywords;
    }

    /**
     * 
     * @param keywords
     *     The keywords
     */
    public void setKeywords(Object keywords) {
        this.keywords = keywords;
    }

    /**
     * 
     * @return
     *     The title
     */
    public Object getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(Object title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The redirect
     */
    public Object getRedirect() {
        return redirect;
    }

    /**
     * 
     * @param redirect
     *     The redirect
     */
    public void setRedirect(Object redirect) {
        this.redirect = redirect;
    }

}
