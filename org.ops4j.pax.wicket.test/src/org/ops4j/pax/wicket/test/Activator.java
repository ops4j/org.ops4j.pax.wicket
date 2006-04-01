package org.ops4j.pax.wicket.test;

import java.io.File;
import java.net.URL;

import javax.servlet.http.HttpServlet;

import org.ops4j.pax.wicket.HttpTracker;
import org.ops4j.pax.wicket.ResourceInfo;
import org.ops4j.pax.wicket.ServletInfo;
import org.ops4j.pax.wicket.SimpleWicketServlet;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator 
{

    private ServiceTracker m_HttpTracker;
 
    public void start( BundleContext bundleContext )
        throws Exception
    {
        debug( "Initializing the servlet." );
        HttpServlet servlet = new SimpleWicketServlet( bundleContext, HomePage.class, LoginPage.class, TestWebSession.class );
        String userDir = System.getProperty( "user.dir" );
        File contextDir = new File( userDir, "webapps/hello" );
        contextDir.mkdirs();
        URL contextRoot = contextDir.toURL();
        ServletInfo[] servletInfos = new ServletInfo[] { new ServletInfo( "/helloapp", servlet, null ) };
        ResourceInfo[] resourceInfos = new ResourceInfo[] { new ResourceInfo( "/hello", "/hello" ) };
        HttpTracker tracker = new HttpTracker( bundleContext, servletInfos, resourceInfos, "/hello", contextRoot );
 
        m_HttpTracker = new ServiceTracker( bundleContext, HttpService.class.getName(), tracker );
        m_HttpTracker.open();
    }
 
    public void stop( BundleContext bundleContext )
        throws Exception
    {
    	m_HttpTracker.close();
    }
 
    public static void debug( String message )
    {
        System.out.println( message );
    }

}
