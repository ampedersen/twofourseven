
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class RebroadcastInfo {

    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("time")
    @Expose
    private Object time;

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
    public Object getTime() {
        return time;
    }

    /**
     * 
     * @param time
     *     The time
     */
    public void setTime(Object time) {
        this.time = time;
    }

}
