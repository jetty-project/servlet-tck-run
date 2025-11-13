package org.eclipse.jetty.tck;

import org.jboss.arquillian.junit5.JUnitJupiterTestClassLifecycleManager;
import org.jboss.arquillian.test.spi.TestRunnerAdaptor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import servlet.tck.api.jakarta_servlet.dispatchtest.DispatchTests;
import servlet.tck.api.jakarta_servlet.servletrequest.ServletRequestTests;
import servlet.tck.api.jakarta_servlet_http.httpservletresponsewrapper.HttpServletResponseWrapperTests;
import servlet.tck.api.jakarta_servlet_http.httpsessionx.HttpSessionxTests;
import servlet.tck.pluggability.api.jakarta_servlet_http.httpservletresponse.HttpServletResponseTests;
import servlet.tck.spec.security.secform.SecFormTests;

/**
 * Unit test for simple App.
 */

public class AppTest extends HttpServletResponseWrapperTests

{

    @Test
    public void foo() throws Exception {
        JUnitJupiterTestClassLifecycleManager s;
//        System.setProperty("servlet.tck.support.crossContext", "false");
//        //super.invalidateHttpSessionTest();
//        super.flushBufferTest();
        super.getWriterTest();
    }

}
