package com.jenkins;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jenkins.manager.JobManager;
import com.jenkins.shutdown.DiagSignalHandler;
import com.jenkins.shutdown.ShutdownHook;

public class Launch
{
    private static final Logger LOG = Logger.getLogger(Launch.class);

    // private Map<String, String> arguments;
    private static AbstractApplicationContext appContext;
    public final static String PARAM_VIEW_NAME = "viewName";
    public final static String PARAM_JOB_NAME = "jobName";

    public final static String SHARED_CONTEXT = "sharedContext.xml";
    public final static String REPORT_CONTEXT = "reportContext.xml";
    public final static String REPORT_BEAN = "reportManager";


    /**
     * To be deleted if we roll back.
     *
     * @return the appContext
     */
    public static AbstractApplicationContext getAppContext()
    {
        return Launch.appContext;
    }

    /**
     * @param args
     */
    public static void main(final String[] args)
    {
        new Launch().process(args);
    }

    protected static void setApplicationContext(final AbstractApplicationContext context)
    {
        Launch.appContext = context;
    }


    /**
     * @param args
     */
    public void process(final String[] args)
    {

        try
        {
            if (args == null || args.length < 2)
            {
                throw new RuntimeException("Parameter list cannot be null: \n" + help());
            }

            Map<String, String> paramMap = this.getParameterMap(args);

            setAppContext(new String[] { SHARED_CONTEXT,REPORT_CONTEXT});
            String mainBean=REPORT_BEAN;

            // register shutdown hooks
            Launch.appContext.registerShutdownHook();
            final ShutdownHook shutdownHook = new ShutdownHook();
            Runtime.getRuntime().addShutdownHook(shutdownHook);

            DiagSignalHandler.install("INT");// Interactive attention (CTRL-C). JVM will exit normally.
            DiagSignalHandler.install("TERM");// Termination request. JVM will exit normally.
            DiagSignalHandler.install("ABRT");// Abnormal termination. The JVM raises this signal whenever it detects a JVM fault.

            final JobManager job = (JobManager) Launch.appContext.getBean(mainBean);
            job.process(paramMap);
        }
        catch (final Exception e)
        {
            Launch.LOG.error("Failure launching Spring", e);
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> getParameterMap(String[] args) {
        Map<String, String> retMap = new HashMap<String, String>();
        String[] keyValue = args[0].split("=");
        retMap.put(keyValue[0], keyValue[1]);

        keyValue = args[1].split("=");
        retMap.put(keyValue[0], keyValue[1]);

        return retMap;
    }

    protected void setAppContext(final String[] contextFiles)
    {
        if (Launch.appContext == null)
        {
            Launch.appContext = new ClassPathXmlApplicationContext(contextFiles);
        }
    }

    /**
     * Display parameter options to user
     *
     * @return Display parameter in a String
     */
    private String help()
    {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nValid Parameters: "+PARAM_VIEW_NAME+" and "+PARAM_JOB_NAME);
        stringBuilder.append("\n\t"+PARAM_VIEW_NAME+" - This should be set to the current view in Jenkins that you wish to run reports against: example: viewName=\"03. Appium Mobile Tests\"");
        stringBuilder.append("\n\t"+PARAM_JOB_NAME+" - Name of the current job. This will be filtered out of the reports.");
        return stringBuilder.toString();
    }

}
