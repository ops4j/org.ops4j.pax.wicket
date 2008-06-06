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

import java.util.Properties;
import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;
import static org.ops4j.pax.wicket.api.ContentSource.PAGE_NAME;
import org.ops4j.pax.wicket.api.PageFactory;
import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStoreModelTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator
    implements BundleActivator
{

    private DepartmentStoreModelTracker storeTracker;
    private ServiceRegistration registration;

    public void start( BundleContext aContext )
        throws Exception
    {
        storeTracker = new DepartmentStoreModelTracker( aContext );
        storeTracker.open();

        AboutPageFactory pageFactory = new AboutPageFactory( storeTracker, aContext );
        Properties props = new Properties();
        props.put( PAGE_NAME, "about" );
        props.put( APPLICATION_NAME, "departmentstore" );
        registration = aContext.registerService( PageFactory.class.getName(), pageFactory, props );
    }

    public void stop( BundleContext aContext )
        throws Exception
    {
        registration.unregister();
        storeTracker.close();

        storeTracker = null;
        registration = null;
    }
}
