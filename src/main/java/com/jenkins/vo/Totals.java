package com.jenkins.vo;

public class Totals {

    private String name;
    private String url;
    private int testTotal = 0;
    private int passTotal = 0;
    private int failTotal = 0;
    private int skipTotal = 0;

    public Totals(String name) {
        this.name = name;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void incPass(){
        this.passTotal++;
        this.testTotal++;
    }
    public void incFail(){
        this.failTotal++;
        this.testTotal++;
    }
    public void incSkip(){
        this.skipTotal++;
        this.testTotal++;
    }


    public String getName() {
        return name;
    }
    public int getTestTotal() {
        return testTotal;
    }
    public int getPassTotal() {
        return passTotal;
    }
    public int getFailTotal() {
        return failTotal;
    }
    public int getSkipTotal() {
        return skipTotal;
    }
}
