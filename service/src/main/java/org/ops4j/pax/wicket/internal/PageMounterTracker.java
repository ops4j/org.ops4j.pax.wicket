/*
 * Copyright 2008 David Leangen
 * Copyright 2008 Edward F. Yakop
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

import java.util.List;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;
import org.ops4j.pax.wicket.api.MountPointInfo;
import org.ops4j.pax.wicket.api.PageMounter;
import org.osgi.framework.BundleContext;
import static org.osgi.framework.Constants.OBJECTCLASS;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

final class PageMounterTracker
    extends ServiceTracker
{

    private final WebApplication application;

    PageMounterTracker( BundleContext aContext, WebApplication anApplication, String anApplicationName )
        throws IllegalArgumentException
    {
        super( aContext, createFilter( aContext, anApplicationName ), null );
        validateNotNull( anApplication, "anApplication" );

        application = anApplication;
    }

    private static Filter createFilter( BundleContext aContext, String anApplicationName )
        throws IllegalArgumentException
    {
        validateNotNull( aContext, "aContext" );
        validateNotNull( anApplicationName, "anApplicationName" );

        String filterString =
            "(&(" + OBJECTCLASS + "=" + PageMounter.class.getName() + ")"
            + "(" + APPLICATION_NAME + "=" + anApplicationName + "))";

        try
        {
            return aContext.createFilter( filterString );
        }
        catch( InvalidSyntaxException e )
        {
            // TODO: Shouldn't happened
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public final Object addingService( ServiceReference aReference )
    {
        PageMounter mounter = (PageMounter) super.addingService( aReference );

        List<MountPointInfo> infos = mounter.getMountPoints();
        for( MountPointInfo info : infos )
        {
            IRequestTargetUrlCodingStrategy strategy = info.getCodingStrategy();
            application.mount( strategy );
        }

        return mounter;
    }

    @Override
    public final void removedService( ServiceReference aReference, Object aMounter )
    {
        PageMounter mounter = (PageMounter) aMounter;
        List<MountPointInfo> infos = mounter.getMountPoints();
        for( MountPointInfo bookmark : infos )
        {
            String path = bookmark.getPath();
            application.unmount( path );
        }

        super.removedService( aReference, aMounter );
    }
}
