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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import org.ops4j.pax.wicket.service.internal.ContentTrackingCallback;
import org.ops4j.pax.wicket.service.internal.DefaultContentTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import wicket.Component;

public class DefaultPageContainer
    implements ContentContainer, ContentTrackingCallback, ManagedService
{
    private Hashtable<String, String> m_properties;
    private BundleContext m_bundleContext;
    private HashMap<String, List<Content>> m_children;
    private ServiceRegistration m_registration;
    private DefaultContentTracker m_contentTracker;

    public DefaultPageContainer( BundleContext bundleContext, String containmentId, String applicationName )
    {
        m_bundleContext = bundleContext;
        m_properties = new Hashtable<String, String>();
        setContainmentId( containmentId );
        setApplicationName( applicationName );
        m_properties.put( Constants.SERVICE_PID, applicationName + "." + containmentId );
        m_children = new HashMap<String, List<Content>>();
    }

    public final String getContainmentId()
    {
        return m_properties.get( Content.CONTAINMENTID );
    }

    public final void setContainmentId( String containmentId )
    {
        m_properties.put( Content.CONTAINMENTID, containmentId );
    }


    public String getApplicationName()
    {
        return m_properties.get( Content.APPLICATION_NAME );
    }

    public void setApplicationName( String applicationName )
    {
        m_properties.put( Content.APPLICATION_NAME, applicationName );
    }

    public final List<Component> createComponents( String id )
    {
        ArrayList<Component> result = new ArrayList<Component>();
        List<Content> contents = m_children.get( id );
        if( contents != null )
        {
            for( Content content : contents )
            {
                result.add( content.createComponent() );
            }
        }
        return result;
    }

    public final void dispose()
    {
        m_contentTracker.close();
    }

    public final void addContent( String id, Content content )
    {
        List<Content> contents = m_children.get( id );
        if( contents == null )
        {
            contents = new ArrayList<Content>();
            m_children.put( id, contents );
        }
        contents.add( content );
    }

    public final boolean removeContent( String id, Content content )
    {
        List<Content> contents = m_children.get( id );
        if( contents == null )
        {
            return false;
        }
        contents.remove( content );
        if( contents.isEmpty() )
        {
            return m_children.remove( id ) != null;
        }
        return false;
    }

    public final ServiceRegistration register()
    {
        m_contentTracker = new DefaultContentTracker( m_bundleContext, this, getApplicationName() );
        m_contentTracker.setContainmentId( getContainmentId() );
        m_contentTracker.open();

        String[] serviceNames =
            {
                ContentContainer.class.getName(), ManagedService.class.getName()
            };
        m_registration = m_bundleContext.registerService( serviceNames, this, m_properties );
        return m_registration;
    }

    public void updated( Dictionary config )
        throws ConfigurationException
    {
        if( config == null )
        {
            m_registration.setProperties( m_properties );
            return;
        }
        m_registration.setProperties( config );
        String existingContainmentId = getContainmentId();
        String newContainmentId = (String) config.get( Content.CONTAINMENTID );
        if( existingContainmentId != null && existingContainmentId.equals( newContainmentId ) )
        {
            return;
        }
        m_children.clear();
        setContainmentId( newContainmentId );
        if( newContainmentId != null )
        {
            try
            {
                ServiceReference[] services = m_bundleContext.getServiceReferences( Content.class.getName(), null );
                if( null == services )
                {
                    return;
                }
                for( ServiceReference service : services )
                {
                    m_contentTracker.addingService( service );
                }
            } catch( InvalidSyntaxException e )
            {
                // Can not happen. Right!
                e.printStackTrace();
            }
        }
    }
}
