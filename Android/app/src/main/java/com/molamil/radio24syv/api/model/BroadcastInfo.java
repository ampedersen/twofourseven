
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class BroadcastInfo {

    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("weekly")
    @Expose
    private Boolean weekly;

    /**
     * 
     * @return
     *     The day
     */
    public String getDay() {
        return day;
    }

    /**
     * 
     * @param day
     *     The day
     */
    public void setDay(String day) {
        this.day = day;
    }

    /**
     * 
     * @return
     *     The time
     */
    public String getTime() {
        return time;
    }

    /**
     * 
     * @param time
     *     The time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 
     * @return
     *     The weekly
     */
    public Boolean getWeekly() {
        return weekly;
    }

    /**
     * 
     * @param weekly
     *     The weekly
     */
    public void setWeekly(Boolean weekly) {
        this.weekly = weekly;
    }

}
