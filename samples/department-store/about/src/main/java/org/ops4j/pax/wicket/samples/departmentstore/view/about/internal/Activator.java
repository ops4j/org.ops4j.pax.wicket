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
package org.ops4j.pax.wicket.samples.departmentstore.view.about.internal;

import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStoreModelTracker;
import org.ops4j.pax.wicket.service.PageContent;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import java.util.Properties;

public class Activator
    implements BundleActivator
{
    private DepartmentStoreModelTracker m_storeTracker;
    private ServiceRegistration m_serviceRegistration;

    public void start( BundleContext bundleContext )
        throws Exception
    {
        m_storeTracker = new DepartmentStoreModelTracker( bundleContext );
        m_storeTracker.open();
        AboutPageContent pageContent = new AboutPageContent( m_storeTracker );
        Properties props = new Properties();
        props.put( "pagename", "about" );
        m_serviceRegistration = bundleContext.registerService( PageContent.class.getName(), pageContent, props );
    }

    public void stop( BundleContext bundleContext )
        throws Exception
    {
        m_serviceRegistration.unregister();
        m_storeTracker.close();
    }
}
