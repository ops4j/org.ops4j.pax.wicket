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
package org.ops4j.pax.wicket.service.internal;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.ops4j.pax.wicket.service.Content;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import wicket.application.IClassResolver;

public class DelegatingClassResolver
    implements IClassResolver
{

    private static final Logger m_logger = Logger.getLogger( DelegatingClassResolver.class );
    private BundleContext m_context;
    private List<IClassResolver> m_resolvers;
    private ClassResolverTracker m_tracker;

    public DelegatingClassResolver( BundleContext context, String applicationName )
    {
        m_context = context;
        m_resolvers = new ArrayList<IClassResolver>();
        m_tracker = new ClassResolverTracker( context, applicationName );
    }

    public void intialize()
    {
        m_tracker.open();
    }

    public void dispose()
    {
        m_tracker.close();
    }

    /**
     * Resolves a class by name (which may or may not involve loading it; thus
     * the name class *resolver* not *loader*).
     *
     * @param classname Fully qualified classname to find
     *
     * @return Class
     */
    public Class resolveClass( final String classname )
    {
        for( IClassResolver resolver : m_resolvers )
        {
            try
            {
                Class candidate = resolver.resolveClass( classname );
                if( candidate != null )
                {
                    return candidate;
                }
            } catch( RuntimeException e )
            {
                m_logger.warn( "ClassResolver" + resolver + " threw an unexpected exception.", e );
            }
        }
        return null;
    }

    private Filter createFilter( String applicationName )
    {
        String filterStr = "(&(" + Constants.OBJECTCLASS + "=" + IClassResolver.class.getName() + ")("
                           + Content.APPLICATION_NAME + "=" + applicationName + "))";

        try
        {
            return m_context.createFilter( filterStr );
        } catch( InvalidSyntaxException e )
        {
            String message = Content.APPLICATION_NAME + "[" + applicationName + "] has an invalid format. ";
            throw new IllegalArgumentException( message );
        }
    }

    private class ClassResolverTracker extends ServiceTracker
    {

        private BundleContext m_context;
        private String m_applicationName;

        public ClassResolverTracker( BundleContext context, String applicationName )
        {
            super( context, createFilter( applicationName ), null );
            m_context = context;
            m_applicationName = applicationName;
        }

        public Object addingService( ServiceReference reference )
        {
            IClassResolver resolver = (IClassResolver) m_context.getService( reference );
            m_resolvers.add( resolver );
            return resolver;
        }

        public void modifiedService( ServiceReference reference, Object service )
        {
            String applName = (String) reference.getProperty( Content.APPLICATION_NAME );
            if( m_applicationName.equals( applName ) )
            {
                return;
            }
            IClassResolver resolver = (IClassResolver) service;
            m_resolvers.remove( resolver );
        }

        public void removedService( ServiceReference reference, Object service )
        {
            IClassResolver resolver = (IClassResolver) service;
            m_resolvers.remove( resolver );
        }

    }
}
