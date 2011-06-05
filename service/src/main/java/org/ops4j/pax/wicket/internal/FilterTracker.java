/*
* Copyright 2011 Fabian Souczek
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package org.ops4j.pax.wicket.internal;

import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;
import static org.osgi.framework.Constants.OBJECTCLASS;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FilterTracker extends ServiceTracker
{
    private static final Logger LOGGER = LoggerFactory.getLogger( FilterTracker.class );

    private Map<String, Filter> m_filters = new HashMap<String, Filter>();

    public FilterTracker( BundleContext bundleContext, String applicationName )
    {
        super( bundleContext, createOsgiFilter( bundleContext, applicationName ), null );
    }

    @Override
    public final Object addingService( ServiceReference reference )
    {
        Filter filter = (Filter) super.addingService( reference );
        synchronized ( m_filters  )
        {
            m_filters.put( filter.getClass().getName(), filter );
        }
        LOGGER.info( "added filter of type {}", filter.getClass().getName() );
        return filter;
    }

    @Override
    public void removedService( ServiceReference reference, Object service )
    {
        synchronized ( m_filters )
        {
            m_filters.remove( service.getClass().getName() );
        }
        LOGGER.info( "removed filter of type {}", filter.getClass().getName() );
        super.removedService( reference, service );
    }

    private static org.osgi.framework.Filter createOsgiFilter( BundleContext bundleContext, String applicationName )
        throws IllegalArgumentException
    {
        validateNotNull( bundleContext, "bundleContext" );
        validateNotEmpty( applicationName, "applicationName" );

        org.osgi.framework.Filter filter;
        try
        {
            String filterString = String.format( "(&(%s=%s)(%s=%s))", APPLICATION_NAME, applicationName,
                                  OBJECTCLASS, Filter.class.getName() );
            LOGGER.debug( "apply FilterTracker with OsgiFilter={}", filterString );
            filter = bundleContext.createFilter( filterString );
        }
        catch( InvalidSyntaxException e )
        {
            throw new IllegalArgumentException( "applicationName can not contain '*', '(' or ')' : " + applicationName
            );
        }
        return filter;
    }

    public Filter getFilter( String type )
    {
        validateNotNull( type, "type" );

        return m_filters.get( type );
    }

}

