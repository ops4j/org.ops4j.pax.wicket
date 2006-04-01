package org.ops4j.pax.wicket;

import java.net.URL;
import java.util.HashMap;

import javax.servlet.ServletException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
 
public class HttpTracker
    implements ServiceTrackerCustomizer
{
 
    private BundleContext m_BundleContext;
    private HttpService m_Service;
    private HttpContext m_HttpContext;
    private URL m_ContextRoot;
	private ServletInfo[] m_ServletInfo;
//    private Servlet m_Servlet;
//	private String m_Alias;
	private ResourceInfo[] m_ResourceInfo;
	private String m_ContextRootName;
 
    public HttpTracker( BundleContext bundleContext, ServletInfo[] servletInfo, ResourceInfo[] resourceInfo, String contextRootName, URL contextRoot )
    {
        m_BundleContext = bundleContext;
        m_ServletInfo = servletInfo;
        m_ResourceInfo = resourceInfo;
        m_ContextRoot = contextRoot;
        m_ContextRootName = contextRootName;
        if( servletInfo != null )
        {
	        for( int i = 0; i < servletInfo.length; i++ )        	
	        	Activator.debug( "HttpTracker( " + bundleContext + ", " + servletInfo[i].getServlet() + ", " + contextRoot + " )" );
        }
        else
        {
        	Activator.debug( "HttpTracker( " + bundleContext + ", " + contextRoot + " )" );
        }
    }
 
    public Object addingService( ServiceReference serviceReference )
    {
        m_Service = (HttpService) m_BundleContext.getService( serviceReference );
        try
        {
            registerAll();
        }
        catch( NamespaceException e )
        {
            e.printStackTrace();  //TODO: Auto-generated, need attention.
        }
        catch( ServletException e )
        {
            e.printStackTrace();  //TODO: Auto-generated, need attention.
        }
        return m_Service;
    }
 
    public void modifiedService( ServiceReference serviceReference, Object value )
    {
        unregisterAll();
        try
        {
            registerAll();
        }
        catch( NamespaceException e )
        {
            e.printStackTrace();  //TODO: Auto-generated, need attention.
        }
        catch( ServletException e )
        {
            e.printStackTrace();  //TODO: Auto-generated, need attention.
        }
    }
 
    public void removedService( ServiceReference serviceReference, Object value )
    {
        unregisterAll();
    }
 
    private void registerAll()
        throws NamespaceException, ServletException
    {    	
        Resource contextRoot = new Resource( m_ContextRoot, null );
        HashMap resources = new HashMap();
        resources.put( m_ContextRootName, contextRoot );
        m_HttpContext = new GenericContext( resources );
        if( m_ServletInfo != null )
        {
	        for( int i = 0; i < m_ServletInfo.length; i++ )
	        {
	        	ServletInfo si = m_ServletInfo[i];
	        	m_Service.registerServlet( si.getAlias(), si.getServlet(), si.getInitParams(), m_HttpContext );
	        }
        }
        if( m_ResourceInfo != null )
        {
	        for( int i = 0; i < m_ResourceInfo.length; i++ ) 
	        {
	        	ResourceInfo ri = m_ResourceInfo[i];
		        m_Service.registerResources( ri.getAlias(), ri.getName(), m_HttpContext );
			}
        }
    }
 
    private void unregisterAll()
    {
        if( m_ServletInfo != null )
        {
	        for( int i = 0; i < m_ServletInfo.length; i++ )
	        {
	        	ServletInfo si = m_ServletInfo[i];
	            m_Service.unregister( si.getAlias() );
	        }
        }
        if( m_ResourceInfo != null )
        {
	        for( int i = 0; i < m_ResourceInfo.length; i++ ) 
	        {
	        	ResourceInfo ri = m_ResourceInfo[i];
	            m_Service.unregister( ri.getAlias() );
			}
        }
    	
    }
}