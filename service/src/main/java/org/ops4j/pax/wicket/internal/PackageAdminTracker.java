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
package org.ops4j.pax.wicket.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A class to simplify the use of the PackageAdmin.
 * <p>
 * Simply by instantiating this class, you get hold of the PackageAdmin service, which should(?)
 * always be provided by the framework, and can't be missing.
 * </p>
 */
public class PackageAdminTracker extends ServiceTracker
    implements PackageAdmin
{

    private static final ExportedPackage[] EMPTY_PACKAGE_ARRAY = new ExportedPackage[0];
    private PackageAdmin m_delegate;

    public PackageAdminTracker( BundleContext bundleContext )
    {
        super( bundleContext, createFilter( bundleContext ), null );
    }

    private static Filter createFilter( BundleContext context )
    {
        try
        {
            return context.createFilter( Constants.OBJECTCLASS + "=" + PackageAdmin.class.getName() );
        } catch( InvalidSyntaxException e )
        {
            // Can not happen.
        }
        return null;
    }

    @Override
    public Object addingService( ServiceReference serviceReference )
    {
        Object service = super.addingService( serviceReference );
        m_delegate = (PackageAdmin) service;
        return service;
    }

    @Override
    public void removedService( ServiceReference serviceReference, Object object )
    {
        super.removedService( serviceReference, object );
        m_delegate = null;
    }

    public ExportedPackage[] getExportedPackages( Bundle bundle )
    {
        if( m_delegate == null ) // Can this ever happen?
        {
            return EMPTY_PACKAGE_ARRAY;
        }
        return m_delegate.getExportedPackages( bundle );
    }

    public ExportedPackage[] getExportedPackages( String name )
    {
        if( m_delegate == null ) // Can this ever happen?
        {
            return EMPTY_PACKAGE_ARRAY;
        }
        return m_delegate.getExportedPackages( name );
    }

    public ExportedPackage getExportedPackage( String name )
    {
        if( m_delegate == null ) // Can this ever happen?
        {
            return null;
        }
        return m_delegate.getExportedPackage( name );
    }

    public void refreshPackages( Bundle[] bundles )
    {
        if( m_delegate == null ) // Can this ever happen?
        {
            return;
        }
        m_delegate.refreshPackages( bundles );
    }

    public boolean resolveBundles( Bundle[] bundles )
    {
        return m_delegate != null && m_delegate.resolveBundles( bundles );
    }

    public RequiredBundle[] getRequiredBundles( String symbolicName )
    {
        if( m_delegate == null ) // Can this ever happen?
        {
            return new RequiredBundle[0];
        }
        return m_delegate.getRequiredBundles( symbolicName );
    }

    public Bundle[] getBundles( String symbolicName, String versionRange )
    {
        if( m_delegate == null ) // Can this ever happen?
        {
            return new Bundle[0];
        }
        return m_delegate.getBundles( symbolicName, versionRange );
    }

    public Bundle[] getFragments( Bundle bundle )
    {
        if( m_delegate == null ) // Can this ever happen?
        {
            return new Bundle[0];
        }
        return m_delegate.getFragments( bundle );
    }

    public Bundle[] getHosts( Bundle bundle )
    {
        if( m_delegate == null ) // Can this ever happen?
        {
            return new Bundle[0];
        }
        return m_delegate.getHosts( bundle );
    }

    public Bundle getBundle( Class aClass )
    {
        if( m_delegate == null ) // Can this ever happen?
        {
            return null;
        }
        return m_delegate.getBundle( aClass );
    }

    public int getBundleType( Bundle bundle )
    {
        if( m_delegate == null ) // Can this ever happen?
        {
            return 0;
        }
        return m_delegate.getBundleType( bundle );
    }
}
