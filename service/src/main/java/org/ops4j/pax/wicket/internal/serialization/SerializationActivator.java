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
package org.ops4j.pax.wicket.internal.serialization;

import static org.apache.wicket.util.lang.Objects.setObjectStreamFactory;
import static org.ops4j.pax.wicket.internal.serialization.deployment.ReplaceBundleContext.removeBundlePlaceHolder;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import static org.osgi.framework.BundleEvent.STOPPING;
import static org.osgi.framework.BundleEvent.UNINSTALLED;
import static org.osgi.framework.BundleEvent.UNRESOLVED;
import org.osgi.framework.BundleListener;

/**
 * @author edward.yakop@gmail.com
 */
public class SerializationActivator
    implements BundleActivator
{

    static
    {
        setObjectStreamFactory( new PaxWicketObjectStreamFactory() );
    }

    private static BundleContext m_bundleContext;

    public static BundleContext bundleContext()
    {
        return m_bundleContext;
    }

    private CleanupBundleListener m_cleanupListener;

    public final void start( BundleContext context )
        throws Exception
    {
        m_bundleContext = context;
        m_cleanupListener = new CleanupBundleListener();
        context.addBundleListener( m_cleanupListener );
    }

    public final void stop( BundleContext context )
        throws Exception
    {
        context.removeBundleListener( m_cleanupListener );

        m_bundleContext = null;
        m_cleanupListener = null;
    }

    private static class CleanupBundleListener
        implements BundleListener
    {

        public final void bundleChanged( BundleEvent event )
        {
            int eventType = event.getType();
            if( eventType == STOPPING ||
                eventType == UNINSTALLED ||
                eventType == UNRESOLVED )
            {
                Bundle bundle = event.getBundle();
                long bundleId = bundle.getBundleId();
                removeBundlePlaceHolder( bundleId );
            }
        }
    }
}
