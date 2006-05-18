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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.ops4j.pax.wicket.service.internal.ContentTrackingCallback;
import org.ops4j.pax.wicket.service.internal.DefaultContentTracking;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import wicket.Component;
import wicket.IPageFactory;
import wicket.Page;
import wicket.PageParameters;

public class DefaultPageContainer
    implements ContentContainer, ContentTrackingCallback, PageContent
{

    private String m_containmentId;
    private IPageFactory m_pageFactory;
    private ServiceTracker m_serviceTracker;
    private HashMap<String, List<Content>> m_children;

    public DefaultPageContainer( String containmentId, BundleContext bundleContext, IPageFactory pageFactory )
    {
        m_containmentId = containmentId;
        m_pageFactory = pageFactory;
        m_children = new HashMap<String, List<Content>>();
        DefaultContentTracking contentTracking = new DefaultContentTracking( bundleContext, this );
        contentTracking.setContainmentId( m_containmentId );
        m_serviceTracker = new ServiceTracker( bundleContext, Content.class.getName(), contentTracking );
        m_serviceTracker.open();
    }

    public final String getContainmentID()
    {
        return m_containmentId;
    }

    public final List<Component> createComponents( String id )
    {
        ArrayList<Component> result = new ArrayList<Component>();
        List<Content> contents = m_children.get( id );
        if( contents != null )
        {
            for( Content content : contents )
            {
                result.add( content.createComponent() );
            }
        }
        return result;
    }

    public final void dispose()
    {
        m_serviceTracker.close();
    }

    public final void addContent( String id, Content content )
    {
        List<Content> contents = m_children.get( id );
        if( contents == null )
        {
            contents = new ArrayList<Content>();
            m_children.put( id, contents );
        }
        contents.add( content );
    }

    public final void removeContent( String id, Content content )
    {
        List<Content> contents = m_children.get( id );
        if( contents == null )
        {
            return;
        }
        contents.remove( content );
        if( contents.isEmpty() )
        {
            m_children.remove( id );
        }
    }

    public Page createPage( Class cls, PageParameters params )
    {
        return m_pageFactory.newPage( cls, params );
    }
}
