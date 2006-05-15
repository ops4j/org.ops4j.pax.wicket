/*
 * Copyright 2006 Niclas Hedhman.
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
package org.ops4j.pax.wicket.samples.library.view.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.ops4j.pax.wicket.samples.library.model.Library;

public class Activator
    implements BundleActivator
{
    private static LibraryTracker m_libraryTracker;
    private ServiceTracker m_tracker;

    public void start( BundleContext bundleContext )
        throws Exception
    {
        m_libraryTracker = new LibraryTracker( bundleContext );
        m_tracker = new ServiceTracker(bundleContext, Library.class.getName(), m_libraryTracker );
        m_tracker.open();
    }

    public void stop( BundleContext bundleContext )
        throws Exception
    {
        m_tracker.close();
    }

    public static Library getLibrary()
    {
        return m_libraryTracker.getLibrary();
    }
}
