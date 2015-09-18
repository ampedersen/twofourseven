
package com.molamil.radio24syv.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Pages {

    @Expose
    private String name;
    @Expose
    private Breaking breaking;
    @Expose
    private List<RecommendedProgram> recommendedPrograms = new ArrayList<RecommendedProgram>();
    @Expose
    private List<RecommendedContent> recommendedContent = new ArrayList<RecommendedContent>();
    @Expose
    private MetaInfo metaInfo;

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The breaking
     */
    public Breaking getBreaking() {
        return breaking;
    }

    /**
     * 
     * @param breaking
     *     The breaking
     */
    public void setBreaking(Breaking breaking) {
        this.breaking = breaking;
    }

    /**
     * 
     * @return
     *     The recommendedPrograms
     */
    public List<RecommendedProgram> getRecommendedPrograms() {
        return recommendedPrograms;
    }

    /**
     * 
     * @param recommendedPrograms
     *     The recommendedPrograms
     */
    public void setRecommendedPrograms(List<RecommendedProgram> recommendedPrograms) {
        this.recommendedPrograms = recommendedPrograms;
    }

    /**
     * 
     * @return
     *     The recommendedContent
     */
    public List<RecommendedContent> getRecommendedContent() {
        return recommendedContent;
    }

    /**
     * 
     * @param recommendedContent
     *     The recommendedContent
     */
    public void setRecommendedContent(List<RecommendedContent> recommendedContent) {
        this.recommendedContent = recommendedContent;
    }

    /**
     * 
     * @return
     *     The metaInfo
     */
    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

    /**
     * 
     * @param metaInfo
     *     The metaInfo
     */
    public void setMetaInfo(MetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

}
