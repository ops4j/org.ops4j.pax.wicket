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

import org.apache.log4j.Logger;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.ContentAggregator;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.internal.BaseAggregator;
import org.ops4j.pax.wicket.internal.ContentTrackingCallback;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ManagedService;
import wicket.Component;
import wicket.MarkupContainer;
import wicket.authorization.strategies.role.Roles;
import java.util.List;
import java.util.ArrayList;

/**
 * This is a convenient superclass for creation of a ContentAggregator.
 * <p>
 * Normal use only requires the overriding of a single abstract method; <code><pre>
 * protected abstract &lt;T extends Component&gt; E createSourceComponent( String wicketId )
 *     throws IllegalArgumentException;
 * </pre></code>
 * </p>
 * <p>
 * The subclass looks something like this; <code><pre>
 *         public class PrinterContentAggregator&lt;E extends Component&gt; extends AbstractContentAggregator
 *         {
 *             public PrinterContentAggregator( BundleContext context, String applicationName,
 *                                              String aggregationPoint, String destination )
 *                 throws IllegalArgumentException
 *             {
 *                 super( context, applicationName, aggregationPoint, destination );
 *             }
 *             protected abstract &lt;T extends Component&gt; E createSourceComponent( String wicketId )
 *                 throws IllegalArgumentException
 *             {
 *                 return new PrintersPanel( wicketId, this );
 *             }
 *         }
 * </pre></code>
 *
 * And the MyPanel handles the generation of the Wicket Panel instance. Example;
 *
 * <code><pre>
 * public class PrintersPanel extends Panel
 * {
 *     private static final long serialVersionUID = 1L;
 *     public static final String WICKET_ID_NAME_LABEL = &quot;name&quot;;
 *     private static final String WICKET_ID_PRINTER = &quot;printer&quot;;
 *     private static final String WICKET_ID_PRINTERS = &quot;printers&quot;;
 *
 *     FloorPanel( String id, ContentAggregator container, Floor floor )
 *     {
 *         super( id, new Model( &quot;printers&quot; ) );
 *         ListView listView = new ListView( WICKET_ID_PRINTERS )
 *         {
 *             private static final long serialVersionUID = 1L;
 *
 *             protected void populateItem( final ListItem item )
 *             {
 *                 Component modelObject = (Component) item.getModelObject();
 *                 item.add( modelObject );
 *             }
 *         };
 *         List&lt;Component&gt; printers = container.createComponents( WICKET_ID_PRINTER, listView );
 *         if ( printers.isEmpty() )
 *         {
 *             Label tLabel = new Label( WICKET_ID_PRINTER, &quot;No Printers are available at the moment.&quot; );
 *             printers.add( tLabel );
 *         }
 *         Model listViewModel = new Model( (Serializable) printers );
 *         listView.setModel( listViewModel );
 *         add( listView );
 *     }
 * }
 * </pre></code> In the above example, we are wiring printer <i>ContentSource</i>s to a printers panel. The rendering of each
 * Printer view is up to the wired printer, and the aggregator just need a little bit of html to define its own panel.
 * Something like this; <code><pre>
 *           &lt;html xmlns=&quot;http://www.w3.org/1999/xhtml&quot;
 *                    xmlns:wicket=&quot;http://wicket.sourceforge.net/&quot;
 *                    xml:lang=&quot;en&quot;
 *                    lang=&quot;en&quot;
 *           &gt;
 *               &lt;body&gt;
 *                   &lt;wicket:panel&gt;
 *                       &lt;div class=&quot;printerspanel&quot;&gt;
 *                           &lt;div class=&quot;printer-row&quot; wicket:id=&quot;printers&quot;&gt;
 *                               &lt;span class=&quot;printer-item&quot; wicket:id=&quot;printer&quot;&gt;&lt;/span&gt;
 *                           &lt;/div&gt;
 *                       &lt;/div&gt;
 *                   &lt;/wicket:panel&gt;
 *               &lt;/body&gt;
 *           &lt;/html&gt;
 * </pre></code>
 * </p>
 */
