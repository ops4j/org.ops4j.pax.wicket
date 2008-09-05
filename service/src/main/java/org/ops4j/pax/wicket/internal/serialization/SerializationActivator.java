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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author edward.yakop@gmail.com
 */
public class SerializationActivator
    implements BundleActivator
{

    private static BundleContext m_bundleContext;

    public static BundleContext bundleContext()
    {
        return m_bundleContext;
    }

    public static BundleContext getBundleContextByBundleId( long bundleId )
    {
        Bundle bundle = m_bundleContext.getBundle( bundleId );
        if( bundle != null )
        {
            return bundle.getBundleContext();
        }
        else
        {
            return null;
        }
    }

    public final void start( BundleContext context )
        throws Exception
    {
        m_bundleContext = context;
    }

    public final void stop( BundleContext context )
        throws Exception
    {
        m_bundleContext = null;
    }
}
