package com.jenkins.manager.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jenkins.Launch;
import com.jenkins.dto.report.BuildListing;
import com.jenkins.dto.report.Job;
import com.jenkins.dto.report.JobListing;
import com.jenkins.dto.report.TestReport;
import com.jenkins.integration.report.JenkinsIntegration;
import com.jenkins.vo.TestCaseWiseReport;
import com.jenkins.vo.Totals;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.poi.ss.examples.html.ToHtml;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jenkins.dto.report.Build;
import com.jenkins.dto.report.View;
import com.jenkins.dto.report.issues.TestCase;
import com.jenkins.manager.JobManager;

public class DetailedReportManagerImpl implements JobManager {

    private static Logger log = Logger.getLogger(DetailedReportManagerImpl.class);
    private JenkinsIntegration jenkinsIntegration;
	
    protected final static String OUTPUT_FILE_NAME = "Automation_Detailed_Summary_Report.xlsx";
    protected final static int MODULE_NAME = 0;
    protected final static int TESTCASE_NAME = 1;
    protected final static int STATUS = 2;
    protected final static int AGE = 3;
    protected final static int DURATION = 4;
//    protected final static int ISSUE_CELL = 5;
    protected final static int COLUMN_COUNT = 5;

    @Override
    public void process(Map<String, String> paramMap) throws Exception {
    	
    	List<TestCaseWiseReport> reportLines = null;
//    	if(paramMap.get(Launch.PARAM_VIEW_NAME).equalsIgnoreCase("EMPTY")) {
//    		  reportLines = this.executeApiRequests( paramMap.get(Launch.PARAM_JOB_NAME));
//    	}else {
//    		 reportLines = this.executeApiRequests(paramMap.get(Launch.PARAM_VIEW_NAME), paramMap.get(Launch.PARAM_JOB_NAME));
    		 reportLines = this.executeApiRequestsDetailed(paramMap.get(Launch.PARAM_VIEW_NAME), paramMap.get(Launch.PARAM_JOB_NAME));
//    	}
       
        this.createReportDetailed(reportLines);
//        this.createReport(reportLines);
    }

    private void createReportDetailed(List<TestCaseWiseReport> reportLines) throws IOException {
        int rowCount = 0;

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        XSSFRow row = sheet.createRow(rowCount++);
        this.createHeaderRowDetailed(workbook, row);

        for (TestCaseWiseReport totals : reportLines) {
            row = sheet.createRow(rowCount++);
            this.createRow(workbook, row, totals);
        }

        for (int j = 0; j <= COLUMN_COUNT; ++j) {
            sheet.autoSizeColumn(j);
        }

        FileOutputStream fout = null;
        ByteArrayOutputStream outputStream = null;

        try {

            // Directory path where the xlsx file will be created is stored in the
            // json config file.
            fout = new FileOutputStream(OUTPUT_FILE_NAME);
            outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            outputStream.writeTo(fout);
            System.out.println("SPREADSHEET written to " + OUTPUT_FILE_NAME);

            int lastIndex = OUTPUT_FILE_NAME.lastIndexOf('.');

            if (lastIndex > 0) {
                String htmlFile = OUTPUT_FILE_NAME.substring(0, lastIndex) + ".html";
                ToHtml toHtml = ToHtml.create(workbook, new PrintWriter(new FileWriter(htmlFile)));

                toHtml.setCompleteHTML(true);
                toHtml.printPage();

                System.out.println("HTML written to " + htmlFile);
            }

        } catch (Exception ex) {
            System.out.println("FAILED TO WRITE TO FILE");
            ex.printStackTrace();

        } finally {

            if (outputStream != null) {
                outputStream.close();
            }

            if (fout != null) {
                fout.close();
            }
        }

    }
    
