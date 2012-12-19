package org.ops4j.pax.wicket.internal.util;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Since a service tracker is not directly aggregatorable. Therefore this interface slightly modifies the functionality
 * to make it usable.
 */
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
