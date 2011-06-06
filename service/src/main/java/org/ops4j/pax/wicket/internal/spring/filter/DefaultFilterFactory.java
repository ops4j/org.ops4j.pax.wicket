package org.ops4j.pax.wicket.internal.spring.filter;

import javax.servlet.Filter;

import org.ops4j.pax.wicket.util.AbstractFilterFactory;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFilterFactory extends AbstractFilterFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFilterFactory.class);

    private Class<? extends Filter> filterClass;
    private String applicationName;

    public DefaultFilterFactory(BundleContext bundleContext, Class<? extends Filter> filterClass, Integer priority,
            String applicationName) {
        super(bundleContext, applicationName, priority);
        this.applicationName = applicationName;
        this.filterClass = filterClass;
    }

    public void start() {
        register();
    }

    public void stop() {
        dispose();
    }

    public Filter createFilter() {
        ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(filterClass.getClassLoader());
            LOGGER.info("Creating new instance of {} for application {}", filterClass.getName(), applicationName);
            return filterClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Filter %s could not be created for application {}",
                filterClass.getName(), applicationName), e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassloader);
        }
    }

}
