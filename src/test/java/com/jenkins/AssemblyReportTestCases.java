package com.jenkins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.jenkins.dto.report.BuildListing;
import com.jenkins.dto.report.Job;
import com.jenkins.dto.report.JobListing;
import com.jenkins.dto.report.TestReport;
import com.jenkins.integration.report.JenkinsIntegration;
import com.jenkins.manager.report.DetailedReportManagerImpl;
import com.jenkins.manager.report.ReportManagerImpl;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jenkins.dto.report.Build;
import com.jenkins.dto.report.View;
import com.jenkins.dto.report.issues.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/resources/reportContext.xml", "file:src/main/resources/sharedContext.xml"})
public class AssemblyReportTestCases {
    private static Logger log = Logger.getLogger(AssemblyReportTestCases.class);

    ReportManagerImpl reportManager;
    DetailedReportManagerImpl detailedReportManager;
    private JenkinsIntegration jenkinsIntegration;

    
//    @Test
    public void testProcess_fpp_simplification() throws Exception {
        String viewName = "All";
        String jobName = "FPP";

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(Launch.PARAM_JOB_NAME, jobName);
        paramMap.put(Launch.PARAM_VIEW_NAME, viewName);

        reportManager.process(paramMap);
        
    }
    
 //   @Test
    public void testProcess_xBMS() throws Exception {
        String viewName = "1. env2 (Latest)";
        String jobName = "xbms-web_ExpDev_Rel";

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(Launch.PARAM_JOB_NAME, jobName);
        paramMap.put(Launch.PARAM_VIEW_NAME, viewName);

        reportManager.process(paramMap);
        detailedReportManager.process(paramMap);
        
    }
    
 //   @Test
    public void testProcess_xBMSEnv5() throws Exception {
        String viewName = "3. env5 (Stage)";
        String jobName = "xbms-web_ExpDev_Rel";

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(Launch.PARAM_JOB_NAME, jobName);
        paramMap.put(Launch.PARAM_VIEW_NAME, viewName);

        reportManager.process(paramMap);
        
    }
    
    
 //   @Test
    public void testProcess() throws Exception {
        String viewName = "1. env2 (Latest)";
        String jobName = "c2_ExpDev_Rel-test-Latest";

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(Launch.PARAM_JOB_NAME, jobName);
        paramMap.put(Launch.PARAM_VIEW_NAME, viewName);

        reportManager.process(paramMap);
        detailedReportManager.process(paramMap);
        
    }

  //  @Test
    public void env5Report() throws Exception {
        String viewName = "3. env5 (Stage)";
        String jobName = "c2_ExpDev_Rel-test-Stage";

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(Launch.PARAM_JOB_NAME, jobName);
        paramMap.put(Launch.PARAM_VIEW_NAME, viewName);
        reportManager.process(paramMap);      
    }
    
    @Test
    public void env2Reportfpp() throws Exception {
	      String viewName = "1. env2 (Latest)";
	      String jobName = "fpp_ExpDev_Rel-test-Latest";
	
	      Map<String, String> paramMap = new HashMap<String, String>();
	      paramMap.put(Launch.PARAM_JOB_NAME, jobName);
	      paramMap.put(Launch.PARAM_VIEW_NAME, viewName);
	      reportManager.process(paramMap);
	      detailedReportManager.process(paramMap);
	  }
    
  // @Test
   public void env5Reportfpp() throws Exception {
	      String viewName = "3. env5 (Stage)";
	      String jobName = "fpp_ExpDev_Rel-test-Stage";
	
	      Map<String, String> paramMap = new HashMap<String, String>();
	      paramMap.put(Launch.PARAM_JOB_NAME, jobName);
	      paramMap.put(Launch.PARAM_VIEW_NAME, viewName);
	      reportManager.process(paramMap);      
	  }
    
//    @Test
    public void testApi() {

        try {

            String viewName = "03. Appium Mobile Tests";
            View view = jenkinsIntegration.getView(viewName);
            List<JobListing> jobListings = view.getJobs();

            JobListing mdxJobListing = null;
            for (JobListing jobListing : jobListings) {
                String name = jobListing.getName();
                log.info(name);
                if(name.equals("MDX IOS Appium Smoke Test")) {
                    mdxJobListing = jobListing;
                }
            }


            Job job = jenkinsIntegration.getJob(mdxJobListing.getName());
            BuildListing buildListing = job.getLastBuild();

            log.info("Job: "+mdxJobListing.getName()+" - last build: "+buildListing.getNumber());

            Build build = jenkinsIntegration.getBuild(job, buildListing.getNumber());
            log.info("result: "+build.getResult());

            TestReport testReport = jenkinsIntegration.getTestReport(job, buildListing.getNumber());
            if(testReport == null) {
//                throw new RuntimeException("No report for given build/job.");
                // could be the job is running at the moment.
            } else {
                log.info("Test Report: Total="+testReport.getTotalCount()+" - Passed="+testReport.getPassCount()+" - Failed="+testReport.getFailCount()+" - Skipped="+testReport.getSkipCount());

                List<TestCase> testCases = testReport.getChildReports().get(0).getResult().getSuites().get(0).getTestCases();
                for (TestCase caseInstance : testCases) {
                    log.info("TestCase name="+caseInstance.getName());
                }

            }

        } catch (final RuntimeException e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Resource
    public void setReportManagerImpl(final ReportManagerImpl reportManager) {
        this.reportManager = reportManager;
    }

    @Resource
    public void setDetailedReportManagerImpl(final DetailedReportManagerImpl detailedReportManager) {
        this.detailedReportManager = detailedReportManager;
    }
    
    @Resource
    public void setJenkinsIntegration(final JenkinsIntegration jenkinsIntegration) {
        this.jenkinsIntegration = jenkinsIntegration;
    }

}
