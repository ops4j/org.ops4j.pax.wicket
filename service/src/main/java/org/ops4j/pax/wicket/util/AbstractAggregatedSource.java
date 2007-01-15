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
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.ContentAggregator;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.internal.ContentTrackingCallback;
import org.ops4j.pax.wicket.internal.DefaultContentTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import wicket.Component;

/**
 * This is a convenient superclass for creation of a ContentAggregator.
 * <p>
 * Normal use only requires the overriding of a single abstract method;
 * <code><pre>
 *     protected abstract <T extends Component> E createComponent( String contentId, T parent )
 *         throws IllegalArgumentException;
 * </pre></code>
 * </p>
 * <p>
 * The subclass looks something like this;
 * <code><pre>
 * public class PrinterContentAggregator<E extends Compontnet> extends AbstractContentAggregator
 * {
 *     public PrinterContentAggregator( BundleContext context, String applicationName,
 *                                      String aggregationPoint, String destination )
 *         throws IllegalArgumentException
 *     {
 *         super( context, applicationName, aggregationPoint, destination );
 *     }
 *
 *     protected abstract <T extends Component> E createComponent( String contentId, T parent )
 *         throws IllegalArgumentException
 *     {
 *         return new PrintersPanel( contentId, this );
 *     }
 * }
 * </pre></code>
 *
 * And the MyPanel handles the generation of the Wicket Panel instance. Example;
 *
 * <code><pre>
 * public class PrintersPanel extends Panel
 * {
 *
 *     private static final long serialVersionUID = 1L;
 *
 *     public static final String WICKET_ID_NAME_LABEL = "name";
 *     private static final String WICKET_ID_PRINTER = "printer";
 *     private static final String WICKET_ID_PRINTERS = "printers";
 *
 *     FloorPanel( String id, ContentAggregator container, Floor floor )
 *     {
 *          super( id, new Model( "printers" ) );
 *
 *          ListView listView = new ListView( WICKET_ID_PRINTERS )
 *          {
 *              private static final long serialVersionUID = 1L;
 *
 *              protected void populateItem( final ListItem item )
 *              {
 *                  Component modelObject = (Component) item.getModelObject();
 *                  item.add( modelObject );
 *              }
 *         };
 *
 *         List&lt;Component&gt; printers = container.createComponents( WICKET_ID_PRINTER, listView );
 *         if ( printers.isEmpty() )
 *         {
 *             Label tLabel = new Label( WICKET_ID_PRINTER, "No Printers are available at the moment." );
 *             printers.add( tLabel );
 *         }
 *         Model listViewModel = new Model( (Serializable) printers );
 *         listView.setModel( listViewModel );
 *         add( listView );
 *     }
 * }
 * </pre></code>
 * In the above example, we are wiring printer <i>ContentSource</i>s to a printers panel. The rendering
 * of each Printer view is up to the wired printer, and the aggregator just need a little bit of html
 * to define its own panel. Something like this;
 * <code><pre>
 *   &lt;html xmlns="http://www.w3.org/1999/xhtml"
 *            xmlns:wicket="http://wicket.sourceforge.net/"
 *            xml:lang="en"
 *            lang="en"
 *   &gt;
 *       &lt;body&gt;
 *           &lt;wicket:panel&gt;
 *               &lt;div class="printerspanel"&gt;
 *                   &lt;div class="printer-row" wicket:id="printers"&gt;
 *                       &lt;span class="printer-item" wicket:id="printer"&gt;&lt;/span&gt;
 *                   &lt;/div&gt;
 *               &lt;/div&gt;
 *           &lt;/wicket:panel&gt;
 *       &lt;/body&gt;
 *   &lt;/html&gt;
 * </pre></code>
 * </p>
 */
