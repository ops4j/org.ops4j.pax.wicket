/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2006 Edward F. Yakop
 * Copyright 2007 David Leangen
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.ops4j.lang.NullArgumentException;

/**
 * @author Niclas Hedhman, Edward Yakop
 * @since 1.0.0
 */
final class PaxWicketRequest extends ServletWebRequest
{

    private static final Log LOGGER = LogFactory.getLog( PaxWicketRequest.class );
    
    private final String m_mountPoint;

    /**
     * Protected constructor.
     *
     * @param mountPoint         The mount point of the application.
     * @param httpServletRequest The servlet request information
     *
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     * @since 1.0.0
     */
    PaxWicketRequest( String mountPoint, HttpServletRequest httpServletRequest )
        throws IllegalArgumentException
    {
        super( httpServletRequest );

        NullArgumentException.validateNotEmpty( mountPoint, "mountPoint" );
        NullArgumentException.validateNotNull( httpServletRequest, "httpServletRequest" );

        if( mountPoint.charAt( 0 ) != '/' )
        {
            mountPoint = "/" + mountPoint;
        }

        m_mountPoint = mountPoint;
    }

    /**
     * Gets the servlet path.
     *
     * @return Servlet path
     */
    @Override
    public final String getServletPath()
    {
        String contextPath = getHttpServletRequest().getContextPath();
        if( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( "getServletPath() : " + contextPath );
        }
        if( !contextPath.endsWith( "/" ) )
        {
            contextPath += "/";
        }

        return contextPath;
    }

//    /**
//     * Gets the servlet context path.
//     *
//     * @return Servlet context path
//     */
//    @Override
//    public final String getContextPath()
//    {
//        HttpServletRequest request = getHttpServletRequest();
//        String servletPath = request.getServletPath();
//
//        if( LOGGER.isDebugEnabled() )
//        {
//            LOGGER.debug( "getContextPath() : " + servletPath );
//        }
//
//        int mountPointLength = m_mountPoint.length();
//        int servletPathLength = servletPath.length();
//        if( servletPathLength == mountPointLength )
//        {
//            servletPath = servletPath + "/";
//        }
//        else
//        {
//            char aChar = servletPath.charAt( mountPointLength );
//            if( '/' != aChar )
//            {
//                String suffix = servletPath.substring( mountPointLength );
//                servletPath = m_mountPoint + '/' + suffix;
//            }
//        }
//
//        return servletPath;
//    }

    @Override
    public String getRelativeURL()
    {
        String url = getServletPath();
        HttpServletRequest servletRequest = getHttpServletRequest();
        String pathInfo = servletRequest.getPathInfo();

        if( pathInfo != null )
        {
            url += pathInfo;
        }

        String queryString = servletRequest.getQueryString();

        if( queryString != null )
        {
            url += ( "?" + queryString );
        }

        // If url is non-empty it has to start with '/', which we should lose
        if( url.length() > 0 )
        {
            // Remove leading '/'
            url = url.substring( 1 );
        }

        return url;
    }

    @Override
    public int getDepthRelativeToWicketHandler()
    {
        return super.getDepthRelativeToWicketHandler();
    }

    @Override
    public String getPath()
    {
        return super.getPath();
    }

    @Override
    public String getRelativePathPrefixToContextRoot()
    {
        return super.getRelativePathPrefixToContextRoot();
    }

    @Override
    public String getRelativePathPrefixToWicketHandler()
    {
        return super.getRelativePathPrefixToWicketHandler();
    }
}
