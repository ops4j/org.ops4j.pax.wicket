/*
 * Copyright 2005 Niclas Hedhman.
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

public class GenericContext
    implements HttpContext
{

    private static final Log LOGGER = LogFactory.getLog( GenericContext.class );

    private String m_rootUrl;
    private MimetypesFileTypeMap m_typeMap;
    private Bundle m_applicationBundle;

    public GenericContext( Bundle applicationBundle, String rootUrl )
    {
        if( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( "GenericContext(" + rootUrl + " )" );
        }
        m_applicationBundle = applicationBundle;
        m_rootUrl = rootUrl;
        m_typeMap = (MimetypesFileTypeMap) MimetypesFileTypeMap.getDefaultFileTypeMap();
        m_typeMap.addMimeTypes( "text/css css" );
    }

    public boolean handleSecurity( HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse )
        throws IOException
    {
        if( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( "handleSecurity()" );
        }
        return true;
    }

    public URL getResource( String resourcename )
    {
        if( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( "getResource( " + resourcename + " )" );
        }
        // The below hack is to compensate for a strange hack in Wicket.AbstractRequestCycleProcessor#resolveExternalResource
        // See comment in that method's source code.
        while( resourcename.startsWith( "//" ) )
        {
            resourcename = resourcename.substring( 1 );
        }
        String resource;
        if( resourcename.startsWith( m_rootUrl ) )
        {
            int prefixLength = m_rootUrl.length();
            resource = resourcename.substring( prefixLength + 1 );
        }
        else
        {
            resource = resourcename;
        }

        URL result = m_applicationBundle.getResource( resource );
        return result;
    }

    public String getMimeType( String resourcename )
    {
        if( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( "getMimeType( " + resourcename + " )" );
        }
        URL resource = getResource( resourcename );
        if( resource == null )
        {
            return null;
        }
        String url = resource.toString();
        if( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( "         URL: " + url );
        }

        String contentType = m_typeMap.getContentType( url );
        if( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( " ContentType: " + contentType );
        }
        return contentType;
    }
}
