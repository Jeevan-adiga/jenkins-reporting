package com.jenkins.integration.report;

import java.util.ArrayList;
import java.util.List;

import com.jenkins.dto.report.Job;
import com.jenkins.dto.report.TestReport;
import org.apache.log4j.Logger;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.jenkins.dto.report.Build;
import com.jenkins.dto.report.View;

public class JenkinsIntegration {

    private static Logger log = Logger.getLogger(JenkinsIntegration.class);

    private String jenkinsURL;

    public final static String API_JSON_URI = "/api/json?pretty=true";
    public final static String VIEW_URI = "view/";
    public final static String JOB_URI = "job/";
    public final static String TEST_REPORT_URI = "/testReport/";

    public final static String URI_SLASH = "/";

    public View getView(String viewName) {
        String completeURL = jenkinsURL+VIEW_URI+viewName+API_JSON_URI;

        final RestTemplate restTemplate = getRestTemplate();
        View view = restTemplate.getForObject(completeURL, View.class);

        return view;
    }

    public Job getJob(String jobName) {
        String completeURL = jenkinsURL+JOB_URI+jobName+API_JSON_URI;

        final RestTemplate restTemplate = getRestTemplate();
        Job job = restTemplate.getForObject(completeURL, Job.class);

        return job;
    }

    public Build getBuild(Job job, String buildNumber) {
        String completeURL = jenkinsURL+JOB_URI+job.getName()+URI_SLASH+buildNumber+API_JSON_URI;

        final RestTemplate restTemplate = getRestTemplate();
        Build build = restTemplate.getForObject(completeURL, Build.class);

        return build;
    }

    public TestReport getTestReport(Job job, String buildNumber) {
        String baseUrl = jenkinsURL+JOB_URI+job.getName()+URI_SLASH+buildNumber+TEST_REPORT_URI;
        log.info("Base Url: "+baseUrl);

        String completeURL = baseUrl+API_JSON_URI;

        final RestTemplate restTemplate = getRestTemplate();
        TestReport testReport=null;
        try {
            testReport = restTemplate.getForObject(completeURL, TestReport.class);
            testReport.setUrl(baseUrl);
        } catch (HttpClientErrorException e) {
            log.error("Unable to get test results for job: "+job.getName()+" - build: "+buildNumber+" - url: "+completeURL);
            // do nothing for now. No reports for this build.
        }

        return testReport;
    }

    protected RestTemplate getRestTemplate() {
        final RestTemplate restTemplate = new RestTemplate();

        //Add the Jackson Message converter
        final List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJacksonHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);

        return restTemplate;

    }

    public void setJenkinsURL(String jenkinsURL) {
        this.jenkinsURL = jenkinsURL;
    }
}
