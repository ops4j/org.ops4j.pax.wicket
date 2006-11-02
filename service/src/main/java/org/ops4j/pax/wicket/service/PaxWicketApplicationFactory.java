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
import org.ops4j.pax.wicket.service.internal.DelegatingClassResolver;
import org.ops4j.pax.wicket.service.internal.PaxWicketApplication;
import org.ops4j.pax.wicket.service.internal.PaxWicketPageFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import wicket.Page;
import wicket.protocol.http.IWebApplicationFactory;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WicketServlet;

public final class PaxWicketApplicationFactory
    implements IWebApplicationFactory, ManagedService
{

    private Class m_homepageClass;
    private BundleContext m_bundleContext;
    private ServiceRegistration m_registration;
    private PaxWicketPageFactory m_pageFactory;
    private Properties m_properties;
    private DelegatingClassResolver m_delegatingClassResolver;
    private PaxWicketAuthenticator m_authenticator;

    public PaxWicketApplicationFactory(
        BundleContext bundleContext, Class<? extends Page> homepageClass, String mountPoint, String applicationName,
        PaxWicketAuthenticator authenticator )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( mountPoint, "mountPoint" );

        m_properties = new Properties();
        m_homepageClass = homepageClass;
        m_bundleContext = bundleContext;
        m_authenticator = authenticator;
        setMountPoint( mountPoint );
        setDeploymentMode( false );
        setApplicationName( applicationName );
        m_properties.setProperty( Content.HOMEPAGE_CLASSNAME, homepageClass.getName() );
    }

    public void dispose()
    {
        m_pageFactory.dispose();
        m_delegatingClassResolver.dispose();
    }

    public String getMountPoint()
    {
        return m_properties.getProperty( Content.MOUNTPOINT );
    }

    public boolean isDeploymentMode()
    {
        return Boolean.parseBoolean( m_properties.getProperty( Content.DEPLOYMENT_MODE ) );
    }

    public void setDeploymentMode( boolean deploymentMode )
    {
        String depl = String.valueOf( deploymentMode );
        m_properties.put( Content.DEPLOYMENT_MODE, depl );
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
        PaxWicketApplication paxWicketApplication = new PaxWicketApplication(
            m_homepageClass, m_pageFactory, m_delegatingClassResolver, m_authenticator, isDeploymentMode()
        );
        return paxWicketApplication;
    }

    public void updated( Dictionary config )
        throws ConfigurationException
    {
        if( config == null )
        {
            m_registration.setProperties( m_properties );
            return;
        }
        String classname = (String) config.get( Content.HOMEPAGE_CLASSNAME );
        if( classname == null )
        {
            config.put( Content.HOMEPAGE_CLASSNAME, m_homepageClass );
        }
        else
        {
            try
            {
                m_homepageClass = m_bundleContext.getBundle().loadClass( classname );
            } catch( ClassNotFoundException e )
            {
                throw new ConfigurationException(
                    Content.HOMEPAGE_CLASSNAME, "Class not found in application bundle.", e
                );
            }
        }
        m_registration.setProperties( config );
    }

    public ServiceRegistration register()
    {
        String[] serviceNames = { PaxWicketApplicationFactory.class.getName(), ManagedService.class.getName() };
        m_registration = m_bundleContext.registerService( serviceNames, this, m_properties );
        return m_registration;
    }

    private void setApplicationName( String applicationName )
    {
        if( m_pageFactory != null )
        {
            m_pageFactory.dispose();
        }
        if( m_delegatingClassResolver != null )
        {
            m_delegatingClassResolver.dispose();
        }

        m_delegatingClassResolver = new DelegatingClassResolver( m_bundleContext, applicationName );
        m_delegatingClassResolver.intialize();

        m_pageFactory = new PaxWicketPageFactory( m_bundleContext, applicationName );
        m_pageFactory.initialize();

        m_properties.setProperty( Content.APPLICATION_NAME, applicationName );
    }

    private void setMountPoint( String mountPoint )
    {
        m_properties.put( Content.MOUNTPOINT, mountPoint );
    }
}
