package org.eclipse.jetty.tck;


import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.jboss.arquillian.container.jetty.embedded_11.WebAppContextProcessor;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.net.URISyntaxException;
import java.net.URL;

public class JettyErrorHandlerWebAppContextProcessor implements WebAppContextProcessor {

    @Override
    public void process(WebAppContext webAppContext, Archive<?> archive) {
        if ("servlet_spec_errorpage_web.war".equals(archive.getName())) {
            ErrorPageErrorHandler errorPageErrorHandler = new ErrorPageErrorHandler();
            errorPageErrorHandler.setUnwrapServletException(true);
            webAppContext.setErrorHandler(errorPageErrorHandler);
        }
        if ("servlet_spec_fragment_web.war".equals(archive.getName())) {
            URL url = Thread.currentThread().getContextClassLoader()
                    .getResource("servlet_spec_fragment_web/webdefault.xml");
            try {
                webAppContext.setDefaultsDescriptor(url.toURI().getPath());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
