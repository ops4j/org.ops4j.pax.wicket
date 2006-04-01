/*
 * Copyright 2005 Niclas Hedhman.
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.servicemanager.ServiceManager;
import wicket.IPageFactory;
import wicket.Page;
import wicket.PageParameters;
import wicket.WicketRuntimeException;

public class PageFactory
    implements IPageFactory
{
    private IPageFactory m_DefaultFactory;
    private ServiceManager m_ServiceManager;

    public PageFactory( IPageFactory defaultFactory, ServiceManager serviceManager )
    {
        m_DefaultFactory = defaultFactory;
        m_ServiceManager = serviceManager;
    }

    /**
     * Creates a new page using a page class.
     *
     * @param pageClass The page class to instantiate
     *
     * @return The page
     *
     * @throws wicket.WicketRuntimeException Thrown if the page cannot be constructed
     */
    public Page newPage( Class pageClass )
    {
        Log logger = LogFactory.getLog( PageFactory.class );
        logger.debug( "PageFactory.newPage( " + pageClass + " );" );
        Page newPage = null;
        try
        {
            Class[] types = new Class[]{ ServiceManager.class };
            Constructor constructor = pageClass.getConstructor( types );
            Object[] args = new Object[]{ m_ServiceManager };
            newPage = (Page) constructor.newInstance( args );
        }
        catch( IllegalAccessException e )
        {
            throw new WicketRuntimeException( "The " + pageClass + " or its constructor is not public.", e );
        }
        catch( NoSuchMethodException e )
        {
            // The Page is not a Pax Wicket compatible page. Let's try to load it with the default factory.
            newPage = m_DefaultFactory.newPage( pageClass );
        }
        catch( InvocationTargetException e )
        {
            throw new WicketRuntimeException( "An exception occurred in the constructor of " + pageClass + ".", e );
        }
        catch( InstantiationException e )
        {
            throw new WicketRuntimeException( "The " + pageClass + " is abstract or an interface.", e );
        }
        return newPage;
    }

    /**
     * Creates a new Page, passing PageParameters to the Page constructor if such a constructor exists. If no such
     * constructor exists and the parameters argument is null or empty, then any available default constructor will be
     * used.
     *
     * @param pageClass  The class of Page to create
     * @param parameters Any parameters to pass to the Page's constructor
     *
     * @return The new page
     *
     * @throws wicket.WicketRuntimeException Thrown if the page cannot be constructed
     */
    public Page newPage( Class pageClass, PageParameters parameters )
    {
        Log logger = LogFactory.getLog( PageFactory.class );
        logger.debug( "PageFactory.newPage( " + pageClass + ", " + parameters + " );" );
        Page newPage = null;
        if( parameters == null || parameters.size() == 0 )
        {
            return newPage( pageClass );
        }
        try
        {
            Class[] types = new Class[]{ ServiceManager.class, PageParameters.class };
            Constructor constructor = pageClass.getConstructor( types );
            Object[] args = new Object[]{ m_ServiceManager, parameters };
            newPage = (Page) constructor.newInstance( args );
        }
        catch( IllegalAccessException e )
        {
            throw new WicketRuntimeException( "The " + pageClass + " or its constructor is not public.", e );
        }
        catch( NoSuchMethodException e )
        {
            // The Page is not a Pax Wicket compatible page. Let's try to load it with the default factory.
            newPage = m_DefaultFactory.newPage( pageClass, parameters );
        }
        catch( InvocationTargetException e )
        {
            throw new WicketRuntimeException( "An exception occurred in the constructor of " + pageClass + ".", e );
        }
        catch( InstantiationException e )
        {
            throw new WicketRuntimeException( "The " + pageClass + " is abstract or an interface.", e );
        }
        return newPage;
    }
}
