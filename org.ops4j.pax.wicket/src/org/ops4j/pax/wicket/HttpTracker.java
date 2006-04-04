package org.ops4j.pax.wicket;

import javax.servlet.ServletException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
 
public class HttpTracker
    implements ServiceTrackerCustomizer
{
 
    private BundleContext m_BundleContext;
    private HttpService m_Service;
//    private URL m_ContextRoot;
	private ServletInfo[] m_ServletInfo;
//    private Servlet m_Servlet;
//	private String m_Alias;
	private ResourceInfo[] m_ResourceInfo;
 
    public HttpTracker( BundleContext bundleContext, ServletInfo[] servletInfo, ResourceInfo[] resourceInfo )
    {
        m_BundleContext = bundleContext;
        m_ServletInfo = servletInfo;
        m_ResourceInfo = resourceInfo;
        if( servletInfo != null )
        {
	        for( int i = 0; i < servletInfo.length; i++ )        	
	        	Activator.debug( "HttpTracker( " + bundleContext + ", " + servletInfo[i].getServlet() + " )" );
        }
        else
        {
        	Activator.debug( "HttpTracker( " + bundleContext + " )" );
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
//        m_HttpContext = m_Service.createDefaultHttpContext();
        if( m_ServletInfo != null )
        {
	        for( int i = 0; i < m_ServletInfo.length; i++ )
	        {
	        	ServletInfo si = m_ServletInfo[i];
	        	m_Service.registerServlet( si.getAlias(), si.getServlet(), si.getInitParams(), null );
	        }
        }
        if( m_ResourceInfo != null )
        {
	        for( int i = 0; i < m_ResourceInfo.length; i++ ) 
	        {
	        	ResourceInfo ri = m_ResourceInfo[i];
		        m_Service.registerResources( ri.getAlias(), ri.getName(), null );
			}
        }
        
        // Register resources from context root
//        try
//        {
//            URLConnection connection = m_ContextRoot.openConnection();
//            String contentType = connection.getContentType();
//            if( "text/plain".equals( contentType ) )
//            {
//                File dir = new File( m_ContextRoot.getPath() );
//                if( dir.isDirectory() )
//                {
//                    File[] files = dir.listFiles();
//                    for( int i = 0; i < files.length; i++ )
//                    {
//                        File file = files[i];
//                        String fileName = "/" + file.getName();
//                        m_Service.registerResources( fileName, fileName, null );
//                    }
//                }
//            }
//        }
//        catch ( IOException e )
//        {
//            e.printStackTrace();
//        }
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