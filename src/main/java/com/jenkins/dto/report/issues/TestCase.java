package com.jenkins.dto.report.issues;

import java.util.regex.Pattern;

import com.jenkins.vo.Totals;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCase {

    private static Logger log = Logger.getLogger(TestCase.class);
    protected final static String SETUP_METHOD ="setUp, startTest, initRun";
    protected final static String TEARDOWN_METHOD ="tearDown, endTest";
    protected final static String PASS ="PASSED";
    protected final static String FAIL ="FAILED";
    protected final static String SKIP ="SKIPPED";
    protected final static String FIXED ="FIXED";
    protected final static String REGRESSION ="REGRESSION";

    @JsonProperty("name")
    private String name;

    @JsonProperty("status")
    private String status;

	@JsonProperty("age")
    private String age;

    @JsonProperty("duration")
    private String duration;

    public String getAge() {
  		return age;
  	}

  	public void setAge(String age) {
  		this.age = age;
  	}

  	public String getDuration() {
  		return duration;
  	}

  	public void setDuration(String duration) {
  		this.duration = duration;
  	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }
    
    public String getTestCaseName() {
    	Pattern pattern = Pattern.compile("\\[(.*?) passed=");
		java.util.regex.Matcher matcher = pattern.matcher(name);
		while(matcher.find()){
			return matcher.group(1);
		}
		return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void incrementCounts(Totals jobTotals, Totals runningTotals){
        if(!isConfigMethod(name)) {
            switch (status) {
            case PASS:
            case FIXED:
                jobTotals.incPass();
                runningTotals.incPass();
                break;
            case FAIL:
            case REGRESSION:
                jobTotals.incFail();
                runningTotals.incFail();
                break;
            case SKIP:
                jobTotals.incSkip();
                runningTotals.incSkip();
                break;
            default:
                log.error("STATUS: "+status+" is not recognized. This needs to be added and handled in the reporting code.");
                break;
            }
        }
    }


    public boolean isConfigMethod(String testName){
        if(SETUP_METHOD.contains(testName) || TEARDOWN_METHOD.contains(testName)) {
            return true;
        } else {
            return false;
        }
    }

}
