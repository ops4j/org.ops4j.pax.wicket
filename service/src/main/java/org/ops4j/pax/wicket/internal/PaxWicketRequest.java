/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2006 Edward F. Yakop
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

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import wicket.protocol.http.servlet.ServletWebRequest;

/**
 * @author Niclas Hedhman, Edward Yakop
 *
 * @since 1.0.0
 */
final class PaxWicketRequest extends ServletWebRequest
{

    private static final Logger m_logger = Logger.getLogger( PaxWicketRequest.class );
    private final String m_mountPoint;

    /**
     * Protected constructor.
     *
     * @param point
     *
     * @param httpServletRequest The servlet request information
     */
    PaxWicketRequest( String mountPoint, HttpServletRequest httpServletRequest )
    {
        super( httpServletRequest );
        m_mountPoint = "/" + mountPoint;
    }

    /**
     * Gets the servlet path.
     *
     * @return Servlet path
     */
    public final String getServletPath()
    {
        String contextPath = getHttpServletRequest().getContextPath();
        if ( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "getServletPath() : " + contextPath );
        }

        return contextPath;
    }

    /**
     * Gets the servlet context path.
     *
     * @return Servlet context path
     */
    public final String getContextPath()
    {
        String servletPath = getHttpServletRequest().getServletPath();
        if ( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "getContextPath() : " + servletPath );
        }

        int mountPointLength = m_mountPoint.length();
        int servletPathLength = servletPath.length();
        if ( servletPathLength == mountPointLength )
        {
            servletPath = servletPath + "/";
        }
        else
        {
            char aChar = servletPath.charAt( mountPointLength );
            if ( '/' != aChar )
            {
                String suffix = servletPath.substring( mountPointLength );
                servletPath = m_mountPoint + '/' + suffix;
            }
        }

        return servletPath;
    }

    public final String getPath()
    {
        String path = super.getPath();

        if ( path == null )
        {
            path = "/";
        }
        else if ( !path.startsWith( "/" ) )
        {
            path = "/" + path;
        }

        return path;
    }
}
