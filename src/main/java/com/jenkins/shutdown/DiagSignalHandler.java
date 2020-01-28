package com.jenkins.shutdown;

import com.jenkins.Launch;
import org.apache.log4j.Logger;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Diagnostic Signal Handler class definition
 */
@SuppressWarnings("restriction")
public class DiagSignalHandler implements SignalHandler
{

    private SignalHandler oldHandler;
    private static final Logger LOG = Logger.getLogger(DiagSignalHandler.class);

    // Static method to install the signal handler
    /**
     * @param signalName
     * @return DiagSignalHandler
     */
    public static DiagSignalHandler install(final String signalName)
    {
        final Signal diagSignal = new Signal(signalName);
        final DiagSignalHandler diagHandler = new DiagSignalHandler();
        diagHandler.oldHandler = Signal.handle(diagSignal, diagHandler);
        return diagHandler;
    }

    /**
     * @see sun.misc.SignalHandler#handle(sun.misc.Signal)
     */
    @Override
    public void handle(final Signal sig)
    {
        DiagSignalHandler.LOG.error("Diagnostic Signal handler called for signal " + sig);

        // Output information for each thread
        final Thread[] threadArray = new Thread[Thread.activeCount()];
        final int numThreads = Thread.enumerate(threadArray);
        DiagSignalHandler.LOG.info("Current threads:");
        for (int i = 0; i < numThreads; i++)
        {
            DiagSignalHandler.LOG.info("    " + threadArray[i]);
        }

        DiagSignalHandler.LOG.warn("Signal " + sig);
        DiagSignalHandler.LOG.warn("Shutting down...");

        if (Launch.getAppContext() != null)
        {
            Launch.getAppContext().close();
        }
        // Chain back to previous handler, if one exists
        if (oldHandler != SignalHandler.SIG_DFL && oldHandler != SignalHandler.SIG_IGN)
        {
            oldHandler.handle(sig);
        }
    }

}
