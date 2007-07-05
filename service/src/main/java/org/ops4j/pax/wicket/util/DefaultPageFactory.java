/*  Copyright 2007 Niclas Hedhman.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.util;

import java.lang.reflect.UndeclaredThrowableException;

import org.ops4j.pax.wicket.api.BookmarkableInfo;
import org.ops4j.pax.wicket.api.PageFactory;
import org.osgi.framework.BundleContext;
import wicket.Page;
import wicket.PageParameters;

public class DefaultPageFactory<T extends Page> extends AbstractPageFactory<T>
{
    private Class<T> m_pageClass;
    private final String m_niceUrlPath;

    public DefaultPageFactory( 
            BundleContext bundleContext, 
            String pageId, 
            String applicationName, 
            String pageName,
            Class<T> pageClass )
        throws IllegalArgumentException
    {
        this( bundleContext, pageId, applicationName, pageName, pageClass, null );
    }

    public DefaultPageFactory( 
            BundleContext bundleContext, 
            String pageId, 
            String applicationName, 
            String pageName,
            Class<T> pageClass,
            String niceUrlPath )
        throws IllegalArgumentException
    {
        super( bundleContext, pageId, applicationName, pageName );
        m_pageClass = pageClass;
        m_niceUrlPath = niceUrlPath;
    }

    public Class<T> getPageClass()
    {
        return m_pageClass;
    }

    public T createPage( PageParameters params )
    {
        try
        {
            return m_pageClass.newInstance();
        } catch( InstantiationException e )
        {
            throw new UndeclaredThrowableException( e );
        } catch( IllegalAccessException e )
        {
            throw new UndeclaredThrowableException( e );
        }
    }

    public BookmarkableInfo<T> getBookmarkableInfo()
    {
        if( null != m_niceUrlPath )
            return new DefaultBookmarkableInfo<T>( m_niceUrlPath, m_pageClass );

        return null;
    }
}
