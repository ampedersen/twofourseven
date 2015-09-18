
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class SocialInfo {

    @Expose
    private String email;
    @Expose
    private String facebook;
    @Expose
    private String twitter;
    @Expose
    private Boolean phoneIn;
    @Expose
    private Boolean textIn;

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
    public String getTwitter() {
        return twitter;
    }

    /**
     * 
     * @param twitter
     *     The twitter
     */
    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    /**
     * 
     * @return
     *     The phoneIn
     */
    public Boolean getPhoneIn() {
        return phoneIn;
    }

    /**
     * 
     * @param phoneIn
     *     The phoneIn
     */
    public void setPhoneIn(Boolean phoneIn) {
        this.phoneIn = phoneIn;
    }

    /**
     * 
     * @return
     *     The textIn
     */
    public Boolean getTextIn() {
        return textIn;
    }

    /**
     * 
     * @param textIn
     *     The textIn
     */
    public void setTextIn(Boolean textIn) {
        this.textIn = textIn;
    }

}
