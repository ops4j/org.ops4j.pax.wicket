package org.ops4j.pax.wicket.util;

import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;

import java.util.Dictionary;
import java.util.Hashtable;

import org.ops4j.pax.wicket.api.FilterFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFilterFactory implements FilterFactory, ManagedService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFilterFactory.class);

    private static final String[] classes = {
        FilterFactory.class.getName(),
        ManagedService.class.getName(),
    };

    private BundleContext bundleContext;
    private Dictionary<String, Object> properties = new Hashtable<String, Object>();

    private ServiceRegistration filterFactoryServiceRegistration;

    public AbstractFilterFactory(BundleContext bundleContext, String applicationName, Integer priority) {
        validateNotNull(bundleContext, "bundleContext");
        validateNotEmpty(applicationName, "applicationName");
        validateNotNull(priority, "priority");
        this.bundleContext = bundleContext;
        setApplicationName(applicationName);
        setPriority(priority);
    }

    private void setPriority(Integer priority) {
        synchronized (this) {
            properties.put(FILTER_PRIORITY, priority);
            LOGGER.debug("Priority of filterFactory had been updated to {}", priority);
        }
    }

    public Integer getPriority() {
        synchronized (this) {
            return (Integer) properties.get(FILTER_PRIORITY);
        }
    }

    private void setApplicationName(String applicationName) {
        synchronized (this) {
            properties.put(APPLICATION_NAME, applicationName);
            LOGGER.debug("ApplicationName of filterFactory had been updated to {}", applicationName);
        }
    }

    public String getApplicationName() {
        synchronized (this) {
            return (String) properties.get(APPLICATION_NAME);
        }
    }

    public final void register() {
        synchronized (this) {
            if (filterFactoryServiceRegistration != null) {
                throw new IllegalStateException(String.format("%s [%s] has been registered.", getClass()
                    .getSimpleName(), this));
            }
            filterFactoryServiceRegistration = bundleContext.registerService(classes, this, properties);
            LOGGER.info("Registered filterFactory for application {} with priority {}", getApplicationName(),
                getPriority());
        }
    }

    public final void dispose() {
        if (filterFactoryServiceRegistration == null) {
            throw new IllegalStateException(String.format("%s [%s] has not been registered.", getClass()
                .getSimpleName(), this));
        }
        filterFactoryServiceRegistration.unregister();
        filterFactoryServiceRegistration = null;
        LOGGER.info("Disposed filterFactory for application {} with priority {}", getApplicationName(),
            getPriority());
    }

    @SuppressWarnings("rawtypes")
    public void updated(Dictionary config) throws ConfigurationException {
        if (config != null) {
            Integer filterPriority = (Integer) config.get(FILTER_PRIORITY);
            String applicationName = (String) config.get(APPLICATION_NAME);
            setPriority(filterPriority);
            setApplicationName(applicationName);
        }
        synchronized (this) {
            filterFactoryServiceRegistration.setProperties(config);
        }
    }

    public int compareTo(FilterFactory o) {
        return getPriority() - o.getPriority();
    }
    
    protected BundleContext getBundleContext() {
        return this.bundleContext;
    }
}
