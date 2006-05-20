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
import java.util.List;
import java.util.Properties;
import org.ops4j.pax.wicket.service.internal.ContentTrackingCallback;
import org.ops4j.pax.wicket.service.internal.DefaultContentTracking;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.osgi.util.tracker.ServiceTracker;
import wicket.Component;

public abstract class DefaultContentContainer
    implements ContentContainer, Content, ContentTrackingCallback, ManagedService
{

    private String m_containmentId;
    private HashMap<String, List<Content>> m_children;
    private ServiceTracker m_serviceTracker;
    private String m_destinationId;
    private BundleContext m_bundleContext;
    private DefaultContentTracking m_contentTracking;
    private ServiceRegistration m_registration;

    protected DefaultContentContainer( String containmentId, String destinationId, BundleContext bundleContext )
    {
        m_bundleContext = bundleContext;
        m_children = new HashMap<String, List<Content>>();
        m_destinationId = destinationId;
        m_containmentId = containmentId;
        m_contentTracking = new DefaultContentTracking( bundleContext, this );
        m_contentTracking.setContainmentId( m_containmentId );
        m_serviceTracker = new ServiceTracker( bundleContext, Content.class.getName(), m_contentTracking );
        m_serviceTracker.open();
    }

    public final void dispose()
    {
        m_serviceTracker.close();
    }

    public final String getContainmentID()
    {
        return m_containmentId;
    }

    public final void setContainmentId( String containmentId )
    {
        m_containmentId = containmentId;
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

    public final void updated( Dictionary config )
    {
        if( config == null )
        {
            return;
        }
        m_registration.setProperties( config );
        m_destinationId = (String) config.get( CONFIG_DESTINATIONID );
        String newContainmentId = (String) config.get( CONFIG_CONTAINMENTID );
        if( m_containmentId != null && m_containmentId.equals( newContainmentId ) )
        {
            return;
        }
        m_children.clear();
        m_containmentId = newContainmentId;
        if( m_containmentId != null )
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
                    m_contentTracking.addingService( service );
                }
            } catch( InvalidSyntaxException e )
            {
                // Can not happen. Right!
                e.printStackTrace();
            }
        }
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

    public final String getDestinationId()
    {
        return m_destinationId;
    }

    public final void setDestinationId( String destinationId )
    {
        m_destinationId = destinationId;
    }

    public final ServiceRegistration register()
    {
        String[] serviceNames =
            {
                Content.class.getName(), ContentContainer.class.getName(), ManagedService.class.getName()
            };
        Properties properties = new Properties();
        properties.put( Content.CONFIG_DESTINATIONID, m_destinationId );
        properties.put( ContentContainer.CONFIG_CONTAINMENTID, m_containmentId );
        properties.put( Constants.SERVICE_PID, m_containmentId );
        m_registration = m_bundleContext.registerService( serviceNames, this, properties );
        return m_registration;
    }

    public final Component createComponent()
    {
        int pos = m_destinationId.lastIndexOf( '.' );
        String id = m_destinationId.substring( pos + 1 );
        return createComponent( id );
    }

    protected abstract Component createComponent( String id );

    /**
     * Note: What is the behavior of removing component? Does this mean, when the component service is added/removed it
     * should not be added again?
     */
    protected abstract void removeComponent( Component component );

}

