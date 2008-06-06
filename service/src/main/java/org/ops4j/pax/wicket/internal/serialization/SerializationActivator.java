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
import static org.ops4j.pax.wicket.internal.serialization.ReplaceBundleContext.removeBundlePlaceHolder;
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

    private static BundleContext bundleContext;

    public static BundleContext bundleContext()
    {
        return bundleContext;
    }

    private CleanupBundleListener cleanupListener;

    public final void start( BundleContext aContext )
        throws Exception
    {
        bundleContext = aContext;
        cleanupListener = new CleanupBundleListener();
        aContext.addBundleListener( cleanupListener );
    }

    public final void stop( BundleContext aContext )
        throws Exception
    {
        aContext.removeBundleListener( cleanupListener );

        bundleContext = null;
        cleanupListener = null;
    }

    private static class CleanupBundleListener
        implements BundleListener
    {

        public final void bundleChanged( BundleEvent anEvent )
        {
            int eventType = anEvent.getType();
            if( eventType == STOPPING ||
                eventType == UNINSTALLED ||
                eventType == UNRESOLVED )
            {
                Bundle bundle = anEvent.getBundle();
                long bundleId = bundle.getBundleId();
                removeBundlePlaceHolder( bundleId );
            }
        }
    }
}
