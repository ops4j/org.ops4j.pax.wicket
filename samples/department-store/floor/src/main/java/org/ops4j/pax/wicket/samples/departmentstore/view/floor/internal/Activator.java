/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2006 Edward F. Yakop
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.samples.departmentstore.view.floor.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStore;
import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.api.ContentAggregator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class Activator
    implements BundleActivator
{

    private static Activator INSTANCE;

    private HashMap<String, FloorContentAggregator> m_containers;
    private List<ServiceRegistration> m_registrations;

    public Activator()
    {
        synchronized ( Activator.class )
        {
            INSTANCE = this;
        }
        m_containers = new HashMap<String, FloorContentAggregator>();
        m_registrations = new ArrayList<ServiceRegistration>();
    }

    public void start( BundleContext bundleContext )
        throws Exception
    {
        String depStoreServiceName = DepartmentStore.class.getName();
        ServiceReference depStoreServiceReference = bundleContext.getServiceReference( depStoreServiceName );
        DepartmentStore departmentStore = (DepartmentStore) bundleContext.getService( depStoreServiceReference );
        List<Floor> floors = departmentStore.getFloors();

        String destinationId = "swp.floor";
        for ( Floor floor : floors )
        {
            String floorName = floor.getName();
            FloorContentAggregator container = new FloorContentAggregator( floor, floorName, destinationId,
                bundleContext, "departmentstore" );
            m_containers.put( floorName, container );
            container.setDestinationId( destinationId );
            container.setContainmentId( floor.getName() );
            ServiceRegistration registration = container.register();
            m_registrations.add( registration );
        }
    }

    public void stop( BundleContext bundleContext )
        throws Exception
    {
        for ( ServiceRegistration registration : m_registrations )
        {
            registration.unregister();
        }
        m_registrations.clear();

        Collection<FloorContentAggregator> floorContainers = m_containers.values();
        for ( ContentAggregator floor : floorContainers )
        {
            floor.dispose();
        }
    }

    static final Activator getInstance()
    {
        synchronized ( Activator.class )
        {
            return INSTANCE;
        }
    }

    final FloorContentAggregator getFloorContentContainer( String containerId )
    {
        return m_containers.get( containerId );
    }
}
