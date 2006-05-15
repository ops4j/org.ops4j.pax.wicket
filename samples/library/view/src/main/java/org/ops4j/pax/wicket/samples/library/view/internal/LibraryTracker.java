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

import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.wicket.samples.library.model.Library;

public class LibraryTracker
    implements ServiceTrackerCustomizer
{
    private Library m_library;
    private BundleContext m_context;

    public LibraryTracker( BundleContext context )
    {
        m_context = context;
    }

    public Object addingService( ServiceReference serviceReference )
    {
        m_library = (Library) m_context.getService( serviceReference );
        return m_library;
    }

    public void modifiedService( ServiceReference serviceReference, Object object )
    {
    }

    public void removedService( ServiceReference serviceReference, Object object )
    {
        m_library = null;
    }

    public Library getLibrary()
    {
        return m_library;
    }
}
