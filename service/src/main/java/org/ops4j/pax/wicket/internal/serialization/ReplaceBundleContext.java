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

import java.io.Serializable;
import java.lang.ref.WeakReference;
import static java.util.Collections.synchronizedMap;
import java.util.HashMap;
import java.util.Map;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author edward.yakop@gmail.com
 * @since 0.5.4
 */
final class ReplaceBundleContext
    implements Serializable
{

    private static final long serialVersionUID = 1L;

    private static final Map<Long, WeakReference<BundleContext>> placeHolders;

    static
    {
        placeHolders = synchronizedMap( new HashMap<Long, WeakReference<BundleContext>>() );
    }

    /**
     * Removes bundle place holder.
     *
     * @param aBundleId The bundle id.
     *
     * @since 0.5.4
     */
    static void removeBundlePlaceHolder( long aBundleId )
    {
        placeHolders.remove( aBundleId );
    }

    private final long bundleId;

    /**
     * Construct a new instance of {@code ReplaceBundleContext}.
     *
     * @param aBundleContext The bundle context. Must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code aBundleContext} is {@code null}.
     * @since 0.5.4
     */
    ReplaceBundleContext( BundleContext aBundleContext )
        throws IllegalArgumentException
    {
        validateNotNull( aBundleContext, "aBundleContext" );

        Bundle bundle = aBundleContext.getBundle();
        bundleId = bundle.getBundleId();

        WeakReference<BundleContext> reference = placeHolders.get( bundleId );
        if( reference == null || reference.get() == null )
        {
            placeHolders.put( bundleId, new WeakReference<BundleContext>( aBundleContext ) );
        }
    }

    /**
     * Returns the bundle context.
     *
     * @return The bundle context.
     *
     * @since 0.5.4
     */
    final BundleContext getBundleContext()
    {
        WeakReference<BundleContext> bundleReference;
        synchronized( placeHolders )
        {
            bundleReference = placeHolders.get( bundleId );
        }

        if( bundleReference != null )
        {
            return bundleReference.get();
        }
        else
        {
            return null;
        }
    }

}
