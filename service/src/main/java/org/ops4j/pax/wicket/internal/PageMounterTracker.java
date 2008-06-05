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

import static java.lang.System.identityHashCode;
import java.util.*;
import javax.servlet.ServletException;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketServlet;
import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;

import org.ops4j.pax.wicket.api.*;
import org.osgi.framework.*;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.*;

final class PageMounterTracker
    extends ServiceTracker
{
    private static final Logger LOGGER = LoggerFactory.getLogger( PageMounterTracker.class );
    private static final String SERVICE_NAME = PageMounter.class.getName();

    private final WebApplication m_application;

    PageMounterTracker( BundleContext bundleContext, WebApplication application )
        throws IllegalArgumentException
    {
        super( bundleContext, SERVICE_NAME, null );
        validateNotNull( application, "WebApplication" );

        m_application = application;
    }

    @Override
    public final Object addingService( ServiceReference serviceReference )
    {
        final PageMounter mounter = (PageMounter)super.addingService( serviceReference );

        for( MountPointInfo bookmark : mounter.getMountPoints() )
        {
            m_application.mount( bookmark.getCodingStrategy() );
        }

        return mounter;
    }

    @Override
    public final void removedService( ServiceReference serviceReference, Object service )
    {
        final PageMounter mounter = (PageMounter)super.addingService( serviceReference );

        for( MountPointInfo bookmark : mounter.getMountPoints() )
        {
            m_application.unmount( bookmark.getPath() );
        }
    }
}
