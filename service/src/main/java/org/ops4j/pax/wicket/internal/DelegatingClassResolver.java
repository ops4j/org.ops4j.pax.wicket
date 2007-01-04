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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.ContentSource;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import wicket.application.IClassResolver;


public final class DelegatingClassResolver implements IClassResolver
{

    private static final Logger m_logger = Logger.getLogger( DelegatingClassResolver.class );

    private final BundleContext m_context;
    private final String m_applicationName;
    private final List<IClassResolver> m_resolvers;

    private ClassResolverTracker m_tracker;

    public DelegatingClassResolver( BundleContext context, String applicationName )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( context, "context" );
        NullArgumentException.validateNotEmpty( applicationName, "applicationName" );

        m_context = context;
        m_applicationName = applicationName;
        m_resolvers = new ArrayList<IClassResolver>();
    }

    public final void intialize()
        throws IllegalStateException
    {
        synchronized ( this )
        {
            if( m_tracker != null )
            {
                throw new IllegalStateException(
                    "DelegatingClassResolver [" + this + "] had been initialized."
                );
            }

            m_tracker = new ClassResolverTracker( m_context, m_applicationName );
            m_tracker.open();

        }
    }

    public void dispose()
        throws IllegalStateException
    {
        synchronized ( this )
        {
            if( m_tracker == null )
            {
                throw new IllegalStateException(
                    "DelegatingClassResolver [" + this + "] had not been initialized."
                );
            }

            m_tracker.close();
            m_tracker = null;
        }
    }

    /**
     * Resolves a class by name (which may or may not involve loading it; thus the name class *resolver* not *loader*).
     *
     * @param classname
     *            Fully qualified classname to find
     *
     * @return Class
     */
    public Class resolveClass( final String classname )
    {
        for ( IClassResolver resolver : m_resolvers )
        {
            try
            {
                Class candidate = resolver.resolveClass( classname );
                if ( candidate != null )
                {
                    return candidate;
                }
            }
            catch ( RuntimeException e )
            {
                m_logger.warn( "ClassResolver" + resolver + " threw an unexpected exception.", e );
            }
        }
        return null;
    }

    private Filter createFilter( String applicationName )
    {
        String filterStr = "(&(" + Constants.OBJECTCLASS + "=" + IClassResolver.class.getName() + ")("
                + ContentSource.APPLICATION_NAME + "=" + applicationName + "))";

        try
        {
            return m_context.createFilter( filterStr );
        }
        catch ( InvalidSyntaxException e )
        {
            String message = ContentSource.APPLICATION_NAME + "[" + applicationName + "] has an invalid format. ";
            throw new IllegalArgumentException( message );
        }
    }


    private class ClassResolverTracker extends ServiceTracker
    {

        private final BundleContext m_context;
        private final String m_applicationName;
        private final List<ServiceReference> m_references;

        public ClassResolverTracker( BundleContext context, String applicationName )
        {
            super( context, createFilter( applicationName ), null );

            m_context = context;
            m_applicationName = applicationName;
            m_references = new LinkedList<ServiceReference>();
        }

        @Override
        public synchronized void close()
        {
            synchronized ( DelegatingClassResolver.this )
            {
                for( ServiceReference reference : m_references )
                {
                    m_context.ungetService( reference );
                }

                m_references.clear();
            }

            super.close();
        }

        public Object addingService( ServiceReference reference )
        {
            IClassResolver resolver = ( IClassResolver ) m_context.getService( reference );

            synchronized( DelegatingClassResolver.this )
            {
                m_resolvers.add( resolver );
                m_references.add( reference );
            }

            return resolver;
        }

        public void modifiedService( ServiceReference reference, Object service )
        {
            String applName = ( String ) reference.getProperty( ContentSource.APPLICATION_NAME );
            if ( m_applicationName.equals( applName ) )
            {
                return;
            }

            removedService( reference, service );
        }

        public void removedService( ServiceReference reference, Object service )
        {
            IClassResolver resolver = ( IClassResolver ) service;

            synchronized( DelegatingClassResolver.this )
            {
                m_resolvers.remove( resolver );
                m_references.remove( reference );

                m_context.ungetService( reference );
            }
        }
    }
}
