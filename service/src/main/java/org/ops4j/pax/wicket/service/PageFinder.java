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
package org.ops4j.pax.wicket.service;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class PageFinder
{

    public static PageContent[] findPages( BundleContext context, String applicationName, String pageName )
    {
        String filter = "(&(" + Content.APPLICATION_NAME + "=" + applicationName + ")"
                              + "(" + Content.PAGE_NAME + "=" + pageName + "))";
        try
        {
            ServiceReference[] refs = context.getServiceReferences( PageContent.class.getName(), filter );
            if( refs == null )
            {
                return new PageContent[0];
            }
            PageContent[] pages = new PageContent[ refs.length ];
            int count = 0;
            for( ServiceReference ref : refs )
            {
                pages[ count++ ] = (PageContent) context.getService( ref );
            }
            return pages;
        } catch( InvalidSyntaxException e )
        {
            e.printStackTrace();
            // can not happen, RIGHT!
            return new PageContent[0];
        }
    }
}
