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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.wicket.service.Content;
import org.ops4j.pax.wicket.service.PageContent;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class PaxWicketPageTracker extends ServiceTracker
{

    private static final Log m_logger = LogFactory.getLog( PaxWicketPageTracker.class );

    private BundleContext m_context;
    private String m_applicationName;
    private PaxWicketPageFactory m_paxWicketPageFactory;

    public PaxWicketPageTracker( BundleContext context, String applicationName,
                                 PaxWicketPageFactory paxWicketPageFactory )
    {
        super( context, TrackingUtil.createAllPageContentFilter( context, applicationName ), null );

        m_context = context;
        m_applicationName = applicationName;
        m_paxWicketPageFactory = paxWicketPageFactory;
    }

    /**
     * Default implementation of the
     * <code>ServiceTrackerCustomizer.addingService</code> method.
     *
     * <p>
     * This method is only called when this <code>ServiceTracker</code> object
     * has been constructed with a <code>null ServiceTrackerCustomizer</code>
     * argument.
     *
     * The default implementation returns the result of calling
     * <code>getService</code>, on the <code>BundleContext</code> object
     * with which this <code>ServiceTracker</code> object was created, passing
     * the specified <code>ServiceReference</code> object.
     * <p>
     * This method can be overridden in a subclass to customize the service
     * object to be tracked for the service being added. In that case, take care
     * not to rely on the default implementation of removedService that will
     * unget the service.
     *
     * @param reference Reference to service being added to this
     *                  <code>ServiceTracker</code> object.
     *
     * @return The service object to be tracked for the service added to this
     *         <code>ServiceTracker</code> object.
     *
     * @see org.osgi.util.tracker.ServiceTrackerCustomizer
     */
    public Object addingService( ServiceReference reference )
    {
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "addingService( " + reference + ");" );
        }
        PageContent page = (PageContent) m_context.getService( reference );
        Class pageClass = page.getPageClass();
        m_paxWicketPageFactory.add( pageClass, page );
        return page;
    }

    /**
     * Default implementation of the
     * <code>ServiceTrackerCustomizer.modifiedService</code> method.
     *
     * <p>
     * This method is only called when this <code>ServiceTracker</code> object
     * has been constructed with a <code>null ServiceTrackerCustomizer</code>
     * argument.
     *
     * The default implementation does nothing.
     *
     * @param reference Reference to modified service.
     * @param service   The service object for the modified service.
     *
     * @see org.osgi.util.tracker.ServiceTrackerCustomizer
     */
    public void modifiedService( ServiceReference reference, Object service )
    {
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "modifiedService( " + reference + ", " + service + ");" );
        }
        PageContent page = (PageContent) m_context.getService( reference );
        String appName = (String) reference.getProperty( Content.APPLICATION_NAME );
        if( !m_applicationName.equals( appName ) )
        {
            Class pageClass = page.getPageClass();
            m_paxWicketPageFactory.remove( pageClass );
        }
    }

    /**
     * Default implementation of the
     * <code>ServiceTrackerCustomizer.removedService</code> method.
     *
     * <p>
     * This method is only called when this <code>ServiceTracker</code> object
     * has been constructed with a <code>null ServiceTrackerCustomizer</code>
     * argument.
     *
     * The default implementation calls <code>ungetService</code>, on the
     * <code>BundleContext</code> object with which this
     * <code>ServiceTracker</code> object was created, passing the specified
     * <code>ServiceReference</code> object.
     * <p>
     * This method can be overridden in a subclass. If the default
     * implementation of <code>addingService</code> method was used, this
     * method must unget the service.
     *
     * @param reference Reference to removed service.
     * @param service   The service object for the removed service.
     *
     * @see org.osgi.util.tracker.ServiceTrackerCustomizer
     */
    public void removedService( ServiceReference reference, Object service )
    {
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "removedService( " + reference + ", " + service + ");" );
        }
        PageContent page = (PageContent) service;
        Class pageclass = page.getPageClass();
        m_paxWicketPageFactory.remove( pageclass );
    }
}
