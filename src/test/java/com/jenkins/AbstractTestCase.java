package com.jenkins;

import org.apache.log4j.Logger;

/**
 * This class will provide standard functions that all assembly and component
 * test could use.
 */
public abstract class AbstractTestCase
{
    private static final Logger LOG = Logger.getLogger(AbstractTestCase.class);

    /**
     * This is a new assertion test that prints the stack trace and logs an
     * error message with some helpful details
     * 
     * @param test
     *            test name
     * @param exception
     *            an exception that occurred during a test
     */
    protected void failure(final String test, final Throwable exception)
    {
        AbstractTestCase.LOG.fatal(exception.getMessage(), exception);
        throw new RuntimeException(test, exception);
    }

    /**
     * This is a new assertion test that prints the stack trace and logs an
     * error message with some helpful details
     * 
     * @param exception
     *            an exception that occurred during the a test
     */
    protected void failure(final Throwable exception)
    {
        AbstractTestCase.LOG.fatal(exception.getMessage(), exception);
        throw new RuntimeException(exception);
    }

    /**
     * User should replay all Mocked objects here. The method should be called
     * after all expect method calls in each unit test.
     */
    protected abstract void replayAll();

    /**
     * User should verify all mocked objects here. The method should be called
     * at the end of each test.
     */
    protected abstract void verifyAll();

}
