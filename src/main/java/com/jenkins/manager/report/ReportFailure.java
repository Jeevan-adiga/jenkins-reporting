package com.jenkins.manager.report;

public class ReportFailure {

    private String filename;
    private String exception;
    
    public ReportFailure(final String filename, final String exception) {
        super();
        this.filename = filename;
        this.exception = exception;
    }
    
    public void setFilename(final String filename) {
        this.filename = filename;
    }
    public void setException(final String exception) {
        this.exception = exception;
    }
}
