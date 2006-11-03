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
import java.util.Locale;
import java.util.Properties;
import org.osgi.service.cm.ManagedService;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import wicket.Component;

public abstract class DefaultContent<E extends Component>
    implements Content, ManagedService
{
    private BundleContext m_bundleContext;
    private Properties m_properties;
    private ServiceRegistration m_registration;

    protected DefaultContent( BundleContext bundleContext, String contentId, String applicationName )
    {
        m_properties = new Properties();
        m_properties.put( Constants.SERVICE_PID, CONTENTID + "/" + contentId );
        m_bundleContext = bundleContext;
        setContentId( contentId );
        setApplicationName( applicationName );
    }

    public final String getDestinationId()
    {
        return m_properties.getProperty( DESTINATIONID );
    }

    public final void setDestinationId( String destinationId )
    {
        m_properties.put( DESTINATIONID, destinationId );
    }

    public final Component createComponent( Locale locale )
    {
        String destinationId = getDestinationId();
        int pos = destinationId.lastIndexOf( '.' );
        String id = destinationId.substring( pos + 1 );
        return createComponent( id, locale );
    }

    public final String getContentId()
    {
        return m_properties.getProperty( CONTENTID );
    }

    private void setContentId( String contentId )
    {
        m_properties.put( CONTENTID, contentId );
    }

    public final String getApplicationName()
    {
        return m_properties.getProperty( APPLICATION_NAME );
    }

    public final void setApplicationName( String applicationName )
    {
        m_properties.put( APPLICATION_NAME, applicationName );
    }

    protected abstract E createComponent( String id, Locale locale );

    public final void updated( Dictionary config )
    {
        if( config == null )
        {
            m_registration.setProperties( m_properties );
            return;
        }
        String destinationId = (String) config.get( DESTINATIONID );
        setDestinationId( destinationId );
        String appName = (String) config.get( APPLICATION_NAME );
        setApplicationName( appName );
        m_registration.setProperties( config );
    }

    public final ServiceRegistration register()
    {
        String[] serviceNames = { Content.class.getName(), ManagedService.class.getName() };
        m_registration = m_bundleContext.registerService( serviceNames, this, m_properties );
        return m_registration;
    }
}
