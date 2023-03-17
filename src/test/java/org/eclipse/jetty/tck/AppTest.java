package org.eclipse.jetty.tck;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest extends com.sun.ts.tests.servlet.spec.security.clientcert.Client
{

    @Test
    public void foo() throws Exception {
        super.clientCertTest();
    }

}
