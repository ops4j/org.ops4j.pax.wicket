/*
 * Copyright 2006 Niclas Hedhman.
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
import java.util.List;
import java.util.Properties;
import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStore;
import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.service.Content;
import org.ops4j.pax.wicket.service.ContentContainer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class Activator
    implements BundleActivator
{

    private List<FloorContentContainer> m_containers;
    private List<ServiceRegistration> m_registrations;

    public Activator()
    {
        m_containers = new ArrayList<FloorContentContainer>();
        m_registrations = new ArrayList<ServiceRegistration>();
    }

    public void start( BundleContext bundleContext )
        throws Exception
    {
        String depStoreServiceName = DepartmentStore.class.getName();
        ServiceReference depStoreServiceReference = bundleContext.getServiceReference( depStoreServiceName );
        DepartmentStore departmentStore = (DepartmentStore) bundleContext.getService( depStoreServiceReference );
        List<Floor> floors = departmentStore.getFloors();

        String[] serviceNames = { Content.class.getName(), ContentContainer.class.getName() };
        String destinationId = "swp.floor";
        for( Floor floor : floors )
        {
            FloorContentContainer container =
                new FloorContentContainer( floor, floor.getName(), destinationId, bundleContext );
            m_containers.add( container );

            Properties properties = new Properties();
            properties.put( Content.CONFIG_DESTINATIONID, destinationId );
            properties.put( ContentContainer.CONFIG_CONTAINMENTID, floor.getName() );
            ServiceRegistration registration = bundleContext.registerService( serviceNames, container, properties );
            m_registrations.add( registration );
        }
    }

    public void stop( BundleContext bundleContext )
        throws Exception
    {
        for( ServiceRegistration registration : m_registrations )
        {
            registration.unregister();
        }
        m_registrations.clear();
        for( ContentContainer floor : m_containers )
        {
            floor.dispose();
        }
    }
}
