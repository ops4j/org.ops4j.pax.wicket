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
package org.ops4j.pax.wicket.internal;

import java.util.HashMap;
import org.apache.wicket.IPageFactory;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.WicketRuntimeException;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import org.ops4j.pax.wicket.api.PageFactory;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PaxWicketPageFactory
    implements IPageFactory
{

    private static final Logger LOGGER = LoggerFactory.getLogger( PaxWicketPageFactory.class );

    private final BundleContext bundleContext;
    private final String applicationName;
    private final HashMap<Class, PageFactory> contents;

    private ServiceTracker pageTracker;

    public PaxWicketPageFactory( BundleContext aBundleContext, String anApplicationName )
    {
        contents = new HashMap<Class, PageFactory>();
        bundleContext = aBundleContext;
        applicationName = anApplicationName;
    }

    public final void initialize()
    {
        pageTracker = new PaxWicketPageTracker( bundleContext, applicationName, this );
        pageTracker.open();
    }

    public final void dispose()
    {
        synchronized( this )
        {
            contents.clear();
            pageTracker.close();
        }
    }

    /**
     * Creates a new page using a page class.
     *
     * @param pageClass The page class to instantiate
     *
     * @return The page
     *
     * @throws org.apache.wicket.WicketRuntimeException
     *          Thrown if the page cannot be constructed
     */
    public final Page newPage( Class pageClass )
        throws IllegalArgumentException
    {
        validateNotNull( pageClass, "pageClass" );

        return newPage( pageClass, null );
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
     * @throws org.apache.wicket.WicketRuntimeException
     *          Thrown if the page cannot be constructed
     */
    public final Page newPage( Class pageClass, PageParameters parameters )
        throws IllegalArgumentException
    {
        validateNotNull( pageClass, "pageClass" );

        PageFactory content;
        synchronized( this )
        {
            content = contents.get( pageClass );
        }
        if( content == null )
        {
            try
            {
                return (Page) pageClass.newInstance();
            }
            catch( InstantiationException e )
            {
                String message = "An abstract class or an interface was requested to be a Page: " + pageClass;
                LOGGER.error( message, e );
                throw new WicketRuntimeException( message, e );
            }
            catch( IllegalAccessException e )
            {
                String message = "The constructor in " + pageClass + " is not public and without parameters.";
                LOGGER.error( message, e );
                throw new WicketRuntimeException( message, e );
            }
        }

        return content.createPage( parameters );
    }

    public void add( Class pageClass, PageFactory pageSource )
        throws IllegalArgumentException
    {
        validateNotNull( pageClass, "pageClass" );
        validateNotNull( pageSource, "pageSource" );

        synchronized( this )
        {
            contents.put( pageClass, pageSource );
        }
    }

    public final void remove( Class pageClass )
        throws IllegalArgumentException
    {
        validateNotNull( pageClass, "pageClass" );

        synchronized( this )
        {
            contents.remove( pageClass );
        }
    }
}
