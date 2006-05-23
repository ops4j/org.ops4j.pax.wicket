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
import java.util.Properties;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.service.internal.PaxWicketApplication;
import org.ops4j.pax.wicket.service.internal.PaxWicketPageFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import wicket.protocol.http.IWebApplicationFactory;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WicketServlet;

public final class PaxWicketApplicationFactory
    implements IWebApplicationFactory, ManagedService
{

    public static final String MOUNTPOINT = "mountpoint";
    public static final String HOMEPAGE_CLASSNAME = "homepageclassname";

    private Class m_homepageClass;

    private String m_mountPoint;

    private BundleContext m_bundleContext;

    private ServiceRegistration m_serviceRegistration;
    private boolean m_deploymentMode;
    private String m_applicationName;
    private PaxWicketPageFactory m_pageFactory;

    public PaxWicketApplicationFactory( BundleContext bundleContext, Class homepageClass, String mountPoint,
                                        String applicationName )
    {
        NullArgumentException.validateNotNull( mountPoint, "mountPoint" );
        m_homepageClass = homepageClass;
        m_bundleContext = bundleContext;
        m_mountPoint = mountPoint;
        m_deploymentMode = false;
        setApplicationName( applicationName );

    }

    public void dispose()
    {
        m_pageFactory.stop();
    }

    public String getMountPoint()
    {
        return m_mountPoint;
    }

    public void setMountPoint( String mountPoint )
    {
        NullArgumentException.validateNotNull( mountPoint, "mountPoint" );
        m_mountPoint = mountPoint;
    }

    /**
     * Create application object
     *
     * @param servlet the wicket servlet
     *
     * @return application object instance
     */
    public WebApplication createApplication( WicketServlet servlet )
    {
        PaxWicketApplication paxWicketApplication =
            new PaxWicketApplication( m_homepageClass, m_pageFactory, m_deploymentMode );
        return paxWicketApplication;
    }

    public void updated( Dictionary config )
        throws ConfigurationException
    {
        if( config == null )
        {
            return;
        }
        String classname = (String) config.get( HOMEPAGE_CLASSNAME );
        if( classname == null )
        {
            config.put( HOMEPAGE_CLASSNAME, m_homepageClass );
        }
        else
        {
            try
            {
                m_homepageClass = m_bundleContext.getBundle().loadClass( classname );
            } catch( ClassNotFoundException e )
            {
                throw new ConfigurationException( HOMEPAGE_CLASSNAME, "Class not found in application bundle.", e );
            }
        }
        String mountpoint = (String) config.get( MOUNTPOINT );
        if( mountpoint == null )
        {
            config.put( MOUNTPOINT, m_mountPoint );
        }
        else
        {
            setMountPoint( mountpoint );
        }
        String s = (String) config.get( "deploymentMode" );
        m_deploymentMode = Boolean.parseBoolean( s );
        m_serviceRegistration.setProperties( config );
    }

    public ServiceRegistration register()
    {
        String serviceName = PaxWicketApplicationFactory.class.getName();
        Properties properties = new Properties();
        properties.put( MOUNTPOINT, m_mountPoint );
        properties.put( HOMEPAGE_CLASSNAME, m_homepageClass.getName() );
        m_serviceRegistration = m_bundleContext.registerService( serviceName, this, properties );
        return m_serviceRegistration;
    }

    private void setApplicationName( String applicationName )
    {
        if( m_pageFactory != null )
        {
            m_pageFactory.stop();
        }

        m_pageFactory = new PaxWicketPageFactory( m_bundleContext, applicationName );
        m_pageFactory.start();
        m_applicationName = applicationName;
    }

}
