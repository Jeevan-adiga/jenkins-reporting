package com.jenkins.dto.report;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Job {
    private static Logger log = Logger.getLogger(Job.class);

    @JsonProperty("name")
    private String name;

    @JsonProperty("url")
    private String url;

    @JsonProperty("lastBuild")
    private BuildListing lastBuild;

//    @JsonProperty("builds")
//    private List<BuildListing> builds = new ArrayList<BuildListing>();


    public BuildListing getLastBuild() {

        if(lastBuild == null) {
            log.info("No builds for job: "+this.name);
        }

        return lastBuild;
    }

    public void setLastBuild(BuildListing lastBuild) {
        this.lastBuild = lastBuild;
    }

    public String getUrl() {
        return url;
    }

//    public List<BuildListing> getBuilds() {
//        return builds;
//    }
//
//    public void setBuilds(List<BuildListing> builds) {
//        this.builds = builds;
//    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
