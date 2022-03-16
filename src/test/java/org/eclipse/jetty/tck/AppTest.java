package org.eclipse.jetty.tck;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.jupiter.api.Test;
import com.sun.ts.tests.servlet.api.jakarta_servlet_http.httpservlet.URLClient;


import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest extends URLClient
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

}
