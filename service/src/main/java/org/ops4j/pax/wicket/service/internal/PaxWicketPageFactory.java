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
package org.ops4j.pax.wicket.service.internal;

import java.util.HashMap;
import java.util.Properties;
import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.wicket.service.PageContent;
import org.ops4j.pax.wicket.service.Content;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import wicket.IPageFactory;
import wicket.Page;
import wicket.PageParameters;
import wicket.WicketRuntimeException;
import wicket.application.IClassResolver;

public final class PaxWicketPageFactory
    implements IPageFactory, IClassResolver, Serializable
{

    private static final long serialVersionUID = 1L;
    private static final Log m_logger = LogFactory.getLog( PaxWicketPageFactory.class );
    private HashMap<Class, PageContent> m_contents;
    private HashMap<String, Class> m_pageClasses;

    private BundleContext m_bundleContext;
    private String m_applicationName;
    private ServiceTracker m_pageTracker;
    private ServiceRegistration m_serviceRegistration;

    public PaxWicketPageFactory( BundleContext appBundleContext, String applicationName )
    {
        m_contents = new HashMap<Class, PageContent>();
        m_pageClasses = new HashMap<String, Class>();
        m_bundleContext = appBundleContext;
        m_applicationName = applicationName;
    }

    public void initialize()
    {
        Properties config = new Properties();
        config.setProperty( Content.APPLICATION_NAME, m_applicationName );
        m_serviceRegistration =
            m_bundleContext.registerService( IClassResolver.class.getName(), this, config );
        m_pageTracker = new PaxWicketPageTracker( m_bundleContext, m_applicationName, this );
        m_pageTracker.open();
    }

    public void dispose()
    {
        m_serviceRegistration.unregister();
        m_contents.clear();
        m_pageTracker.close();
    }

    /**
     * Creates a new page using a page class.
     *
     * @param pageClass The page class to instantiate
     *
     * @return The page
     *
     * @throws wicket.WicketRuntimeException Thrown if the page cannot be constructed
     */
    public Page newPage( final Class pageClass )
    {
        return newPage( pageClass, null );
    }

    /**
     * Creates a new Page, passing PageParameters to the Page constructor if
     * such a constructor exists. If no such constructor exists and the
     * parameters argument is null or empty, then any available default
     * constructor will be used.
     *
     * @param pageClass  The class of Page to create
     * @param parameters Any parameters to pass to the Page's constructor
     *
     * @return The new page
     *
     * @throws wicket.WicketRuntimeException Thrown if the page cannot be constructed
     */
    public Page newPage( final Class pageClass, final PageParameters parameters )
    {
        PageContent content = m_contents.get( pageClass );
        if( content == null )
        {
            try
            {
                return (Page) pageClass.newInstance();
            } catch( InstantiationException e )
            {
                String message = "An abstract class or an interface was requested to be a Page: " + pageClass;
                m_logger.error( message, e );
                throw new WicketRuntimeException( message, e );
            } catch( IllegalAccessException e )
            {
                String message = "The constructor in " + pageClass + " is not public and without parameters.";
                m_logger.error( message, e );
                throw new WicketRuntimeException( message, e );
            }
        }
        return content.createPage( parameters );
    }

    public void add( Class pageClass, PageContent page )
    {
        m_contents.put( pageClass, page );
        m_pageClasses.put( pageClass.getName(), pageClass );
    }

    public void remove( Class pageClass )
    {
        m_contents.remove( pageClass );
        m_pageClasses.remove( pageClass.getName() );
    }

    /**
     * Resolves a class by name (which may or may not involve loading it; thus
     * the name class *resolver* not *loader*).
     *
     * @param classname Fully qualified classname to find
     *
     * @return Class
     */
    public Class resolveClass( final String classname )
    {
        Class resolved = m_pageClasses.get( classname );
        if( resolved == null )
        {
            try
            {
                Class<? extends PaxWicketPageFactory> thisClass = getClass();
                ClassLoader classLoader = thisClass.getClassLoader();
                resolved = classLoader.loadClass( classname );
            } catch( ClassNotFoundException e )
            {
                return null;
            }
        }
        return resolved;
    }
}