public abstract class AbstractAggregatedSource<E extends Component>
    implements ContentAggregator, ContentSource<E>, ContentTrackingCallback, ManagedService
{

    protected final Logger m_logger = Logger.getLogger( getClass() );

    private Properties m_properties;
    private HashMap<String, List<ContentSource>> m_children;
    private BundleContext m_bundleContext;
    private DefaultContentTracker m_contentTracker;
    private ServiceRegistration m_registration;

    /**
     * Construct an instance of {@code AbstractContentAggregator} with the specified arguments.
     *
     * @param bundleContext    The bundle context. This argument must not be {@code null}.
     * @param applicationName  The application name. This argument must not be {@code null} or empty.
     * @param aggregationPoint The containment id. This argument must not be {@code null} or empty.
     * @param destination      The destination id. This argument must not be {@code null} or empty.
     *
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null} or empty.
     * @since 1.0.0
     */
    protected AbstractAggregatedSource( BundleContext bundleContext, String applicationName, String aggregationPoint,
                                         String destination )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( bundleContext, "bundleContext" );
        NullArgumentException.validateNotEmpty( applicationName, "applicationName" );
        NullArgumentException.validateNotEmpty( aggregationPoint, "aggregationPoint" );
        NullArgumentException.validateNotEmpty( destination, "destination" );

        m_bundleContext = bundleContext;
        m_children = new HashMap<String, List<ContentSource>>();
        m_properties = new Properties();

        setAggregationId( aggregationPoint );
        setDestinationId( destination );
        setApplicationName( applicationName );

        m_properties.put( Constants.SERVICE_PID, applicationName + "." + aggregationPoint );
    }

    /**
     * Returns the application name of this {@code AbstractContentAggregator} instance belongs to.
     *
     * @return The application name of this {@code AbstractContentAggregator} instance belongs to.
     *
     * @since 1.0.0
     */
    public String getApplicationName()
    {
        synchronized( this )
        {
            return m_properties.getProperty( ContentSource.APPLICATION_NAME );
        }
    }

    /**
     * Sets the application name of this {@code AbstractContentAggregator} instant belongs to.
     * <p>
     * Note: Application name property must not be set after this {@code AbstractContentAggregator} instance is registered
     * to OSGi framework.
     * </p>
     *
     * @param applicationName The application name. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code applicationName} argument is {@code null} or
     *                                  empty.
     * @since 1.0.0
     */
    public final void setApplicationName( String applicationName )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( applicationName, "applicationName" );

        synchronized( this )
        {
            m_properties.put( ContentSource.APPLICATION_NAME, applicationName );
        }
    }

    public final void dispose()
    {
        synchronized( this )
        {
            m_contentTracker.close();
            onDispose();
        }
    }

    /**
     * Override this method to handle additional dispose.
     *
     * @since 1.0.0
     */
    protected void onDispose()
    {
    }

    /**
     * Returns the containment id of this {@code AbstractContentAggregator} instance. This method must not return
     * {@code null} object.
     *
     * @since 1.0.0
     */
    public final String getContainmentId()
    {
        synchronized( this )
        {
            return m_properties.getProperty( AGGREGATION_POINT );
        }
    }

    /**
     * Set the containment id of this {@code AbstractContentAggregator}.
     * <p>
     * Note: Containment id property must not be set after this {@code AbstractContentAggregator} instance is registered
     * to OSGi framework.
     * </p>
     *
     * @param containmentId The containment id. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code containmentId} argument is {@code null} or empty.
     * @since 1.0.0
     */
    public final void setAggregationId( String containmentId )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( containmentId, "containmentId" );

        synchronized( this )
        {
            m_properties.put( AGGREGATION_POINT, containmentId );
        }
    }

    /**
     * Create the wicket component represented by this {@code ContentSource} instance. This method must not return
     * {@code null} object.
     *
     * @param parent The parent of created components. This argument must not be {@code null}.
     *
     * @return The wicket component represented by this {@code ContentSource} instance.
     *
     * @since 1.0.0
     */
    @SuppressWarnings( "unchecked" )
    public final <V extends Component, T extends Component> List<V> createComponents( String contentId, T parent )
    {
        ArrayList<V> result = new ArrayList<V>();

        List<ContentSource> contents = getContents( contentId );
        if( !contents.isEmpty() )
        {
            Locale locale = null;
            for( ContentSource source : contents )
            {
                V component = (V) source.createComponent( parent );

                if( locale == null )
                {
                    locale = component.getLocale();
                }
                result.add( component );
            }

            Comparator<V> comparator = getComparator( contentId, locale );
            if( comparator != null )
            {
                Collections.sort( result, comparator );
            }
        }

        return result;
    }

    /**
     * Returns list of {@code ContentSource} instnaces of the specified {@code contentId}. Returns an empty list if there is
     * no content for the specified {@code contentId}.
     *
     * @param contentId The wicket id. This argument must not be {@code null} or empty.
     *
     * @return List of {@code ContentSource} of the specified {@code contentId}.
     *
     * @throws IllegalArgumentException if the contentId is null or empty.
     */
    @SuppressWarnings( "unchecked" )
    public final <V extends ContentSource> List<V> getContents( String contentId )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( contentId, "contentId" );

        List<V> contents;
        synchronized( this )
        {
            contents = (List<V>) m_children.get( contentId );
        }

        if( contents != null )
        {
            contents = new ArrayList<V>( contents );
        }
        else
        {
            contents = new ArrayList<V>();
        }

        return contents;
    }

    /**
     * Overrides this method to create a sorting mechanism for content with the specified {@code contentId}. Returns
     * {@code null} if the comparator is not defined. By default, this comparator returns {@code null}.
     *
     * @param contentId The content id. This argument must not be {@code null}.
     * @param locale    The current active locale. This argument must not be {@code null}.
     *
     * @return The comparator for the specified {@code contentId}.
     *
     * @see ContentAggregator#createComponents(String,wicket.Component)
     */
    public <V extends Component> Comparator<V> getComparator( String contentId, Locale locale )
        throws IllegalArgumentException
    {
        return null;
    }

    public final void updated( Dictionary config )
        throws ConfigurationException
    {
        if( config == null )
        {
            m_registration.setProperties( m_properties );

            return;
        }

        String existingContainmentId = getContainmentId();
        String newContainmentId = (String) config.get( AGGREGATION_POINT );
        if( newContainmentId == null )
        {
            throw new ConfigurationException( AGGREGATION_POINT, "This property must not be [null]." );
        }

        String newApplicationName = (String) config.get( APPLICATION_NAME );
        if( newApplicationName == null )
        {
            throw new ConfigurationException( APPLICATION_NAME, "This property must not be [null]." );
        }

        String existingApplicationName = getApplicationName();
        if( existingContainmentId.equals( newContainmentId ) && existingApplicationName.equals( newApplicationName ) )
        {
            return;
        }

        m_children.clear();
        setApplicationName( newApplicationName );
        setAggregationId( newContainmentId );

        String filter = "(&(" + ContentSource.APPLICATION_NAME + "=" + getApplicationName() + ")" + "("
                        + ContentSource.DESTINATION + "=" + getContainmentId() + ".*)" + ")";

        try
        {
            ServiceReference[] services = m_bundleContext.getServiceReferences( ContentSource.class.getName(), filter );
            if( null == services )
            {
                return;
            }

            for( ServiceReference service : services )
            {
                m_contentTracker.addingService( service );
            }
        }
        catch( InvalidSyntaxException e )
        {
            // Can not happen. Right!
            m_logger.warn( "Invalid filter [" + filter + "]. This probably caused by either the [application name] "
                           + "or the containement id] contains osgi filter keywords.", e
            );
        }

        m_registration.setProperties( config );
    }

    public final void addContent( String id, ContentSource content )
    {
        synchronized( this )
        {
            List<ContentSource> contents = m_children.get( id );
            if( contents == null )
            {
                contents = new ArrayList<ContentSource>();
                m_children.put( id, contents );
            }
            contents.add( content );
        }
    }

    public final boolean removeContent( String id, ContentSource content )
    {
        synchronized( this )
        {
            List<ContentSource> contents = m_children.get( id );
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
    }

    /**
     * Returns the destination id of this {@code AbstractContentAggregator} instance. This method must not return
     * {@code null} object.
     *
     * @since 1.0.0
     */
    public final String getDestinationId()
    {
        synchronized( this )
        {
            return m_properties.getProperty( ContentSource.DESTINATION );
        }
    }

    /**
     * Set the destination id of this {@code AbstractContentAggregator}.
     * <p>
     * Note: Destination id property must not be set after this {@code AbstractContentAggregator} instance is registered
     * to OSGi framework.
     * </p>
     *
     * @param destinationId The destination id. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code destinationId} argument is {@code null} or empty.
     * @since 1.0.0
     */
    public final void setDestinationId( String destinationId )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( destinationId, "destinationId" );

        synchronized( this )
        {
            m_properties.put( ContentSource.DESTINATION, destinationId );
        }
    }

    public final ServiceRegistration register()
    {
        synchronized( this )
        {
            String applicationName = getApplicationName();
            String containmentId = getContainmentId();
            m_contentTracker = new DefaultContentTracker( m_bundleContext, this, applicationName, containmentId );
            m_contentTracker.open();

            String[] serviceNames =
                {
                    ContentSource.class.getName(), ContentAggregator.class.getName(), ManagedService.class.getName()
                };
            m_registration = m_bundleContext.registerService( serviceNames, this, m_properties );

            return m_registration;
        }
    }

    /**
     * Create the wicket component represented by this {@code ContentSource} instance. This method must not return
     * {@code null} object.
     * <p>
     * General convention:<br/>
     * <ul>
     * <li>In the use case of Wicket 1 environment. The callee of this method responsibles to add the component created
     * this method;</li>
     * <li>In the use case of Wicket 2 environment. The parent is passed through constructor during creational of the
     * component created by this method.</li>
     * </ul>
     * </p>
     *
     * @param parent The parent component of the component to be created by this method. This argument must not be
     *               {@code null}.
     *
     * @return The wicket component represented by this {@code ContentSource} instance.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code parent} arguement is {@code null}.
     * @since 1.0.0
     */
    public final <T extends Component> E createComponent( T parent )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( parent, "parent" );

        String destinationId = getDestinationId();
        int pos = destinationId.lastIndexOf( '.' );
        String id = destinationId.substring( pos + 1 );
        return createComponent( id, parent );
    }

    /**
     * Create component represented by this {@code AbstractContentAggregator} with the specified {@code contentId} and
     * {@code parent}.
     *
     * <p>
     * General convention:<br/>
     * <ul>
     * <li>In the use case of Wicket 1 environment. The callee of this method responsibles to add the component created
     * this method;</li>
     * <li>In the use case of Wicket 2 environment. The parent is passed through constructor during creational of the
     * component created by this method.</li>
     * </ul>
     * </p>
     *
     * @param contentId The contentId. This argument must not be {@code null} nor empty. It maps to the wicket:id of the
     *                  rendering process in Wicket.
     * @param parent    The parent component. This argument must not be {@code null}.
     *
     * @return A new instance of wicket component represented by this {@code AbstractContentAggregator}.
     *
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     * @since 1.0.0
     */
    protected abstract <T extends Component> E createComponent( String contentId, T parent )
        throws IllegalArgumentException;
}
