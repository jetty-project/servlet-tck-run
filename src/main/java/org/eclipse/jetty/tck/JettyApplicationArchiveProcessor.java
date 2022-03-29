package org.eclipse.jetty.tck;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class JettyApplicationArchiveProcessor implements ApplicationArchiveProcessor {

    @Override
    public void process(Archive<?> archive, TestClass testClass) {
        if ("servlet_spec_errorpage_web.war".equals(archive.getName())) {
            WebArchive webArchive = (WebArchive) archive;
            // Thread.currentThread().getContextClassLoader().getResource
            webArchive.addAsWebInfResource("servlet_spec_errorpage_web/servlet_spec_errorpage_web.xml",
                    "servlet_spec_errorpage_web.xml");
        }
    }
}
