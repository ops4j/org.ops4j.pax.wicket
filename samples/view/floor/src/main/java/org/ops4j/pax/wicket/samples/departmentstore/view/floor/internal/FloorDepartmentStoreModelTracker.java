/**
 * 
 */
package org.ops4j.pax.wicket.samples.departmentstore.view.floor.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ops4j.pax.wicket.api.ContentAggregator;
import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStore;
import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class FloorDepartmentStoreModelTracker extends ServiceTracker {
    private final BundleContext bundleContext;
    private final Map<ServiceReference, List<ContentAggregator>> registrations;

    public FloorDepartmentStoreModelTracker(BundleContext bundleContext) {
        super(bundleContext, DepartmentStore.class.getName(), null);
        this.bundleContext = bundleContext;
        registrations = new HashMap<ServiceReference, List<ContentAggregator>>();
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        DepartmentStore departmentStore = (DepartmentStore) super.addingService(serviceReference);
        createFloors(serviceReference, departmentStore);
        return departmentStore;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object service) {
        super.removedService(serviceReference, service);
        removeFloors(serviceReference, (DepartmentStore) service);
    }

    private void createFloors(ServiceReference serviceReference, DepartmentStore departmentStore) {
        List<ContentAggregator> content = new ArrayList<ContentAggregator>();
        List<Floor> floors = departmentStore.getFloors();
        String destinationId = "swp.floor";
        for (Floor floor : floors) {
            String floorName = floor.getName();
            FloorAggregatedSource aggregatedSource = new FloorAggregatedSource(floor, floorName, destinationId,
                bundleContext, "departmentstore");
            aggregatedSource.setDestination(destinationId);
            aggregatedSource.setAggregationPointName(floor.getName());
            aggregatedSource.register();
            content.add(aggregatedSource);
        }
        registrations.put(serviceReference, content);
    }

    private void removeFloors(ServiceReference serviceReference, DepartmentStore departmentStore) {
        for (ContentAggregator floor : registrations.get(serviceReference)) {
            floor.dispose();
        }
        registrations.remove(serviceReference);
    }

}