    private void createHeaderRowDetailed(XSSFWorkbook workbook, XSSFRow row){
        XSSFCell cell = row.createCell(MODULE_NAME);
        decorateHeaderCell(workbook, cell);
        cell.setCellValue("Module");

        cell = row.createCell(TESTCASE_NAME);
        decorateHeaderCell(workbook, cell);
        cell.setCellValue("TestCase Name");

        cell = row.createCell(STATUS);
        decorateHeaderCell(workbook, cell);
        cell.setCellValue("Status");
        
        cell = row.createCell(AGE);
        decorateHeaderCell(workbook, cell);
        cell.setCellValue("AGE");
        
        cell = row.createCell(DURATION);
        decorateHeaderCell(workbook, cell);
        cell.setCellValue("Duration");
    }

    private void createRow(XSSFWorkbook workbook, XSSFRow row, TestCaseWiseReport totals){
        XSSFCell cell = row.createCell(MODULE_NAME);
        cell.setCellValue(totals.getModuleName());

        cell = row.createCell(TESTCASE_NAME);
        cell.setCellValue(totals.getTestCaseName());
        
        cell = row.createCell(STATUS);
        cell.setCellValue(totals.getStatus());
        
        cell = row.createCell(AGE);
        cell.setCellValue(totals.getAge());
        
        cell = row.createCell(DURATION);
        cell.setCellValue(totals.getDuration());
    }

    protected File getFile(final String name){
        return new File(name);
    }
    
    protected List<TestCaseWiseReport> executeApiRequestsDetailed(String viewName, String jobName) {
        List<TestCaseWiseReport> reportLines = new ArrayList<TestCaseWiseReport>();

        try {

            log.info("starting process...");

            View view = jenkinsIntegration.getView(viewName);
            List<JobListing> jobListings = view.getJobs();

            Totals runningTotal = new Totals("Totals");
            TestCaseWiseReport jobTotal = null;

            log.info("Executing API requests... ");
            for (JobListing jobListing : jobListings) {

                log.info("Job Name: "+jobListing.getName());

                if (!jobName.equals(jobListing.getName()) && !jobListing.getName().toLowerCase().contains("debug")) {
                    NDC.push("Job: " + jobListing.getName());
                    jobTotal = new TestCaseWiseReport();
                    Job job = jenkinsIntegration.getJob(jobListing.getName());
                    BuildListing buildListing = job.getLastBuild();
                    TestReport testReport = null;
                    if (buildListing != null) {
                        log.info("last build: " + buildListing.getNumber());
                        NDC.push(" - build: " + buildListing.getNumber());
                        Build build = jenkinsIntegration.getBuild(job, buildListing.getNumber());
                        log.info("Result: " + build.getResult());
                        testReport = jenkinsIntegration.getTestReport(job, buildListing.getNumber());

                        if (testReport == null) {
                            log.warn("No report for given build/job.");
                        } else {
                            jobTotal.setUrl(testReport.getUrl());

                            try {
                                List<TestCase> testCases = testReport.getChildReports().get(0).getResult().getSuites().get(0).getTestCases();

                                for (TestCase testCase : testCases) {
                                	if(!testCase.isConfigMethod(testCase.getName())){
                                    	System.out.println(job.getName()+" | "+testCase.getTestCaseName()+" | "+testCase.getStatus());
                                    	jobTotal = new TestCaseWiseReport();
                                    	jobTotal.setModuleName(job.getName());
                                    	jobTotal.setTestCaseName(testCase.getTestCaseName());
                                    	jobTotal.setStatus(testCase.getStatus());
                                    	jobTotal.setAge(testCase.getAge());
                                    	jobTotal.setDuration(testCase.getDuration());
                                    	reportLines.add(jobTotal);
                                	}

                                }
                            } catch (IndexOutOfBoundsException e) {
                                log.warn("API report incomplete");
                            }
                        }
                    }

                    if(jobTotal!=null) {
                        reportLines.add(jobTotal);
                    }
                    NDC.pop();
                }
                NDC.pop();
            }
            reportLines.add(jobTotal);
            log.info("Total Tests="+runningTotal.getTestTotal()+" - Total Passed="+runningTotal.getPassTotal()+" - Total Failed="+runningTotal.getFailTotal()+" - Total Skipped="+runningTotal.getSkipTotal());
            log.warn("API requests complete");

        } catch (final Exception e) {
             log.fatal("Failure in process method",e);
             throw(e);
        } finally {
            log.warn("process method complete...");
            NDC.remove();
        }
        return reportLines;
    }
    
//    with out view
    protected List<Totals> executeApiRequests(String jobName) {
        List<Totals> reportLines = new ArrayList<Totals>();
        Totals runningTotal = new Totals("Totals");
        Totals jobTotal = null;

        try {

            log.info("starting process...");

         
            jobTotal = new Totals(jobName);
                    Job job = jenkinsIntegration.getJob(jobName);
                    BuildListing buildListing = job.getLastBuild();
                    TestReport testReport = null;
                    if (buildListing != null) {
                        log.info("last build: " + buildListing.getNumber());
                        NDC.push(" - build: " + buildListing.getNumber());
                        Build build = jenkinsIntegration.getBuild(job, buildListing.getNumber());
                        log.info("Result: " + build.getResult());
                        testReport = jenkinsIntegration.getTestReport(job, buildListing.getNumber());

                        if (testReport == null) {
                            log.warn("No report for given build/job.");
                        } else {
                            jobTotal.setUrl(testReport.getUrl());

                            try {
                                List<TestCase> testCases = testReport.getChildReports().get(0).getResult().getSuites().get(0).getTestCases();

                                for (TestCase testCase : testCases) {
                                    testCase.incrementCounts(jobTotal, runningTotal);
                                }

                                log.info("Test Report: Total=" + jobTotal.getTestTotal() + " - Passed=" + jobTotal.getPassTotal() + " - Failed=" + jobTotal.getFailTotal() + " - Skipped=" + jobTotal.getSkipTotal());
                            } catch (IndexOutOfBoundsException e) {
                                log.warn("API report incomplete");
                            }
                        }
                    }

                    if(jobTotal!=null) {
                        reportLines.add(jobTotal);
                    }
                    NDC.pop();
                
                NDC.pop();
           
            reportLines.add(runningTotal);
            log.info("Total Tests="+runningTotal.getTestTotal()+" - Total Passed="+runningTotal.getPassTotal()+" - Total Failed="+runningTotal.getFailTotal()+" - Total Skipped="+runningTotal.getSkipTotal());
            log.warn("API requests complete");

        } catch (final Exception e) {
             log.fatal("Failure in process method",e);
             throw(e);
        } finally {
            log.warn("process method complete...");
            NDC.remove();
        }
        return reportLines;
    }
    
//  with out view
    public static void decorateHeaderCell(XSSFWorkbook workbook, XSSFCell cell) {
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        cell.setCellStyle(style);
    }

