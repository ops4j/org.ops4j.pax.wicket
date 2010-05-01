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
package org.ops4j.pax.wicket.util;

import java.util.Dictionary;
import java.util.Hashtable;
import org.apache.wicket.Page;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;
import static org.ops4j.pax.wicket.api.ContentSource.PAGE_ID;
import static org.ops4j.pax.wicket.api.ContentSource.PAGE_NAME;
import org.ops4j.pax.wicket.api.PageFactory;
import org.ops4j.pax.wicket.api.PaxWicketAuthentication;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

public abstract class AbstractPageFactory<T extends Page>
    implements PageFactory<T>, ManagedService
{

    private BundleContext m_bundleContext;
    private ServiceRegistration m_serviceRegistration;
    private Hashtable<String, String> m_properties;

    protected AbstractPageFactory( BundleContext bundleContext,
                                   String pageId, String applicationName, String pageName )
        throws IllegalArgumentException
    {
        validateNotNull( bundleContext, "bundleContext" );
        validateNotEmpty( pageId, "pageId" );
        validateNotEmpty( applicationName, "applicationName" );
        validateNotEmpty( pageName, "pageName" );

        m_properties = new Hashtable<String, String>();
        m_bundleContext = bundleContext;
        setApplicationName( applicationName );
        setPageName( pageName );

        m_properties.put( Constants.SERVICE_PID, PAGE_ID + "/" + pageId );
    }

    @SuppressWarnings( "unchecked" )
    public final void register()
        throws IllegalStateException
    {
        String[] classes = { PageFactory.class.getName(), ManagedService.class.getName() };

        synchronized( this )
        {
            if( m_serviceRegistration != null )
            {
                Class<? extends AbstractPageFactory> clazz = getClass();
                String className = clazz.getSimpleName();
                throw new IllegalArgumentException( className + "[" + this + "] has been registered." );
            }

            m_serviceRegistration = m_bundleContext.registerService( classes, this, m_properties );
        }
    }

    @SuppressWarnings( "unchecked" )
    public final void dispose()
        throws IllegalStateException
    {
        synchronized( this )
        {
            if( m_serviceRegistration == null )
            {
                Class<? extends AbstractPageFactory> clazz = getClass();
                String className = clazz.getSimpleName();
                throw new IllegalStateException( className + "[" + this + "] has not been registered." );
            }

            m_serviceRegistration.unregister();
            m_serviceRegistration = null;
        }
    }

    /**
     * Returns the application name.
     *
     * @return The application name.
     *
     * @since 1.0.0
     */
    public final String getApplicationName()
    {
        synchronized( this )
        {
            return m_properties.get( APPLICATION_NAME );
        }
    }

    /**
     * Returns the Authentication of the current request.
     *
     * It is possible to obtain the Username of the logged in user as well as which roles that this
     * user has assigned to it.
     *
     * @return the Authentication of the current request.
     */
    protected PaxWicketAuthentication getAuthentication()
    {
        return (PaxWicketAuthentication) AuthenticatedWebSession.get();
    }

    /**
     * Returns the page name.
     *
     * @return The page name.
     *
     * @since 1.0.0
     */
    public final String getPageName()
    {
        synchronized( this )
        {
            return m_properties.get( PAGE_NAME );
        }
    }

    @SuppressWarnings( "unchecked" )
    public void updated( Dictionary config )
        throws ConfigurationException
    {
        if( config == null )
        {
            synchronized( this )
            {
                m_serviceRegistration.setProperties( m_properties );
            }

            return;
        }

        String pagename = (String) config.get( PAGE_NAME );
        String appname = (String) config.get( APPLICATION_NAME );
        setPageName( pagename );
        setApplicationName( appname );
        synchronized( this )
        {
            m_serviceRegistration.setProperties( config );
        }
    }

    /**
     * Sets the application name.
     *
     * @param applicationName The application name. This argument must not be {@code null} or empty.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code applicationName} is {@code null}.
     * @since 1.0.0
     */
    protected final void setApplicationName( String applicationName )
        throws IllegalArgumentException
    {
        validateNotEmpty( applicationName, "applicationName" );

        synchronized( this )
        {
            m_properties.put( APPLICATION_NAME, applicationName );
        }
    }

    /**
     * Set the page name.
     *
     * @param pageName The page name. This argument must not be {@code null} or empty.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code pageName} arguments are {@code null}.
     * @since 1.0.0
     */
    protected final void setPageName( String pageName )
        throws IllegalArgumentException
    {
        validateNotEmpty( pageName, "pageName" );

        synchronized( this )
        {
            m_properties.put( PAGE_NAME, pageName );
        }
    }
}
