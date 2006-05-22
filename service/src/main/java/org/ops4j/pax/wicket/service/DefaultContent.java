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
package org.ops4j.pax.wicket.service;

import java.util.Dictionary;
import java.util.Properties;
import org.osgi.service.cm.ManagedService;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import wicket.Component;

public abstract class DefaultContent
    implements Content, ManagedService
{
    private BundleContext m_bundleContext;
    private String m_contentId;
    private String m_applicationName;
    private String m_destinationId;
    private ServiceRegistration m_registration;

    protected DefaultContent( BundleContext bundleContext, String contentId, String applicationName )
    {
        m_bundleContext = bundleContext;
        m_contentId = contentId;
        m_applicationName = applicationName;
    }

    public final String getDestinationId()
    {
        return m_destinationId;
    }

    public final void setDestinationId( String destinationId )
    {
        m_destinationId = destinationId;
    }

    public final Component createComponent()
    {
        int pos = m_destinationId.lastIndexOf( '.' );
        String id = m_destinationId.substring( pos + 1 );
        return createComponent( id );
    }

    protected abstract Component createComponent( String id );

    public final void updated( Dictionary config )
    {
        m_registration.setProperties( config );
        m_destinationId = (String) config.get( CONFIG_DESTINATIONID );
    }

    public final ServiceRegistration register()
    {
        String[] serviceNames = { Content.class.getName(), ManagedService.class.getName() };
        Properties properties = new Properties();
        properties.put( Constants.SERVICE_PID, m_applicationName + "." + m_contentId );
        properties.put( Content.APPLICATION_NAME, m_applicationName );
        properties.put( Content.CONFIG_DESTINATIONID, m_destinationId );
        m_registration = m_bundleContext.registerService( serviceNames, this, properties );
        return m_registration;
    }

}
