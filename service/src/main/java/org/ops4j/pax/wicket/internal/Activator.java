/*
 * Copyright 2005 Niclas Hedhman.
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

import org.ops4j.pax.wicket.internal.serialization.SerializationActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Activator
    implements BundleActivator
{

    private static final Logger LOGGER = LoggerFactory.getLogger( Activator.class );

    private HttpTracker m_httpTracker;
    private ServiceTracker m_applicationFactoryTracker;
    private SerializationActivator m_serializationActivator;

    public final void start( BundleContext context )
        throws Exception
    {
        if( LOGGER.isDebugEnabled() )
        {
            Bundle bundle = context.getBundle();
            String bundleSymbolicName = bundle.getSymbolicName();

            LOGGER.debug( "Initializing [" + bundleSymbolicName + "] bundle." );
        }

        m_httpTracker = new HttpTracker( context );
        m_httpTracker.open();

        m_applicationFactoryTracker = new PaxWicketAppFactoryTracker( context, m_httpTracker );
        m_applicationFactoryTracker.open();

        m_serializationActivator = new SerializationActivator();
        m_serializationActivator.start( context );
        context.getBundle().getResources( "pathToResource" );
    }

    public final void stop( BundleContext context )
        throws Exception
    {
        m_serializationActivator.stop( context );
        m_httpTracker.close();
        m_applicationFactoryTracker.close();

        m_serializationActivator = null;
        m_httpTracker = null;
        m_applicationFactoryTracker = null;

        if( LOGGER.isDebugEnabled() )
        {
            Bundle bundle = context.getBundle();
            String bundleSymbolicName = bundle.getSymbolicName();

            LOGGER.debug( "Bundle [" + bundleSymbolicName + "] stopped." );
        }
    }
}
