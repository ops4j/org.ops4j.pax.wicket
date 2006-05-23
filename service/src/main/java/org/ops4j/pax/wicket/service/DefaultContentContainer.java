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
import org.ops4j.pax.wicket.service.internal.TrackingUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Filter;
import org.osgi.service.cm.ManagedService;
import org.osgi.util.tracker.ServiceTracker;
import wicket.Component;

public abstract class DefaultContentContainer
    implements ContentContainer, Content, ContentTrackingCallback, ManagedService
{
    private Hashtable<String, String> m_properties;
    private HashMap<String, List<Content>> m_children;
    private BundleContext m_bundleContext;
    private ServiceTracker m_contentTracker;
    private ServiceRegistration m_registration;

    protected DefaultContentContainer( BundleContext bundleContext, String applicationName, String containmentId,
                                       String destinationId
    )
    {
        m_bundleContext = bundleContext;
        m_children = new HashMap<String, List<Content>>();
        m_properties = new Hashtable<String, String>();
        setContainmentId( containmentId );
        setDestinationId( destinationId );
        setApplicationName(applicationName);
        m_properties.put( Constants.SERVICE_PID, applicationName + "." + containmentId );
    }

    public String getApplicationName()
    {
        return m_properties.get( Content.APPLICATION_NAME );
    }

    public void setApplicationName( String applicationName )
    {
        m_properties.put( Content.APPLICATION_NAME, applicationName );
    }

    public final void dispose()
    {
        m_contentTracker.close();
    }

    public final String getContainmentId()
    {
        return m_properties.get( ContentContainer.CONFIG_CONTAINMENTID );
    }

    public final void setContainmentId( String containmentId )
    {
        m_properties.put( ContentContainer.CONFIG_CONTAINMENTID, containmentId );
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
        String existingContainmentId = getContainmentId();
        String newContainmentId = (String) config.get( CONFIG_CONTAINMENTID );
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
        m_registration.setProperties( config );
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
        return m_properties.get( Content.CONFIG_DESTINATIONID );
    }

    public final void setDestinationId( String destinationId )
    {
        m_properties.put( Content.CONFIG_DESTINATIONID, destinationId );
    }

    public final ServiceRegistration register()
    {
        DefaultContentTracker customizer = new DefaultContentTracker( m_bundleContext, this );
        customizer.setContainmentId( getContainmentId() );
        Filter filter = TrackingUtil.createContentFilter( m_bundleContext, getApplicationName() );
        m_contentTracker = new ServiceTracker( m_bundleContext, filter, customizer );
        m_contentTracker.open();

        String[] serviceNames =
            {
                Content.class.getName(), ContentContainer.class.getName(), ManagedService.class.getName()
            };
        m_registration = m_bundleContext.registerService( serviceNames, this, m_properties );
        return m_registration;
    }

    public final Component createComponent()
    {
        String destinationId = getDestinationId();
        int pos = destinationId.lastIndexOf( '.' );
        String id = destinationId.substring( pos + 1 );
        return createComponent( id );
    }

    protected abstract Component createComponent( String id );

    /**
     * Note: What is the behavior of removing component? Does this mean, when the component service is added/removed it
     * should not be added again?
     */
    protected abstract void removeComponent( Component component );

}

