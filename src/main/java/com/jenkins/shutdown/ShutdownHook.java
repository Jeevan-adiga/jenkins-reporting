package com.jenkins.shutdown;

import org.apache.log4j.Logger;

public class ShutdownHook extends Thread
{
    private static final Logger LOG = Logger.getLogger(ShutdownHook.class);

    @Override
    public void run()
    {
        ShutdownHook.LOG.warn("Shutting down...");
    }

}
