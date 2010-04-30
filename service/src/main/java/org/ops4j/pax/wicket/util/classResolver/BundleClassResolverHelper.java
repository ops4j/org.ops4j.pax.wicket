/*  Copyright 2008 Edward Yakop.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.util.classResolver;


import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.Properties;
import org.apache.wicket.application.IClassResolver;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;

import org.ops4j.pax.wicket.internal.EnumerationAdapter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import static org.osgi.framework.Constants.SERVICE_PID;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

/**
 * {@code BundleClassResolverHelper} is a helper to register {@code IClassResolver}.
 *
 * @author edward.yakop@gmail.com
 * @since 0.5.4
 */
public final class BundleClassResolverHelper
{

    private static final String[] SERVICE_NAMES =
        {
            IClassResolver.class.getName(),
            ManagedService.class.getName()
        };

    private final BundleContext m_bundleContext;
    private final Properties m_serviceProperties;

    private final Object m_lock = new Object();
    private ServiceRegistration m_serviceRegistration;

    /**
     * Construct an instance of {@code BundleClassResolver}.
     *
     * @param bundleContext The bundle context. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code bundleContext} argument is {@code null}.
     * @since 0.5.4
     */
    public BundleClassResolverHelper( BundleContext bundleContext )
        throws IllegalArgumentException
    {
        validateNotNull( bundleContext, "bundle" );

        m_bundleContext = bundleContext;
        m_serviceProperties = new Properties();
    }

    /**
     * Sets the service pid of this {@code BundleClassResolverHelper} instance.
     * This is useful if this class resolver needs to be wired to multiple pax-wicket applications.
     *
     * @param servicePid The service pid.
     *
     * @see Constants#SERVICE_PID
     * @since 0.5.4
     */
    public final void setServicePid( String servicePid )
    {
        synchronized( m_lock )
        {
            if( servicePid == null )
            {
                m_serviceProperties.remove( SERVICE_PID );
            }
            else
            {
                m_serviceProperties.setProperty( SERVICE_PID, servicePid );
            }

            if( m_serviceRegistration != null )
            {
                m_serviceRegistration.setProperties( m_serviceProperties );
            }
        }
    }

    /**
     * @return The service pid of this {@code BundleClassResolverHelper}. Returns {@code null} if not set.
     *
     * @since 0.5.4
     */
    public final String getServicePid()
    {
        synchronized( m_lock )
        {
            return m_serviceProperties.getProperty( SERVICE_PID );
        }
    }

    /**
     * Sets the application nane.
     *
     * @param applicationNames The application names.
     *
     * @since 0.5.4
     */
    public final void setApplicationName( String... applicationNames )
    {
        synchronized( m_lock )
        {
            if( applicationNames == null )
            {
                m_serviceProperties.remove( APPLICATION_NAME );
            }
            else
            {
                m_serviceProperties.put( APPLICATION_NAME, applicationNames );
            }

            if( m_serviceRegistration != null )
            {
                m_serviceRegistration.setProperties( m_serviceProperties );
            }
        }
    }

    /**
     * Register class resolver.
     *
     * @since 0.5.4
     */
    public final void register()
    {
        synchronized( m_lock )
        {
            if( m_serviceRegistration == null )
            {
                BundleClassResolver resolver = new BundleClassResolver();
                m_serviceRegistration = m_bundleContext.registerService( SERVICE_NAMES, resolver, m_serviceProperties );
            }
        }
    }

    /**
     * Unregister class resolver.
     *
     * @since 0.5.4
     */
    public final void unregister()
    {
        synchronized( m_lock )
        {
            if( m_serviceRegistration != null )
            {
                m_serviceRegistration.unregister();
                m_serviceRegistration = null;
            }
        }
    }

    private final class BundleClassResolver
        implements IClassResolver, ManagedService
    {

        public final Class<?> resolveClass( String classname )
            throws ClassNotFoundException
        {
            Bundle bundle = m_bundleContext.getBundle();
            return bundle.loadClass( classname );
        }

        @SuppressWarnings( "unchecked" )
        public Iterator<URL> getResources( String name )
        {
            try
            {
                final Bundle bundle = m_bundleContext.getBundle();
                final Enumeration<URL> enumeration = bundle.getResources( name );
                if( null == enumeration )
                    return null;

                return new EnumerationAdapter<URL>( enumeration );
            }
            catch ( IOException e )
            {
                return Collections.<URL>emptyList().iterator();
            }
        }

        @SuppressWarnings( "unchecked" )
        public final void updated( Dictionary dictionary )
            throws ConfigurationException
        {
            synchronized( m_lock )
            {
                if( dictionary == null )
                {
                    return;
                }

                Object applicationNames = dictionary.get( APPLICATION_NAME );
                if( applicationNames != null )
                {
                    m_serviceProperties.put( APPLICATION_NAME, applicationNames );
                }
                else
                {
                    m_serviceProperties.remove( APPLICATION_NAME );
                }

                m_serviceRegistration.setProperties( m_serviceProperties );
            }
        }
    }
}