public abstract class AbstractAggregatedSource<E extends Component> extends BaseAggregator
    implements ContentAggregator, ContentSource<E>, ContentTrackingCallback, ManagedService
{

    private static final String[] ROLES_TYPE = new String[0];

    protected final Logger m_logger = Logger.getLogger( getClass() );
    private Roles m_requiredRoles;
    private Roles m_basicRoles;

    /**
     * Construct an instance of {@code AbstractContentAggregator} with the specified arguments.
     *
     * @param bundleContext    The bundle context. This argument must not be {@code null}.
     * @param applicationName  The application name. This argument must not be {@code null} or empty.
     * @param aggregationPoint The aggregation point id. This argument must not be {@code null} or empty.
     * @param destination      The destination id. This argument must not be {@code null} or empty.
     *
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null} or empty.
     * @since 1.0.0
     */
    protected AbstractAggregatedSource( BundleContext bundleContext, String applicationName, String aggregationPoint,
                                        String destination )
        throws IllegalArgumentException
    {
        super( bundleContext, applicationName, aggregationPoint );
        NullArgumentException.validateNotEmpty( destination, "destination" );

        setDestination( destination );
    }

    /**
     * Returns the source id.
     *
     * @return The source id.
     *
     * @since 1.0.0
     */
    public final String getSourceId()
    {
        return getStringProperty( SOURCE_ID, null );
    }

    /**
     * Returns the destination of this {@code ContentSource} instance. This method must not return {@code null} object.
     *
     * @since 1.0.0
     */
    public final String[] getDestinations()
    {
        return getStringArrayProperty( ContentSource.DESTINATIONS );
    }

    /**
     * Set the destination of this {@code ContentSource}.
     * <p>
     * Note: Destination property must not be set after this {@code ContentSource} instance is registered to OSGi
     * framework.
     * </p>
     *
     * @param destination The destination. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code destination} argument is {@code null} or empty.
     * @since 1.0.0
     */
    public final void setDestination( String destination )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( destination, "destination" );
        setProperty( ContentSource.DESTINATIONS, destination );
        updateRegistration();
    }

    protected String[] getServiceNames()
    {
        return new String[]
            {
                ContentSource.class.getName(), ContentAggregator.class.getName(), ManagedService.class.getName()
            };
    }

    /**
     * Create the wicket component represented by this {@code ContentSource} instance. This method must not return
     * {@code null} object.
     * <p>
     * General convention:<br/>
     * <ul>
     * <li>In the use case of Wicket 1 environment. The callee of this method responsibles to add the component created
     * this method;</li>
     * </ul>
     * </p>
     *
     * @param wicketId The wicket id. This argument must not be {@code null}.
     *
     * @return The wicket component represented by this {@code ContentSource} instance, or null if user has no access to
     *         this ContentSource.
     *
     * @throws IllegalArgumentException Thrown if the {@code wicketId} argument is {@code null}.
     * @since 1.0.0
     */
    public final <T extends MarkupContainer> E createSourceComponent( String wicketId )
        throws IllegalArgumentException
    {
        boolean isRolesApproved = isRolesAuthorized();
        if( isRolesApproved )
        {
            return createComponent( wicketId );
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns {@code true} if the user roles is approved to create this content source component, {@code false}
     * otherwise.
     *
     * @return A {@code boolean} indicator whether user roles is approved to create this content source component.
     *
     * @since 1.0.0
     */
    private boolean isRolesAuthorized()
    {
        Roles userRoles = getAuthentication().getRoles();

        Roles requiredRoles = new Roles( getStringProperty( REQUIRED_ROLES, "" ) );
        Roles basicRoles = new Roles( getStringProperty( BASIC_ROLES, "" ) );
        boolean isRequiredRolesAuthorized = userRoles.hasAllRoles( requiredRoles );
        boolean isBasicRolesAuthorized = true;
        if( !basicRoles.isEmpty() )
        {
            userRoles.hasAnyRole( basicRoles );
        }
        return isRequiredRolesAuthorized && isBasicRolesAuthorized;
    }

    /**
     * Create component represented by this {@code AbstractContentAggregator} with the specified {@code wicketId}.
     *
     * <p>
     * General convention:<br/>
     * <ul>
     * <li>In the use case of Wicket 1 environment. The callee of this method responsibles to add the component created
     * this method;</li>
     * </ul>
     * </p>
     *
     * @param wicketId The wicketId. This argument must not be {@code null} nor empty. It maps to the wicket:id of the
     *                 rendering process in Wicket.
     *
     * @return A new instance of wicket component represented by this {@code AbstractContentAggregator}.
     *
     * @throws IllegalArgumentException Thrown if argument is {@code null}.
     * @since 1.0.0
     */
    protected abstract <T extends MarkupContainer> E createComponent( String wicketId )
        throws IllegalArgumentException;

    public final Roles getRequiredRoles()
    {
        return m_requiredRoles;
    }

    public final Roles getBasicRoles()
    {
        return m_basicRoles;
    }

    public final void setRoles( Roles requiredRoles, Roles basicRoles )
    {
        boolean changed = false;
        if( requiredRoles != null )
        {
            changed = true;
            m_requiredRoles = requiredRoles;
            setProperty( REQUIRED_ROLES, requiredRoles.toArray( ROLES_TYPE ) );
        }
        if( basicRoles != null )
        {
            m_basicRoles = basicRoles;
            setProperty( REQUIRED_ROLES, basicRoles.toArray( ROLES_TYPE ) );
            changed = true;
        }
        if( changed )
        {
            updateRegistration();
        }
    }

    protected final List<Component> createComponents( String wicketId )
    {
        NullArgumentException.validateNotEmpty( wicketId, "wicketId" );
        List<ContentSource> contents = getContents( wicketId );
        List<Component> items = new ArrayList<Component>();
        for( ContentSource content : contents )
        {
            Component comp = content.createSourceComponent( "portal-item" );
            if( comp != null )
            {
                items.add( comp );
            }
        }
        return items;
    }

}
