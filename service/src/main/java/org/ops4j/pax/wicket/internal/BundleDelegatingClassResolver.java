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

import java.util.HashSet;
import org.apache.wicket.application.IClassResolver;
import org.ops4j.pax.wicket.api.ContentAggregator;
import org.ops4j.pax.wicket.api.ContentSource;
import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;
import org.ops4j.pax.wicket.api.PageFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class BundleDelegatingClassResolver extends ServiceTracker
    implements IClassResolver
{

    private static final String FILTER;

    static
    {
        FILTER = "(|" +
                 "(objectClass=" + ContentSource.class.getName() + ")" +
                 "(objectClass=" + ContentAggregator.class.getName() + ")" +
                 "(objectClass=" + PageFactory.class.getName() + ")" +
                 ")";
    }

    private HashSet<Bundle> bundles;
    private final String applicationName;
    private final Bundle paxWicketbundle;

    public BundleDelegatingClassResolver( BundleContext aContext, String anApplicationName, Bundle aPaxWicketBundle )
    {
        super( aContext, createFilter( aContext ), null );

        applicationName = anApplicationName;
        paxWicketbundle = aPaxWicketBundle;
        bundles = new HashSet<Bundle>();
        bundles.add( aPaxWicketBundle );

        open( true );
    }

    public Class resolveClass( String classname )
        throws ClassNotFoundException
    {
        for( Bundle bundle : bundles )
        {
            try
            {
                return bundle.loadClass( classname );
            } catch( ClassNotFoundException e )
            {
                // ignore, expected in many cases.
            } catch( IllegalStateException e )
            {
                // if the bundle has been uninstalled.
            }
        }

        throw new ClassNotFoundException( "Class [" + classname + "] can't be resolved." );
    }

    public Object addingService( ServiceReference serviceReference )
    {
        String appName = (String) serviceReference.getProperty( APPLICATION_NAME );
        if( !applicationName.equals( appName ) )
        {
            return null;
        }
        synchronized( this )
        {
            Bundle bundle = serviceReference.getBundle();
            HashSet<Bundle> clone = (HashSet<Bundle>) bundles.clone();
            clone.add( bundle );
            bundles = clone;
        }
        return super.addingService( serviceReference );
    }

    public void removedService( ServiceReference serviceReference, Object o )
    {
        String appName = (String) serviceReference.getProperty( APPLICATION_NAME );
        if( !applicationName.equals( appName ) )
        {
            return;
        }
        HashSet<Bundle> revisedSet = new HashSet<Bundle>();
        revisedSet.add( paxWicketbundle );
        try
        {
            ServiceReference[] serviceReferences = context.getAllServiceReferences( null, FILTER );
            for( ServiceReference ref : serviceReferences )
            {
                revisedSet.add( ref.getBundle() );
            }
            bundles = revisedSet;
        } catch( InvalidSyntaxException e )
        {
            // Can not happen.
        }
        super.removedService( serviceReference, o );
    }

    private static Filter createFilter( BundleContext context )
    {
        try
        {
            return context.createFilter( FILTER );
        }
        catch( InvalidSyntaxException e )
        {
            // Should not happened
            e.printStackTrace();
        }

        return null;
    }
}
