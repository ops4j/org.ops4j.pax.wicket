/**
 *
 */
package org.ops4j.pax.wicket.samples.departmentstore.view.franchisee.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStore;
import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.samples.departmentstore.model.Franchisee;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class FranchiseeDepartmentStoreModelTracker extends ServiceTracker {

    private final BundleContext bundleContext;
    private final Map<ServiceReference, List<FranchiseeContentSource>> registrations;

    public FranchiseeDepartmentStoreModelTracker(BundleContext bundleContext) {
        super(bundleContext, DepartmentStore.class.getName(), null);
        this.bundleContext = bundleContext;
        registrations = new HashMap<ServiceReference, List<FranchiseeContentSource>>();
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        DepartmentStore departmentStore = (DepartmentStore) super.addingService(serviceReference);
        registerContentSources(serviceReference, departmentStore);
        return departmentStore;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object service) {
        super.removedService(serviceReference, service);
        unregisterContentSources(serviceReference);
    }

    private void registerContentSources(ServiceReference serviceReference, DepartmentStore departmentStore) {
        List<FranchiseeContentSource> content = new ArrayList<FranchiseeContentSource>();
        List<Floor> floors = departmentStore.getFloors();
        for (Floor floor : floors) {
            List<Franchisee> franchisees = floor.getFranchisees();
            for (Franchisee franchisee : franchisees) {
                String destinationId = floor.getName() + ".franchisee";
                FranchiseeContentSource source = new FranchiseeContentSource(bundleContext, franchisee,
                                                                              "departmentstore"
                    );
                source.setDestination(destinationId);
                source.register();
                content.add(source);
            }
        }
        registrations.put(serviceReference, content);
    }

    private void unregisterContentSources(ServiceReference serviceReference) {
        for (FranchiseeContentSource registeration : registrations.get(serviceReference)) {
            registeration.dispose();
        }
        registrations.remove(serviceReference);
    }

}
