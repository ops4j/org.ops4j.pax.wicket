/*
 * Copyright 2005 Niclas Hedhman.
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
package org.ops4j.pax.wicket.sample.page2;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.wicket.sample.page2.internal.WorldTimePage;
import org.ops4j.pax.servicemanager.ServiceManager;
import org.ops4j.pax.servicemanager.ServiceManagerImpl;

public class Activator
    implements BundleActivator
{

    private WorldTimePage m_Page1;

    public void start( BundleContext bundleContext )
        throws Exception
    {
        ServiceManager man = new ServiceManagerImpl( bundleContext );

        m_Page1 = new WorldTimePage( man );
        bundleContext.registerService( "wicket.markup.html.WebPage", m_Page1, null );
    }

    public void stop( BundleContext bundleContext )
        throws Exception
    {

    }
}
