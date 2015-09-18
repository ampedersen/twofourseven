
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class MetaInfo {

    @Expose
    private Object description;
    @Expose
    private Object keywords;
    @Expose
    private Object title;

    /**
     * 
     * @return
     *     The description
     */
    public Object getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    public void setDescription(Object description) {
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

}
