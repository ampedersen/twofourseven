
package com.molamil.radio24syv.api.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class AppImages {

    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("player")
    @Expose
    private String player;
    @SerializedName("live")
    @Expose
    private String live;

    /**
     * 
     * @return
     *     The overview
     */
    public String getOverview() {
        return overview;
    }

    /**
     * 
     * @param overview
     *     The overview
     */
    public void setOverview(String overview) {
        this.overview = overview;
    }

    /**
     * 
     * @return
     *     The player
     */
    public String getPlayer() {
        return player;
    }

    /**
     * 
     * @param player
     *     The player
     */
    public void setPlayer(String player) {
        this.player = player;
    }

    /**
     *
     * @return
     *     The player
     */
    public String getLive() {
        return live;
    }

    /**
     *
     * @param player
     *     The player
     */
    public void setLive(String live) {
        this.live = live;
    }

}
