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

import java.util.Dictionary;
import java.util.Hashtable;
import org.ops4j.lang.NullArgumentException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

public abstract class AbstractPageContent
    implements PageContent, ManagedService
{

    private BundleContext m_bundleContext;
    private ServiceRegistration m_serviceRegistration;
    private Hashtable<String, String> m_properties;

    protected AbstractPageContent( BundleContext bundleContext, String pageId, String applicationName, String pageName )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( bundleContext, "bundleContext" );
        NullArgumentException.validateNotEmpty( pageId, "pageId" );
        NullArgumentException.validateNotEmpty( applicationName, "applicationName" );
        NullArgumentException.validateNotEmpty( pageName, "pageName" );

        m_properties = new Hashtable<String, String>();
        m_bundleContext = bundleContext;
        setApplicationName( applicationName );
        setPageName( pageName );
        m_properties.put( Constants.SERVICE_PID, Content.PAGE_ID + "/" + pageId );
    }

    public final void register()
    {
        String[] classes = { PageContent.class.getName(), ManagedService.class.getName() };
        m_serviceRegistration = m_bundleContext.registerService( classes, this, m_properties );
    }

    public final void dispose()
    {
        m_serviceRegistration.unregister();
    }

    public final String getApplicationName()
    {
        return m_properties.get( Content.APPLICATION_NAME );
    }

    public final String getPageName()
    {
        return m_properties.get( Content.PAGE_NAME );
    }

    public void updated( Dictionary config )
        throws ConfigurationException
    {
        if( config == null )
        {
            m_serviceRegistration.setProperties( m_properties );
            return;
        }
        String pagename = (String) config.get( Content.PAGE_NAME );
        String appname = (String) config.get( Content.APPLICATION_NAME );
        setPageName( pagename );
        setApplicationName( appname );
        m_serviceRegistration.setProperties( config );
    }

    protected final void setApplicationName( String applicationName )
    {
        m_properties.put( Content.APPLICATION_NAME, applicationName );
    }

    protected final void setPageName( String pageName )
    {
        m_properties.put( Content.PAGE_NAME, pageName );
    }
}
