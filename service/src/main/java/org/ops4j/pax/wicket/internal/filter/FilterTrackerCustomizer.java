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
package org.ops4j.pax.wicket.internal.filter;

import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.Constants.APPLICATION_NAME;
import static org.osgi.framework.Constants.OBJECTCLASS;

import org.ops4j.pax.wicket.api.FilterFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link org.ops4j.pax.wicket.internal.filter.FilterTrackerCustomizer} do all the work of transforming a {@link org.ops4j.pax.wicket.api.FilterFactory} service into
 * {@link org.ops4j.pax.wicket.internal.filter.FilterFactoryReference}s for usage in PAX Wicket and keep track of the internal state and buffering
 *
 * @author nmw
 * @version $Id: $Id
 */
public final class FilterTrackerCustomizer implements ServiceTrackerCustomizer<FilterFactory, FilterFactoryReference> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterTrackerCustomizer.class);

    private final String applicationName;

    private final BundleContext bundleContext;

    /**
     * <p>Constructor for FilterTrackerCustomizer.</p>
     *
     * @param bundleContext a {@link org.osgi.framework.BundleContext} object.
     * @param applicationName a {@link java.lang.String} object.
     */
    public FilterTrackerCustomizer(BundleContext bundleContext, String applicationName) {
        validateNotNull(bundleContext, "bundleContext");
        validateNotEmpty(applicationName, "applicationName");
        this.bundleContext = bundleContext;
        this.applicationName = applicationName;
    }

    /** {@inheritDoc} */
    public final FilterFactoryReference addingService(ServiceReference<FilterFactory> reference) {
        FilterFactory filterFactory = bundleContext.getService(reference);
        if (filterFactory != null) {
            FilterFactoryReference factoryReference = new FilterFactoryReference(filterFactory);
            LOGGER.debug("added FilterFactory {} for application {}", filterFactory.getClass().getName(),
                applicationName);
            return factoryReference;
        }
        return null;
    }

    /**
     * <p>modifiedService.</p>
     *
     * @param reference a {@link org.osgi.framework.ServiceReference} object.
     * @param service a {@link org.ops4j.pax.wicket.internal.filter.FilterFactoryReference} object.
     */
    public void modifiedService(ServiceReference<FilterFactory> reference, FilterFactoryReference service) {
        if (service != null) {
            service.setProperties(reference);
            LOGGER.debug("updated FilterFactory {} for application {}", service.getFactory().getClass().getName(),
                applicationName);
        }
    }

    /**
     * <p>removedService.</p>
     *
     * @param reference a {@link org.osgi.framework.ServiceReference} object.
     * @param service a {@link org.ops4j.pax.wicket.internal.filter.FilterFactoryReference} object.
     */
    public void removedService(ServiceReference<FilterFactory> reference, FilterFactoryReference service) {
        bundleContext.ungetService(reference);
        if (service != null) {
            service.dispose();
            LOGGER.debug("removed filterFactory for application {}", applicationName);
        }
    }

    /**
     * <p>createOsgiFilter.</p>
     *
     * @return a {@link org.osgi.framework.Filter} object.
     * @throws java.lang.IllegalArgumentException if any.
     */
    public org.osgi.framework.Filter createOsgiFilter()
        throws IllegalArgumentException {
        org.osgi.framework.Filter filter;
        try {
            String filterString = String.format("(&(%s=%s)(%s=%s))", APPLICATION_NAME, applicationName,
                OBJECTCLASS, FilterFactory.class.getName());
            LOGGER.debug("createOsgiFilter={} for application {}", filterString, applicationName);
            filter = bundleContext.createFilter(filterString);
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException("applicationName can not contain '*', '(' or ')' : " + applicationName,
                e);
        }
        return filter;
    }

}
