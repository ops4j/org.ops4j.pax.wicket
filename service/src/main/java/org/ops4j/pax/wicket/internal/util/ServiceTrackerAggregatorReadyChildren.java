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

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Since a service tracker is not directly aggregatorable. Therefore this interface slightly modifies the functionality
 * to make it usable.
 */
@Deprecated
public interface ServiceTrackerAggregatorReadyChildren<ServiceType> {

    /**
     * Almost like the addingService method of the ServiceTracker but already including the service and without a return
     * value.
     */
    void addingService(ServiceReference<ServiceType> reference, ServiceType service);

    /**
     * Almost the same like the {@link #modifiedService(ServiceReference, Object)} method of the {@link ServiceTracker}
     * without a super mehtod.
     */
    public void modifiedService(ServiceReference<ServiceType> reference, ServiceType service);

    /**
     * Almost the same like the {@link #removedService(ServiceReference, Object)} method of the {@link ServiceTracker}
     * without a super mehtod.
     */
    public void removedService(ServiceReference<ServiceType> reference, ServiceType service);
}
