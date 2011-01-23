/**
 *
 */
package org.ops4j.pax.wicket.samples.departmentstore.view.franchisee.internal;

import java.util.ArrayList;
import java.util.List;
import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStore;
import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.samples.departmentstore.model.Franchisee;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class FranchiseeDepartmentStoreModelTracker extends ServiceTracker
{

    private final BundleContext m_bundleContext;

    private List<ServiceRegistration> m_registrations;

    public FranchiseeDepartmentStoreModelTracker( BundleContext bundleContext )
    {
        super( bundleContext, DepartmentStore.class.getName(), null );
        this.m_bundleContext = bundleContext;
        m_registrations = new ArrayList<ServiceRegistration>();
    }

    @Override
    public Object addingService( ServiceReference serviceReference )
    {
        DepartmentStore departmentStore = (DepartmentStore) super.addingService( serviceReference );
        registerContentSources( departmentStore );
        return departmentStore;
    }

    @Override
    public void removedService( ServiceReference serviceReference, Object service )
    {
        super.removedService( serviceReference, service );
        unregisterContentSources();
    }

    private void registerContentSources( DepartmentStore departmentStore )
    {
        List<Floor> floors = departmentStore.getFloors();
        for( Floor floor : floors )
        {
            List<Franchisee> franchisees = floor.getFranchisees();
            for( Franchisee franchisee : franchisees )
            {
                String destinationId = floor.getName() + ".franchisee";
                FranchiseeContentSource source = new FranchiseeContentSource( m_bundleContext, franchisee,
                                                                              "departmentstore"
                );
                source.setDestination( destinationId );
                ServiceRegistration registration = source.register();
                m_registrations.add( registration );
            }
        }
    }

    private void unregisterContentSources()
    {
        for( ServiceRegistration registeration : m_registrations )
        {
            registeration.unregister();
        }
        m_registrations.clear();
    }

}