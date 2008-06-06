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
package org.ops4j.pax.wicket.api;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;
import org.apache.wicket.Page;
import org.apache.wicket.application.IClassResolver;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;
import static org.ops4j.pax.wicket.api.ContentSource.HOMEPAGE_CLASSNAME;
import static org.ops4j.pax.wicket.api.ContentSource.MOUNTPOINT;
import org.ops4j.pax.wicket.internal.BundleDelegatingClassResolver;
import org.ops4j.pax.wicket.internal.DelegatingClassResolver;
import org.ops4j.pax.wicket.internal.PaxAuthenticatedWicketApplication;
import org.ops4j.pax.wicket.internal.PaxWicketApplication;
import org.ops4j.pax.wicket.internal.PaxWicketPageFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

public final class PaxWicketApplicationFactory
    implements IWebApplicationFactory, ManagedService
{

    private static final String[] APPLICATION_FACTORY_SERVICE_NAMES = {
        PaxWicketApplicationFactory.class.getName(), ManagedService.class.getName()
    };

    private final BundleContext bundleContext;
    private Class<? extends Page> homepageClass;
    private final Properties properties;

    private PaxWicketPageFactory pageFactory;
    private DelegatingClassResolver delegatingClassResolver;

    private ServiceRegistration registration;

    private PaxWicketAuthenticator authenticator;
    private Class<? extends WebPage> signinPage;

    private PageMounter pageMounter;

    private List<IComponentInstantiationListener> componentInstantiationListeners;

    private ServiceRegistration bdcrRegistration;
    private BundleDelegatingClassResolver bdcr;

    /**
     * Construct an instance of {@code PaxWicketApplicationFactory} with the specified arguments.
     *
     * @param aBundleContext    The bundle context. This argument must not be {@code null}.
     * @param aHomepageClass    The homepage class. This argument must not be {@code null}.
     * @param aMountPoint       The mount point. This argument must not be be {@code null}.
     * @param anApplicationName The application name. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null}.
     * @since 1.0.0
     */
    public PaxWicketApplicationFactory(
        BundleContext aBundleContext,
        Class<? extends Page> aHomepageClass,
        String aMountPoint,
        String anApplicationName )
        throws IllegalArgumentException
    {
        validateNotNull( aBundleContext, "aBundleContext" );
        validateNotNull( aHomepageClass, "aHomepageClass" );
        validateNotNull( aMountPoint, "aMountPoint" );
        validateNotEmpty( anApplicationName, "anApplicationName" );

        properties = new Properties();

        homepageClass = aHomepageClass;
        bundleContext = aBundleContext;

        setMountPoint( aMountPoint );
        setApplicationName( anApplicationName );

        String homepageClassName = aHomepageClass.getName();
        properties.setProperty( HOMEPAGE_CLASSNAME, homepageClassName );

        componentInstantiationListeners = new ArrayList<IComponentInstantiationListener>();
    }

    /**
     * Sets the authenticator of this pax application factory.
     * <p>
     * Note: Value changed will only affect wicket application created after this method invocation.
     * </p>
     *
     * @param anAuthenticator The authenticator.
     * @param aSignInPage     The sign in page.
     *
     * @throws IllegalArgumentException Thrown if one of the arguments are {@code null}.
     * @see #register()
     * @since 1.0.0
     */
    public final void setAuthenticator( PaxWicketAuthenticator anAuthenticator, Class<? extends WebPage> aSignInPage )
        throws IllegalArgumentException
    {
        if( ( anAuthenticator != null && aSignInPage == null ) || ( anAuthenticator == null && aSignInPage != null ) )
        {
            String message = "Both [anAuthenticator] and [aSignInPage] argument must not be [null].";
            throw new IllegalArgumentException( message );
        }

        authenticator = anAuthenticator;
        signinPage = aSignInPage;
    }

    /**
     * Clear resources used by this {@code PaxWicketApplicationFactory} instance.
     * <p>
     * Note: dispose does not unregister this {@code PaxWicketApplicationFactory} instance from the OSGi container.
     * </p>
     *
     * @since 1.0.0
     */
    public final void dispose()
    {
        synchronized( this )
        {
            pageFactory.dispose();
            delegatingClassResolver.dispose();

            if( bdcrRegistration != null )
            {
                bdcrRegistration.unregister();
            }
        }
    }

    /**
     * Returns the mount point that the wicket application will be accessible. This method must not return {@code null}
     * string.
     *
     * @return The mount point that the wicket application will be accessible.
     *
     * @since 1.0.0
     */
    public final String getMountPoint()
    {
        synchronized( this )
        {
            return properties.getProperty( MOUNTPOINT );
        }
    }

    @SuppressWarnings( "unchecked" )
    public final void updated( Dictionary config )
        throws ConfigurationException
    {
        if( config == null )
        {
            synchronized( this )
            {
                registration.setProperties( properties );
            }

            return;
        }

        String classname = (String) config.get( HOMEPAGE_CLASSNAME );
        if( classname == null )
        {
            synchronized( this )
            {
                String homepageClassName = homepageClass.getName();
                config.put( HOMEPAGE_CLASSNAME, homepageClassName );
            }
        }
        else
        {
            synchronized( this )
            {
                try
                {
                    Bundle bundle = bundleContext.getBundle();
                    homepageClass = bundle.loadClass( classname );
                }
                catch( ClassNotFoundException e )
                {
                    throw new ConfigurationException( HOMEPAGE_CLASSNAME, "Class not found in application bundle.", e );
                }
            }
        }

        String applicationName = (String) config.get( APPLICATION_NAME );
        if( applicationName == null )
        {
            String currentApplicationName;
            synchronized( this )
            {
                currentApplicationName = (String) properties.get( APPLICATION_NAME );
            }

            config.put( APPLICATION_NAME, currentApplicationName );
        }
        else
        {
            setApplicationName( applicationName );
        }

        String mountPoint = (String) config.get( MOUNTPOINT );
        if( mountPoint == null )
        {
            String currentMountPoint;
            synchronized( this )
            {
                currentMountPoint = properties.getProperty( MOUNTPOINT );
            }
            config.put( MOUNTPOINT, currentMountPoint );
        }
        else
        {
            setMountPoint( mountPoint );
        }

        registration.setProperties( config );
    }

    /**
     * Register this {@code PaxWicketApplicationFactory} instance to OSGi container.
     *
     * @return The service registration.
     *
     * @since 1.0.0
     */
    public final ServiceRegistration register()
    {
        Properties serviceProperties;
        synchronized( this )
        {
            serviceProperties = new Properties( properties );
        }
        registration = bundleContext.registerService( APPLICATION_FACTORY_SERVICE_NAMES, this, serviceProperties );

        return registration;
    }

    private void setApplicationName( String applicationName )
    {
        synchronized( this )
        {
            if( pageFactory != null )
            {
                pageFactory.dispose();
            }
            if( delegatingClassResolver != null )
            {
                delegatingClassResolver.dispose();
            }

            delegatingClassResolver = new DelegatingClassResolver( bundleContext, applicationName );
            delegatingClassResolver.intialize();

            pageFactory = new PaxWicketPageFactory( bundleContext, applicationName );
            pageFactory.initialize();

            properties.setProperty( APPLICATION_NAME, applicationName );
        }
    }

    private void setMountPoint( String aMountPoint )
    {
        synchronized( this )
        {
            properties.put( MOUNTPOINT, aMountPoint );
        }
    }

    public final void setPageMounter( PageMounter aPageMounter )
    {
        validateNotNull( aPageMounter, "pageMounter" );

        pageMounter = aPageMounter;
    }

    public final void setPaxWicketBundle( Bundle aBundle )
    {
        if( bdcrRegistration != null )
        {
            bdcrRegistration.unregister();
            bdcrRegistration = null;
        }

        if( bdcr != null )
        {
            bdcr.close();
        }

        if( aBundle != null )
        {
            Properties config = new Properties();
            String applicationName = getApplicationName();
            config.setProperty( APPLICATION_NAME, applicationName );

            bdcr = new BundleDelegatingClassResolver( bundleContext, applicationName, aBundle );
            bdcrRegistration = bundleContext.registerService( IClassResolver.class.getName(), bdcr, config );
        }
    }

    /**
     * Returns the application name.
     *
     * @return The application name.
     *
     * @since 0.5.4
     */
    private String getApplicationName()
    {
        return properties.getProperty( APPLICATION_NAME );
    }

    public void addComponentInstantiationListener( IComponentInstantiationListener listener )
    {
        componentInstantiationListeners.add( listener );
    }

    /**
     * Creates a web application.
     *
     * @param aFilter The wicket filter.
     *
     * @return The new web application.
     */
    public final WebApplication createApplication( WicketFilter aFilter )
    {
        WebApplication paxWicketApplication;

        synchronized( this )
        {
            String applicationName = getApplicationName();

            if( authenticator != null && signinPage != null )
            {
                paxWicketApplication = new PaxAuthenticatedWicketApplication(
                    bundleContext, applicationName, pageMounter, homepageClass, pageFactory,
                    delegatingClassResolver, authenticator, signinPage
                );
            }
            else
            {
                paxWicketApplication = new PaxWicketApplication(
                    bundleContext, applicationName, pageMounter, homepageClass, pageFactory,
                    delegatingClassResolver
                );
            }
        }

        for( IComponentInstantiationListener listener : componentInstantiationListeners )
        {
            paxWicketApplication.addComponentInstantiationListener( listener );
        }

        return paxWicketApplication;
    }
}