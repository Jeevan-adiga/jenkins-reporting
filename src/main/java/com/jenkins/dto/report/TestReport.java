package com.jenkins.dto.report;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.jenkins.dto.report.issues.ChildReport;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestReport {

    @JsonProperty("childReports")
    private List<ChildReport> childReports = new ArrayList<ChildReport>();

    @JsonProperty("failCount")
    private String failCount;

    @JsonProperty("skipCount")
    private String skipCount;

    @JsonProperty("totalCount")
    private String totalCount;

    private String url;

    public List<ChildReport> getChildReports() {
        return childReports;
    }

    public void setCases(List<ChildReport> childReports) {
        this.childReports = childReports;
    }

    public int getPassCount() {
        int fail = Integer.valueOf(this.failCount);
        int skip = Integer.valueOf(this.skipCount);
        int total = Integer.valueOf(this.totalCount);
        int pass = total - skip - fail;
        return pass;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getFailCount() {
        return Integer.valueOf(failCount);
    }

    public void setFailCount(String failCount) {
        this.failCount = failCount;
    }

    public int getSkipCount() {
        return Integer.valueOf(skipCount);
    }

    public void setSkipCount(String skipCount) {
        this.skipCount = skipCount;
    }

    public int getTotalCount() {
        return Integer.valueOf(totalCount);
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }




}
