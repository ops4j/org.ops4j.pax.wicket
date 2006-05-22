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

import org.ops4j.pax.wicket.samples.departmentstore.view.OverviewPage;
import org.ops4j.pax.wicket.service.ContentContainer;
import org.ops4j.pax.wicket.service.PageContent;
import org.ops4j.pax.wicket.service.PageFilterFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import wicket.IPageFactory;
import wicket.Page;
import wicket.PageParameters;

class OverviewPageFactory
    implements IPageFactory
{
    private BundleContext m_context;
    private ContentContainer m_store;

    public OverviewPageFactory( BundleContext context, ContentContainer store )
    {
        m_context = context;
        m_store = store;
    }

    public Page newPage( final Class pageClass, PageParameters params )
    {
        if( OverviewPage.class.isAssignableFrom( pageClass ) )
        {
            ServiceReference[] refs;
            try
            {
                refs = m_context.getServiceReferences( PageContent.class.getName(), PageFilterFactory.createPageFilter( "about", "departmentstore" ) );
            } catch( InvalidSyntaxException e )
            {
                e.printStackTrace();
                return null;
            }
            Class aboutpageClass;
            if( refs == null || refs.length == 0 )
            {
                aboutpageClass = null;
            }
            else
            {
                ServiceReference ref = refs[ 0 ];
                PageContent pageContent = (PageContent) m_context.getService( ref );
                aboutpageClass = pageContent.getPageClass();
            }
            OverviewPage overviewPage = new OverviewPage( m_store, "Sungei Wang Plaza", aboutpageClass );
            return overviewPage;
        }
        return null;
    }

    public Page newPage( final Class pageClass )
    {
        return newPage( pageClass, null );
    }
}