    public static void decorateSuccessCell(XSSFWorkbook workbook, XSSFCell cell) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIME.getIndex());
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        cell.setCellStyle(style);
    }

    public static void decorateFailureCell(XSSFWorkbook workbook, XSSFCell cell) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        //style.setFillForegroundColor(IndexedColors.CORAL.getIndex());
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        cell.setCellStyle(style);
    }

    public static void decorateSkippedCell(XSSFWorkbook workbook, XSSFCell cell) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        cell.setCellStyle(style);
    }

    public static void decorateBoldCell(XSSFWorkbook workbook, XSSFCell cell) {
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        cell.setCellStyle(style);
    }

    protected void checkExceptions(final Date date, final List<Exception> failures) throws Exception{
        log.info("Checking for exceptions... ");
        final StringBuilder exceptionMessages = new StringBuilder("<br><br>");
        for (final Exception exception : failures) {
            exceptionMessages.append(exception.getMessage()+"<br>");
        }
    }

    protected List<Exception> getNewExceptionList() {
        return new ArrayList<Exception>();
    }

    protected Date getDate() {
        return new Date();
    }

    public void setJenkinsIntegration(JenkinsIntegration jenkinsApiIntegration) {
        this.jenkinsIntegration = jenkinsApiIntegration;
    }

}
