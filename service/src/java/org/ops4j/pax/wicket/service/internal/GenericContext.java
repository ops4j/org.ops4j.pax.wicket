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

import java.io.IOException;
import java.net.URL;
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.ops4j.pax.servicemanager.ServiceManager;

public class GenericContext
    implements HttpContext
{
    private BundleContext m_BundleContext;
    private String m_RootUrl;
    private FileTypeMap m_TypeMap;
    private ServiceManager m_serviceManager;

    public GenericContext( BundleContext bundleContext, String rootUrl, ServiceManager serviceManager )
    {
        Log logger = LogFactory.getLog( GenericContext.class );
        m_serviceManager = serviceManager;
        logger.debug( "GenericContext(" + rootUrl + " )" );
        m_BundleContext = bundleContext;
        m_RootUrl = rootUrl;
        m_TypeMap = MimetypesFileTypeMap.getDefaultFileTypeMap();
    }

    public boolean handleSecurity( HttpServletRequest httpServletRequest,
                                   HttpServletResponse httpServletResponse )
        throws IOException
    {
        Log logger = LogFactory.getLog( GenericContext.class );
        logger.debug( "handleSecurity()" );
        return true;
    }

    public URL getResource( String resourcename )
    {
        Log logger = LogFactory.getLog( GenericContext.class );
        logger.debug( "getResource( " + resourcename + " )" );
        int prefixLength = m_RootUrl.length();
        String resource = resourcename.substring( prefixLength + 1 );
        return m_BundleContext.getBundle().getResource( resource );
    }

    public String getMimeType( String resourcename )
    {
        Log logger = LogFactory.getLog( GenericContext.class );
        logger.debug( "getMimeType( " + resourcename + " )" );
        URL resource = getResource( resourcename );
        if( resource == null )
        {
            return null;
        }
        String url = resource.toString();
        logger.debug( "         URL: " + url );
        String contentType = m_TypeMap.getContentType( url );
        logger.debug( " ContentType: " + contentType );
        return contentType;
    }
}
