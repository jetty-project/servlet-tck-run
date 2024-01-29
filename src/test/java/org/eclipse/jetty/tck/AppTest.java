package org.eclipse.jetty.tck;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import servlet.tck.api.jakarta_servlet.dispatchtest.DispatchTests;
import servlet.tck.api.jakarta_servlet.servletrequest.ServletRequestTests;
import servlet.tck.api.jakarta_servlet_http.httpsessionx.HttpSessionxTests;
import servlet.tck.pluggability.api.jakarta_servlet_http.httpservletresponse.HttpServletResponseTests;
import servlet.tck.spec.security.secform.SecFormTests;

/**
 * Unit test for simple App.
 */
@Disabled
public class AppTest extends SecFormTests

{

    @Test
    public void foo() throws Exception {
//        System.setProperty("servlet.tck.support.crossContext", "false");
//        //super.invalidateHttpSessionTest();
//        super.flushBufferTest();
        super.test6_anno();
    }

}
