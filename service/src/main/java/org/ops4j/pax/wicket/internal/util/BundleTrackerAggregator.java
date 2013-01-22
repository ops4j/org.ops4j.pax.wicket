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
package org.ops4j.pax.wicket.internal.util;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * Simple aggregator for {@link ServiceTrackerAggregatorReadyChildren}s of the same type which needs to be called in a
 * specific order. Therefore add the child {@link ServiceTrackerAggregatorReadyChildren}s in the order you would like to
 * have them called! As another remark
 */
public class BundleTrackerAggregator<ServiceType> extends ServiceTracker {

    private final ServiceTrackerAggregatorReadyChildren<ServiceType>[] children;

    public BundleTrackerAggregator(BundleContext context, Filter filter, ServiceTrackerCustomizer customizer,
            ServiceTrackerAggregatorReadyChildren<ServiceType>... children) {
        super(context, filter, customizer);
        this.children = children;
    }

    public BundleTrackerAggregator(BundleContext context, ServiceReference reference,
            ServiceTrackerCustomizer customizer, ServiceTrackerAggregatorReadyChildren<ServiceType>... children) {
        super(context, reference, customizer);
        this.children = children;
    }

    public BundleTrackerAggregator(BundleContext context, String clazz, ServiceTrackerCustomizer customizer,
            ServiceTrackerAggregatorReadyChildren<ServiceType>... children) {
        super(context, clazz, customizer);
        this.children = children;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object addingService(ServiceReference reference) {
        Object service = super.addingService(reference);
        for (ServiceTrackerAggregatorReadyChildren<ServiceType> child : children) {
            child.addingService(reference, (ServiceType) service);
        }
        return service;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void modifiedService(ServiceReference reference, Object service) {
        for (ServiceTrackerAggregatorReadyChildren<ServiceType> child : children) {
            child.addingService(reference, (ServiceType) service);
        }
        super.modifiedService(reference, service);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removedService(ServiceReference reference, Object service) {
        for (int i = children.length - 1; i >= 0; i--) {
            children[i].removedService(reference, (ServiceType) service);
        }
        super.removedService(reference, service);
    }

}
