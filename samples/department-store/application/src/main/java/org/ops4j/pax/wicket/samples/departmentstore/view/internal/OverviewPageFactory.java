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
package org.ops4j.pax.wicket.samples.departmentstore.view.internal;

import wicket.IPageFactory;
import wicket.Page;
import wicket.PageParameters;
import wicket.markup.html.WebPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.wicket.samples.departmentstore.view.OverviewPage;
import org.ops4j.pax.wicket.service.ContentContainer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.InvalidSyntaxException;

class OverviewPageFactory
    implements IPageFactory
{

    private final Log m_logger = LogFactory.getLog( OverviewPage.class );

    private BundleContext m_context;
    private ContentContainer m_store;

    public OverviewPageFactory( BundleContext context, ContentContainer store )
    {
        m_context = context;
        m_store = store;
    }

    public Page newPage( final Class pageClass, PageParameters params )
    {
        if( OverviewPage.class.isAssignableFrom( pageClass ))
        {
            ServiceReference[] refs;
            try
            {
                refs = m_context.getServiceReferences( WebPage.class.getName(), "(pagename=about)");
            } catch( InvalidSyntaxException e )
            {
                // Can't happen.
                e.printStackTrace();
                return null;
            }
            if( refs.length == 0 )
            {
                return null;
            }
            String classname = (String) refs[0].getProperty( "pageclassname" );
            Class aboutpageClass = null;
            try
            {
                aboutpageClass = refs[0].getBundle().loadClass( classname );
                OverviewPage overviewPage = new OverviewPage( m_store, "Sungei Wang Plaza", aboutpageClass );
                return overviewPage;
            } catch( ClassNotFoundException e )
            {
                m_logger.error( "Class '" + classname + "' could not be found.");
                return null;
            }
        }
        return null;
    }

    public Page newPage( final Class pageClass )
    {
        return newPage( pageClass, null );
    }
}
