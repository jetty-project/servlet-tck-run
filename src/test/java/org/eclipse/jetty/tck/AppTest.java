package org.eclipse.jetty.tck;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import servlet.tck.api.jakarta_servlet.servletrequest.ServletRequestTests;

/**
 * Unit test for simple App.
 */
@Disabled
public class AppTest extends ServletRequestTests
{

    @Test
    public void foo() throws Exception {
        super.setCharacterEncodingTest1();
    }

}
