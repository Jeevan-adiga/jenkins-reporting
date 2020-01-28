package com.jenkins.dto.report;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class View {

    @JsonProperty("jobs")
    private List<JobListing> jobs = new ArrayList<JobListing>();

    public List<JobListing> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobListing> jobListings) {
        this.jobs = jobListings;
    }
}
