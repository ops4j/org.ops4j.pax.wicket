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

import java.util.Iterator;
import java.util.HashSet;
import org.apache.wicket.application.IClassResolver;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.packageadmin.ExportedPackage;

public class BundleDelegatingClassResolver
    implements IClassResolver, BundleListener
{
    private HashSet<ExportedPackage> m_packages;
    private PackageAdminTracker m_packageAdmin;

    public BundleDelegatingClassResolver( BundleContext context )
    {
        m_packages = new HashSet<ExportedPackage>();
        m_packageAdmin = new PackageAdminTracker( context );
    }

    public Class resolveClass( String classname )
        throws ClassNotFoundException
    {
        String packageName = extractPackageName( classname );
        if( packageName == null )
        {
            return null;
        }
        for( ExportedPackage pakkage : m_packages )
        {
            if( packageName.equals( pakkage.getName() ) )
            {
                return pakkage.getExportingBundle().loadClass( classname );
            }
        }
        return null;
    }

    private String extractPackageName( String classname )
    {
        int lastDot = classname.lastIndexOf( '.' );
        if( lastDot < 0 )
        {
            return null;
        }
        return classname.substring( 0, lastDot );
    }

    public void bundleChanged( BundleEvent bundleEvent )
    {
        int type = bundleEvent.getType();
        if( type == Bundle.UNINSTALLED )
        {
            Bundle bundle = bundleEvent.getBundle();
            Iterator<ExportedPackage> iter = m_packages.iterator();
            while( iter.hasNext() )
            {
                ExportedPackage pakkage = iter.next();
                if( pakkage.getExportingBundle().equals( bundle ))
                {
                    iter.remove();
                }
            }
        }
        
        if( type == Bundle.RESOLVED )
        {
            Bundle bundle = bundleEvent.getBundle();
            ExportedPackage[] pakkages = m_packageAdmin.getExportedPackages( bundle );
            for( ExportedPackage pakkage : pakkages )
            {
                if( ! pakkage.isRemovalPending() )
                {
                    m_packages.add( pakkage );
                }
            }
        }
    }
}
