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
import java.util.List;
import org.apache.wicket.application.IClassResolver;
import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;
import org.osgi.framework.BundleContext;
import static org.osgi.framework.Constants.OBJECTCLASS;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DelegatingClassResolver
    implements IClassResolver
{

    private static final Logger LOGGER = LoggerFactory.getLogger( DelegatingClassResolver.class );

    private final BundleContext m_context;
    private final String m_applicationName;
    private final List<IClassResolver> m_resolvers;

    private ClassResolverTracker m_tracker;

    public DelegatingClassResolver( BundleContext context, String applicationName )
        throws IllegalArgumentException
    {
        validateNotNull( context, "context" );
        validateNotEmpty( applicationName, "applicationName" );

        m_context = context;
        m_applicationName = applicationName;
        m_resolvers = new ArrayList<IClassResolver>();
    }

    public final void intialize()
        throws IllegalStateException
    {
        synchronized( this )
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
        synchronized( this )
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
     * @param classname Fully qualified classname to find
     *
     * @return Class
     */
    public Class<?> resolveClass( final String classname )
        throws ClassNotFoundException
    {
        for( IClassResolver resolver : m_resolvers )
        {
            try
            {
                Class<?> candidate = resolver.resolveClass( classname );
                if( candidate != null )
                {
                    return candidate;
                }
            }
            catch( ClassNotFoundException e )
            {
                LOGGER.info( "ClassResolver " + resolver + " could not find class: " + classname );
            }
            catch( RuntimeException e )
            {
                LOGGER.warn( "ClassResolver " + resolver + " threw an unexpected exception.", e );
            }
        }

        throw new ClassNotFoundException( "Class [" + classname + "] can't be resolved." );
    }

    private final class ClassResolverTracker extends ServiceTracker
    {

        private final String m_applicationName;

        ClassResolverTracker( BundleContext context, String applicationName )
        {
            super( context, createFilter( context, applicationName ), null );

            m_applicationName = applicationName;
        }

        @Override
        public final Object addingService( ServiceReference reference )
        {
            IClassResolver resolver = (IClassResolver) super.addingService( reference );

            synchronized( DelegatingClassResolver.this )
            {
                m_resolvers.add( resolver );
            }

            return resolver;
        }

        @Override
        public final void modifiedService( ServiceReference reference, Object service )
        {
            Object objAppName = reference.getProperty( APPLICATION_NAME );
            if( objAppName != null )
            {
                Class<?> nameClass = objAppName.getClass();

                if( String.class.isAssignableFrom( nameClass ) )
                {
                    if( !nameClass.isArray() )
                    {
                        String appName = (String) objAppName;
                        if( m_applicationName.equals( appName ) )
                        {
                            return;
                        }
                    }
                    else
                    {
                        String[] appNames = (String[]) objAppName;
                        for( String appName : appNames )
                        {
                            if( m_applicationName.equals( appName ) )
                            {
                                return;
                            }
                        }
                    }
                }
            }

            removedService( reference, service );
        }

        @Override
        public final void removedService( ServiceReference reference, Object service )
        {
            IClassResolver resolver = (IClassResolver) service;

            synchronized( DelegatingClassResolver.this )
            {
                m_resolvers.remove( resolver );
            }

            super.removedService( reference, service );
        }
    }

    private static Filter createFilter( BundleContext context, String applicationName )
    {
        String filterStr = "(&(" + OBJECTCLASS + "=" + IClassResolver.class.getName() + ")(" + APPLICATION_NAME + "="
                           + applicationName + "))";

        try
        {
            return context.createFilter( filterStr );
        }
        catch( InvalidSyntaxException e )
        {
            String message = APPLICATION_NAME + "[" + applicationName + "] has an invalid format. ";
            throw new IllegalArgumentException( message );
        }
    }
}
