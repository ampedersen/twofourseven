
package com.molamil.radio24syv.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Searches {

    @Expose
    private String q;
    @Expose
    private Integer resultsFound;
    @Expose
    private Integer resultsLoaded;
    @Expose
    private Integer offset;
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
