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
package org.ops4j.pax.wicket.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import org.apache.log4j.Logger;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.internal.ContentTrackingCallback;
import org.ops4j.pax.wicket.internal.DefaultContentTracker;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.api.ContentAggregator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import wicket.Component;

public class RootContentAggregator
    implements ContentAggregator, ContentTrackingCallback, ManagedService
{
    protected final Logger m_logger = Logger.getLogger( RootContentAggregator.class );

    private Hashtable<String, String> m_properties;
    private BundleContext m_bundleContext;
    private HashMap<String, List<ContentSource>> m_children;
    private ServiceRegistration m_registration;
    private DefaultContentTracker m_contentTracker;

    public RootContentAggregator( BundleContext bundleContext, String containmentId, String applicationName )
    {
        m_bundleContext = bundleContext;
        m_properties = new Hashtable<String, String>();
        setContainmentId( containmentId );
        setApplicationName( applicationName );
        m_properties.put( Constants.SERVICE_PID, applicationName + "." + containmentId );
        m_children = new HashMap<String, List<ContentSource>>();
    }

    public final String getContainmentId()
    {
        synchronized ( this )
        {
            return m_properties.get( ContentSource.AGGREGATION_POINT );
        }
    }

    public final void setContainmentId( String containmentId )
    {
        synchronized ( this )
        {
            m_properties.put( ContentSource.AGGREGATION_POINT, containmentId );
        }
    }

    public final String getApplicationName()
    {
        return m_properties.get( ContentSource.APPLICATION_NAME );
    }

    public final void setApplicationName( String applicationName )
    {
        m_properties.put( ContentSource.APPLICATION_NAME, applicationName );
    }

    @SuppressWarnings("unchecked")
    public final <V extends Component, T extends Component> List<V> createComponents( String wicketId, T parent )
    {
        ArrayList<V> result = new ArrayList<V>();

        List<ContentSource<V>> contents = getContents( wicketId );
        if ( !contents.isEmpty() )
        {
            Locale locale = null;
            for ( ContentSource content : contents )
            {
                V component = (V) content.createComponent( null );

                if ( locale != null )
                {
                    locale = component.getLocale();
                }

                result.add( component );
            }

            Comparator<V> comparator = getComparator( wicketId, locale );
            if ( comparator != null )
            {
                Collections.sort( result, comparator );
            }
        }

        return result;
    }

    /**
     * Overrides this method to create a sorting mechanism for content with the specified {@code contentId}. Returns
     * {@code null} if the comparator is not defined. By default, this comparator returns {@code null}.
     *
     * @param contentId The content id. This argument must not be {@code null}.
     * @param locale The locale. This argument must not be {@code null}.
     *
     * @return The comparator for the specified {@code contentId}.
     *
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     *
     * @see ContentAggregator#createComponents(String, wicket.Component)
     * @since 1.0.0
     */
    public <V extends Component> Comparator<V> getComparator( String contentId, Locale locale )
        throws IllegalArgumentException
    {
        return null;
    }

    /**
     * Dispose this {@code RootContentAggregator} instance.
     * <p>
     * Note: Dispose does not unregister this {@code RootContentAggregator}, and ensure that dispose is only called
     * after this {@code RootContentAggregator} instance is unregistered from OSGi container.
     * </p>
     *
     * @throws IllegalStateException Thrown if this content tracker has not been registered.
     *
     * @see ServiceRegistration#unregister()
     *
     * @since 1.0.0
     */
    public final void dispose()
        throws IllegalStateException
    {
        synchronized ( this )
        {
            if ( m_contentTracker == null )
            {
                throw new IllegalStateException( "RootContentAggregator [" + this + "] has not been registered." );
            }

            m_contentTracker.close();
            m_contentTracker = null;
        }
    }

    /**
     * Add the specified {@code content} to this {@code RootContentAggregator} and mapped it as {@code wicketId}.
     *
     * @param wicketId The wicket id. This argument must not be {@code null} or empty.
     * @param content The content. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     * @since 1.0.0
     */
    public final void addContent( String wicketId, ContentSource content )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( wicketId, "wicketId" );
        NullArgumentException.validateNotNull( content, "content" );

        synchronized ( this )
        {
            List<ContentSource> contents = m_children.get( wicketId );
            if ( contents == null )
            {
                contents = new ArrayList<ContentSource>();
                m_children.put( wicketId, contents );
            }

            contents.add( content );
        }
    }

    /**
     * Remove the specified {@code content} to this {@code RootContentAggregator} and unmapped it as {@code wicketId}.
     *
     * @param wicketId The wicket id. This argument must not be {@code null} or empty.
     * @param content The content. This argument must not be {@code null}.
     *
     * @return A {@code boolean} indicator whether removal is successfull.
     *
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     * @since 1.0.0
     */
    public final boolean removeContent( String wicketId, ContentSource content )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( wicketId, "wicketId" );
        NullArgumentException.validateNotNull( content, "content" );

        synchronized ( this )
        {
            List<ContentSource> contents = m_children.get( wicketId );
            if ( contents == null )
            {
                return false;
            }
            contents.remove( content );
            if ( contents.isEmpty() )
            {
                return m_children.remove( wicketId ) != null;
            }

            return false;
        }
    }

    /**
     * Returns list of {@code ContentSource} instnaces of the specified {@code wicketId}. Returns an empty list if there is
     * no content for the specified {@code wicketId}.
     *
     * @param wicketId The wicket id. This argument must not be {@code null} or empty.
     *
     * @return List of {@code ContentSource} of the specified {@code wicketId}.
     *
     * @throws NullArgumentException If the wicketId is null.
     */
    @SuppressWarnings("unchecked")
    public final <V extends ContentSource> List<V> getContents( String wicketId )
        throws NullArgumentException
    {
        NullArgumentException.validateNotEmpty( wicketId, "wicketId" );

        List<V> contents;
        synchronized ( this )
        {
            contents = (List<V>) m_children.get( wicketId );
        }

        if ( contents != null )
        {
            contents = new ArrayList<V>( contents );
        }
        else
        {
            contents = new ArrayList<V>();
        }

        return contents;
    }

    public final ServiceRegistration register()
        throws IllegalStateException
    {
        synchronized ( this )
        {
            if ( m_contentTracker != null )
            {
                throw new IllegalStateException( "RootContentAggregator [" + this + "] has already been registered." );
            }

            String applicationName = getApplicationName();
            String containmentId = getContainmentId();
            m_contentTracker = new DefaultContentTracker( m_bundleContext, this, applicationName, containmentId );
            m_contentTracker.open();

            String[] serviceNames =
            {
                ContentAggregator.class.getName(), ManagedService.class.getName()
            };
            m_registration = m_bundleContext.registerService( serviceNames, this, m_properties );

            return m_registration;
        }
    }

    public void updated( Dictionary config )
        throws ConfigurationException
    {
        if ( config == null )
        {
            synchronized ( this )
            {
                m_registration.setProperties( m_properties );
            }

            return;
        }

        String newContainmentId = (String) config.get( ContentSource.AGGREGATION_POINT );
        String existingContainmentId = getContainmentId();
        if ( existingContainmentId != null && existingContainmentId.equals( newContainmentId ) )
        {
            return;
        }

        synchronized ( this )
        {
            m_children.clear();
        }

        setContainmentId( newContainmentId );
        if ( newContainmentId != null )
        {
            try
            {
                String tServiceClassName = ContentSource.class.getName();
                ServiceReference[] services = m_bundleContext.getServiceReferences( tServiceClassName, null );
                if ( services == null )
                {
                    return;
                }
                for ( ServiceReference service : services )
                {
                    m_contentTracker.addingService( service );
                }
            }
            catch ( InvalidSyntaxException e )
            {
                // Can not happen. Right!
                e.printStackTrace();
            }
        }

        m_registration.setProperties( config );
    }

    @Override
    protected void finalize()
        throws Throwable
    {
        synchronized ( this )
        {
            if ( m_contentTracker != null )
            {
                m_logger.warn( "RootContentAggregator [" + this + "] is not disposed." );
            }
            dispose();
        }
        super.finalize();
    }
}
