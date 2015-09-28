
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class SocialInfo {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("facebook")
    @Expose
    private String facebook;
    @SerializedName("twitter")
    @Expose
    private Object twitter;
    @SerializedName("wikipedia")
    @Expose
    private Object wikipedia;

    /**
     * 
     * @return
     *     The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * 
     * @param email
     *     The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 
     * @return
     *     The facebook
     */
    public String getFacebook() {
        return facebook;
    }

    /**
     * 
     * @param facebook
     *     The facebook
     */
    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    /**
     * 
     * @return
     *     The twitter
     */
    public Object getTwitter() {
        return twitter;
    }

    /**
     * 
     * @param twitter
     *     The twitter
     */
    public void setTwitter(Object twitter) {
        this.twitter = twitter;
    }

    /**
     * 
     * @return
     *     The wikipedia
     */
    public Object getWikipedia() {
        return wikipedia;
    }

    /**
     * 
     * @param wikipedia
     *     The wikipedia
     */
    public void setWikipedia(Object wikipedia) {
        this.wikipedia = wikipedia;
    }

}
