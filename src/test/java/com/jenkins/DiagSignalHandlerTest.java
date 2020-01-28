package com.jenkins;

import com.jenkins.shutdown.DiagSignalHandler;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the DiagSignalHandler class.
 */
@SuppressWarnings("restriction")
public class DiagSignalHandlerTest
{

    private final static String SIGNAL_NAME = "INT";

    /**
     * tests the install method.
     */
    @Test
    public void testInstall()
    {

        final DiagSignalHandler result = DiagSignalHandler.install(DiagSignalHandlerTest.SIGNAL_NAME);
        Assert.assertNotNull(result);

    }

}
