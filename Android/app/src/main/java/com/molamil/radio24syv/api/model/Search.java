
package com.molamil.radio24syv.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Search {

    @SerializedName("q")
    @Expose
    private String q;
    @SerializedName("resultsFound")
    @Expose
    private Integer resultsFound;
    @SerializedName("resultsLoaded")
    @Expose
    private Integer resultsLoaded;
    @SerializedName("offset")
    @Expose
    private Integer offset;
    @SerializedName("results")
    @Expose
    private List<Result> results = new ArrayList<Result>();

    /**
     * 
     * @return
     *     The q
     */
    public String getQ() {
        return q;
    }

    /**
     * 
     * @param q
     *     The q
     */
    public void setQ(String q) {
        this.q = q;
    }

    /**
     * 
     * @return
     *     The resultsFound
     */
    public Integer getResultsFound() {
        return resultsFound;
    }

    /**
     * 
     * @param resultsFound
     *     The resultsFound
     */
    public void setResultsFound(Integer resultsFound) {
        this.resultsFound = resultsFound;
    }

    /**
     * 
     * @return
     *     The resultsLoaded
     */
    public Integer getResultsLoaded() {
        return resultsLoaded;
    }

    /**
     * 
     * @param resultsLoaded
     *     The resultsLoaded
     */
    public void setResultsLoaded(Integer resultsLoaded) {
        this.resultsLoaded = resultsLoaded;
    }

    /**
     * 
     * @return
     *     The offset
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * 
     * @param offset
     *     The offset
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * 
     * @return
     *     The results
     */
    public List<Result> getResults() {
        return results;
    }

    /**
     * 
     * @param results
     *     The results
     */
    public void setResults(List<Result> results) {
        this.results = results;
    }

}
