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
package org.ops4j.pax.wicket.internal;

import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;

import java.io.File;
import static java.lang.System.identityHashCode;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import org.ops4j.pax.wicket.api.PaxWicketApplicationFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class PaxWicketAppFactoryTracker extends ServiceTracker
{

    private static final Logger LOGGER = LoggerFactory.getLogger( PaxWicketAppFactoryTracker.class );
    private static final String SERVICE_NAME = PaxWicketApplicationFactory.class.getName();

    private final HttpTracker m_httpTracker;
    private final Map<PaxWicketApplicationFactory, String> m_factories;

    PaxWicketAppFactoryTracker( BundleContext context, HttpTracker httpTracker )
        throws IllegalArgumentException
    {
        super( context, SERVICE_NAME, null );

        validateNotNull( httpTracker, "httpTracker" );
        m_httpTracker = httpTracker;
        m_factories = new HashMap<PaxWicketApplicationFactory, String>();
    }

    @Override
    public final Object addingService( ServiceReference reference )
    {
        final PaxWicketApplicationFactory factory =
            (PaxWicketApplicationFactory) super.addingService( reference );

        if( LOGGER.isDebugEnabled() )
        {
            int factoryHash = identityHashCode( factory );
            String message = "Service Added [" + reference + "], Factory hash [" + factoryHash + "]";
            LOGGER.debug( message );
        }

        factory.setPaxWicketBundle( context.getBundle() );

        File tmpDir = context.getDataFile( "tmp-dir" );
        String mountPoint = factory.getMountPoint();
        Servlet servlet = null;
        if ( factory.getFilterConfiguration().getFilters().isEmpty() )
        {
            servlet = ServletProxy.newServletProxy( factory, tmpDir, mountPoint );
        }
        else
        {
            FilterDelegator filterDelegator = new FilterDelegator( reference.getBundle().getBundleContext(), 
                factory.getFilterConfiguration(), tmpDir, mountPoint, (String) reference.getProperty( APPLICATION_NAME ) );
            servlet = ServletProxy.newServletProxy( factory, tmpDir, mountPoint, filterDelegator );
        }
        addServlet( mountPoint, servlet, reference );

        synchronized( m_factories )
        {
            m_factories.put( factory, mountPoint );
        }

        return factory;
    }

    @Override
    public final void modifiedService( ServiceReference reference, Object service )
    {
        PaxWicketApplicationFactory factory = (PaxWicketApplicationFactory) service;
        String oldMountPoint;
        synchronized( m_factories )
        {
            oldMountPoint = m_factories.get( factory );
        }

        String newMountPoint = factory.getMountPoint();
        if( oldMountPoint.equals( newMountPoint ) )
        {
            return;
        }

        Servlet servlet = m_httpTracker.getServlet( oldMountPoint );
        removedService( reference, service );
        addServlet( newMountPoint, servlet, reference );
    }

    @Override
    public final void removedService( ServiceReference reference, Object service )
    {
        PaxWicketApplicationFactory factory = (PaxWicketApplicationFactory) service;
        if( LOGGER.isDebugEnabled() )
        {
            int factoryHash = identityHashCode( factory );
            String message = "Service removed [" + reference + "], Application hash [" + factoryHash + "]";
            LOGGER.debug( message );
        }

        String mountPoint;
        synchronized( m_factories )
        {
            mountPoint = m_factories.remove( factory );
        }

        m_httpTracker.removeServlet( mountPoint );
        factory.setPaxWicketBundle( null );
    }

    private void addServlet( String mountPoint, Servlet servlet, ServiceReference appFactoryReference )
    {
        validateNotEmpty( mountPoint, "mountPoint" );
        validateNotNull( servlet, "servlet" );
        validateNotNull( appFactoryReference, "appFactoryReference" );

        Bundle bundle = appFactoryReference.getBundle();
        try
        {
            m_httpTracker.addServlet( mountPoint, servlet, bundle );
        }
        catch( NamespaceException e )
        {
            throw new IllegalArgumentException(
                "Unable to mount [" + appFactoryReference + "] on mount point '" + mountPoint + "'."
            );
        }
        catch( ServletException e )
        {
            String message = "Wicket Servlet for [" + appFactoryReference + "] is unable to initialize. "
                             + "This servlet was tried to be mounted on '" + mountPoint + "'.";
            throw new IllegalArgumentException( message, e );
        }
    }
}
