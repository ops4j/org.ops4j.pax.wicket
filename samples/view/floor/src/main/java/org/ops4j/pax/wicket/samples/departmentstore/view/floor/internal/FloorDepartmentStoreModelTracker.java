/**
 * 
 */
package org.ops4j.pax.wicket.samples.departmentstore.view.floor.internal;

import java.util.ArrayList;
import java.util.List;
import org.ops4j.pax.wicket.api.ContentAggregator;
import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStore;
import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class FloorDepartmentStoreModelTracker extends ServiceTracker
{
    final List<ServiceRegistration> m_registrations;

    final List<ContentAggregator> m_floors;

    private final BundleContext m_bundleContext;

    public FloorDepartmentStoreModelTracker(BundleContext context)
    {
        super(context, DepartmentStore.class.getName(), null);
        m_bundleContext = context;
        m_registrations = new ArrayList<ServiceRegistration>();
        m_floors = new ArrayList<ContentAggregator>();

    }

    @Override
    public Object addingService(ServiceReference serviceReference)
    {
        DepartmentStore departmentStore = (DepartmentStore) super.addingService(serviceReference);
        createFloors( m_bundleContext, departmentStore);
        return departmentStore;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object service)
    {
        super.removedService(serviceReference, service);
        removeFloors((DepartmentStore) service);
    }

    private void createFloors(BundleContext context, DepartmentStore departmentStore)
    {
        List<Floor> floors = departmentStore.getFloors();
        String destinationId = "swp.floor";
        for (Floor floor : floors)
        {
            String floorName = floor.getName();
            FloorAggregatedSource aggregatedSource = new FloorAggregatedSource(floor, floorName, destinationId,
                    context, "departmentstore");
            aggregatedSource.setDestination(destinationId);
            aggregatedSource.setAggregationPointName(floor.getName());
            ServiceRegistration registration = aggregatedSource.register();

            m_registrations.add(registration);
            m_floors.add(aggregatedSource);
        }
    }

    private void removeFloors(DepartmentStore departmentStore)
    {
        for (ServiceRegistration registration : m_registrations)
        {
            registration.unregister();
        }
        m_registrations.clear();

        for (ContentAggregator floor : m_floors)
        {
            floor.dispose();
        }
        m_floors.clear();
    }

}