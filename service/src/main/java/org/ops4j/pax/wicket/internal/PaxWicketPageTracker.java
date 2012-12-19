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
package org.ops4j.pax.wicket.internal;

import static org.ops4j.pax.wicket.api.Constants.APPLICATION_NAME;
import static org.ops4j.pax.wicket.internal.TrackingUtil.createAllPageFactoryFilter;

import org.apache.wicket.request.component.IRequestablePage;
import org.ops4j.pax.wicket.api.PageFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class PaxWicketPageTracker extends
        ServiceTracker<PageFactory<? extends IRequestablePage>, PageFactory<? extends IRequestablePage>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaxWicketPageTracker.class);

    private final String applicationName;
    private final PaxWicketPageFactory paxWicketPageFactory;

    PaxWicketPageTracker(BundleContext context, String applicationName, PaxWicketPageFactory paxWicketPageFactory) {
        super(context, createAllPageFactoryFilter(context, applicationName), null);

        this.applicationName = applicationName;
        this.paxWicketPageFactory = paxWicketPageFactory;
    }

    /**
     * Default implementation of the {@code ServiceTrackerCustomizer.addingService} method.
     * 
     * <p>
     * This method is only called when this <code>ServiceTracker</code> object has been constructed with a
     * <code>null ServiceTrackerCustomizer</code> argument.
     * 
     * The default implementation returns the result of calling <code>getService</code>, on the
     * <code>BundleContext</code> object with which this <code>ServiceTracker</code> object was created, passing the
     * specified <code>ServiceReference</code> object.
     * <p>
     * This method can be overridden in a subclass to customize the service object to be tracked for the service being
     * added. In that case, take care not to rely on the default implementation of removedService that will unget the
     * service.
     * 
     * @param reference Reference to service being added to this <code>ServiceTracker</code> object.
     * 
     * @return The service object to be tracked for the service added to this <code>ServiceTracker</code> object.
     * 
     * @see org.osgi.util.tracker.ServiceTrackerCustomizer
     */
    @Override
    public final PageFactory<? extends IRequestablePage> addingService(
            ServiceReference<PageFactory<? extends IRequestablePage>> reference) {
        PageFactory<? extends IRequestablePage> pageSource = super.addingService(reference);
        paxWicketPageFactory.add(pageSource);
        return pageSource;
    }

    /**
     * Default implementation of the <code>ServiceTrackerCustomizer.modifiedService</code> method.
     * 
     * <p>
     * This method is only called when this <code>ServiceTracker</code> object has been constructed with a
     * <code>null ServiceTrackerCustomizer</code> argument.
     * 
     * The default implementation does nothing.
     * 
     * @param reference Reference to modified service.
     * @param service The service object for the modified service.
     * 
     * @see org.osgi.util.tracker.ServiceTrackerCustomizer
     */
    @Override
    public final void modifiedService(ServiceReference<PageFactory<? extends IRequestablePage>> reference,
            PageFactory<? extends IRequestablePage> service) {
        String appName = (String) reference.getProperty(APPLICATION_NAME);
        if (!applicationName.equals(appName)) {
            paxWicketPageFactory.remove(service);
        }
    }

    /**
     * Default implementation of the <code>ServiceTrackerCustomizer.removedService</code> method.
     * 
     * <p>
     * This method is only called when this <code>ServiceTracker</code> object has been constructed with a
     * <code>null ServiceTrackerCustomizer</code> argument.
     * 
     * The default implementation calls <code>ungetService</code>, on the <code>BundleContext</code> object with which
     * this <code>ServiceTracker</code> object was created, passing the specified <code>ServiceReference</code> object.
     * <p>
     * This method can be overridden in a subclass. If the default implementation of <code>addingService</code> method
     * was used, this method must unget the service.
     * 
     * @param reference Reference to removed service.
     * @param service The service object for the removed service.
     * 
     * @see org.osgi.util.tracker.ServiceTrackerCustomizer
     */
    @Override
    public final void removedService(ServiceReference<PageFactory<? extends IRequestablePage>> reference,
            PageFactory<? extends IRequestablePage> service) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("removedService( " + reference + ", " + service + ");");
        }
        paxWicketPageFactory.remove(service);
        super.removedService(reference, service);
    }
}
