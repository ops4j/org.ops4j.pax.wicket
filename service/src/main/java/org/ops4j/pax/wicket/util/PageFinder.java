/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2007 David Leangen
 *
 * you may not use  this file  except in  compliance with the License.
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
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
package org.ops4j.pax.wicket.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Page;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.api.PageFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class PageFinder
{

    private static final Log LOGGER = LogFactory.getLog( PageFinder.class );

    /**
     * Returns the page content from the specified {@code context} for the specified {@code applicationName} and
     * {@code pageName}.
     *
     * @param <T>             The page subclass.
     * @param context         The bundle context. This argument must not be {@code null}.
     * @param applicationName The application name. This argument must not be {@code null} or empty.
     * @param pageName        The page name. This argument must not be {@code null} or empty.
     *
     * @return The page of the specified {@code applicationName} and {@code pageName}.
     *
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null} or empty.
     * @since 1.0.0
     */
    @SuppressWarnings( "unchecked" )
    public static <T extends Page> PageFactory<T>[] findPages(
        BundleContext context, String applicationName, String pageName )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( context, "context" );
        NullArgumentException.validateNotEmpty( applicationName, "applicationName" );
        NullArgumentException.validateNotEmpty( pageName, "pageName" );

        String filter =
            "(&(" + ContentSource.APPLICATION_NAME + "=" + applicationName + ")" + "(" + ContentSource.PAGE_NAME + "="
            + pageName + "))";
        try
        {
            ServiceReference[] refs = context.getServiceReferences( PageFactory.class.getName(), filter );
            if( refs == null )
            {
                return new PageFactory[0];
            }
            PageFactory<T>[] pageSources = new PageFactory[refs.length];
            int count = 0;
            for( ServiceReference ref : refs )
            {
                pageSources[ count++ ] = (PageFactory<T>) context.getService( ref );
            }
            return pageSources;
        }
        catch( InvalidSyntaxException e )
        {
            LOGGER.warn( "Invalid syntax [" + filter + "]. This should not happen unless if both application name " +
                         "and page name contains ldap filters.", e
            );

            // can not happen, RIGHT!
            return new PageFactory[0];
        }
    }
}
