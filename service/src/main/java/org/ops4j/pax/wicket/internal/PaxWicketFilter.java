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

import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WicketFilter;

public class PaxWicketFilter extends WicketFilter
{

    private final IWebApplicationFactory m_appFactory;

    PaxWicketFilter( IWebApplicationFactory appFactory )
    {
        m_appFactory = appFactory;
    }

    @Override
    protected IWebApplicationFactory getApplicationFactory()
    {
        return m_appFactory;
    }

    @Override
    protected String getFilterPath( HttpServletRequest request )
    {
        return super.getFilterPath( request );
    }

    @Override
    public String getRelativePath( HttpServletRequest request )
    {
        String path = super.getRelativePath( request );
        return path;
    }

    /**
     * @return The filter config of this WicketFilter
     */
    @Override
    public FilterConfig getFilterConfig()
    {
        FilterConfig config = super.getFilterConfig();
        if( config == null )
        {
            return new PaxFilterConfig();
        }
        return config;
    }

    private class PaxFilterConfig
        implements FilterConfig
    {

        public String getFilterName()
        {
            return "pax-wicket";
        }

        public ServletContext getServletContext()
        {
            return null;
        }

        public String getInitParameter( String param )
        {
            return null;
        }

        public Enumeration getInitParameterNames()
        {
            return new Enumeration()
            {
                public boolean hasMoreElements()
                {
                    return false;
                }

                public Object nextElement()
                {
                    throw new NoSuchElementException();
                }
            };
        }
    }
}
