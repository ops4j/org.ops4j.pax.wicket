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

import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public abstract class AbstractPageContent
    implements PageContent
{

    private BundleContext m_bundleContext;
    private String m_applicationName;
    private String m_pageName;
    private ServiceRegistration m_serviceRegistration;

    protected AbstractPageContent( BundleContext bundleContext, String applicationName, String pageName)
    {
        m_bundleContext = bundleContext;
        m_applicationName = applicationName;
        m_pageName = pageName;
    }

    public final void register()
    {
        Properties props = new Properties();
        props.put( Content.APPLICATION_NAME, m_applicationName );
        props.put( Content.PAGE_NAME, m_pageName );
        m_serviceRegistration = m_bundleContext.registerService( PageContent.class.getName(), this, props );
    }

    public final void dispose()
    {
        m_serviceRegistration.unregister();
    }
}
