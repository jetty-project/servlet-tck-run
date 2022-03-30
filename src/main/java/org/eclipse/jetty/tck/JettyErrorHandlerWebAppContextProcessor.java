package org.eclipse.jetty.tck;


import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.jboss.arquillian.container.jetty.embedded_11.WebAppContextProcessor;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class JettyErrorHandlerWebAppContextProcessor implements WebAppContextProcessor {

    @Override
    public void process(WebAppContext webAppContext, Archive<?> archive) {
        if ("servlet_spec_errorpage_web.war".equals(archive.getName())) {
            ErrorPageErrorHandler errorPageErrorHandler = new ErrorPageErrorHandler();
            errorPageErrorHandler.setUnwrapServletException(true);
            webAppContext.setErrorHandler(errorPageErrorHandler);
        }
    }
}
