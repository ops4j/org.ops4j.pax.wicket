/**
 * Copyright OPS4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.api.support;

import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;

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

    private static final String[] SERVICE_CLASSES = {
        FilterFactory.class.getName(),
        ManagedService.class.getName(),
    };

    private final BundleContext bundleContext;
    private final Dictionary<String, Object> properties = new Hashtable<String, Object>();

    private ServiceRegistration<?> filterFactoryServiceRegistration;

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
            filterFactoryServiceRegistration = bundleContext.registerService(SERVICE_CLASSES, this, properties);
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

    public void updated(Dictionary<String, ?> config) throws ConfigurationException {
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

    protected BundleContext getBundleContext() {
        return bundleContext;
    }
}
