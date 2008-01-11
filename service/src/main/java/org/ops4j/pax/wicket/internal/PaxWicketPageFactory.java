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
package org.ops4j.pax.wicket.internal;

import java.util.HashMap;
import java.util.Properties;
import org.apache.wicket.IPageFactory;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.application.IClassResolver;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.api.PageFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PaxWicketPageFactory
    implements IPageFactory, IClassResolver
{

    private static final Logger LOGGER = LoggerFactory.getLogger( PaxWicketPageFactory.class );

    private final BundleContext m_bundleContext;
    private final String m_applicationName;
    private final HashMap<String, Class> m_pageClasses;
    private final HashMap<Class, PageFactory> m_contents;

    private ServiceTracker m_pageTracker;
    private ServiceRegistration m_serviceRegistration;

    public PaxWicketPageFactory( BundleContext appBundleContext, String applicationName )
    {
        m_contents = new HashMap<Class, PageFactory>();
        m_pageClasses = new HashMap<String, Class>();
        m_bundleContext = appBundleContext;
        m_applicationName = applicationName;
    }

    public final void initialize()
    {
        Properties config = new Properties();
        config.setProperty( ContentSource.APPLICATION_NAME, m_applicationName );
        m_serviceRegistration = m_bundleContext.registerService( IClassResolver.class.getName(), this, config );
        m_pageTracker = new PaxWicketPageTracker( m_bundleContext, m_applicationName, this );
        m_pageTracker.open();
    }

    public final void dispose()
    {
        synchronized( this )
        {
            m_serviceRegistration.unregister();
            m_contents.clear();
            m_pageTracker.close();
        }
    }

    /**
     * Creates a new page using a page class.
     *
     * @param pageClass The page class to instantiate
     *
     * @return The page
     *
     * @throws org.apache.wicket.WicketRuntimeException
     *          Thrown if the page cannot be constructed
     */
    public final Page newPage( Class pageClass )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( pageClass, "pageClass" );

        return newPage( pageClass, null );
    }

    /**
     * Creates a new Page, passing PageParameters to the Page constructor if such a constructor exists. If no such
     * constructor exists and the parameters argument is null or empty, then any available default constructor will be
     * used.
     *
     * @param pageClass  The class of Page to create
     * @param parameters Any parameters to pass to the Page's constructor
     *
     * @return The new page
     *
     * @throws org.apache.wicket.WicketRuntimeException
     *          Thrown if the page cannot be constructed
     */
    public final Page newPage( Class pageClass, PageParameters parameters )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( pageClass, "pageClass" );

        PageFactory content;
        synchronized( this )
        {
            content = m_contents.get( pageClass );
        }
        if( content == null )
        {
            try
            {
                return (Page) pageClass.newInstance();
            }
            catch( InstantiationException e )
            {
                String message = "An abstract class or an interface was requested to be a Page: " + pageClass;
                LOGGER.error( message, e );
                throw new WicketRuntimeException( message, e );
            }
            catch( IllegalAccessException e )
            {
                String message = "The constructor in " + pageClass + " is not public and without parameters.";
                LOGGER.error( message, e );
                throw new WicketRuntimeException( message, e );
            }
        }

        return content.createPage( parameters );
    }

    public void add( Class pageClass, PageFactory pageSource )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( pageClass, "pageClass" );
        NullArgumentException.validateNotNull( pageSource, "pageSource" );

        synchronized( this )
        {
            m_contents.put( pageClass, pageSource );
            String tPageClassName = pageClass.getName();
            m_pageClasses.put( tPageClassName, pageClass );
        }
    }

    public final void remove( Class pageClass )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( pageClass, "pageClass" );

        synchronized( this )
        {
            m_contents.remove( pageClass );
            String tPageClassName = pageClass.getName();
            m_pageClasses.remove( tPageClassName );
        }
    }

    /**
     * Resolves a class by name (which may or may not involve loading it; thus the name class *resolver* not *loader*).
     *
     * @param classname Fully qualified classname to find
     *
     * @return Class
     */
    public final Class resolveClass( String classname )
    {
        Class resolved;
        synchronized( this )
        {
            resolved = m_pageClasses.get( classname );
        }

        if( resolved == null )
        {
            try
            {
                Class<? extends PaxWicketPageFactory> thisClass = getClass();
                ClassLoader classLoader = thisClass.getClassLoader();
                resolved = classLoader.loadClass( classname );
            }
            catch( ClassNotFoundException e )
            {
                return null;
            }
        }
        return resolved;
    }
}
