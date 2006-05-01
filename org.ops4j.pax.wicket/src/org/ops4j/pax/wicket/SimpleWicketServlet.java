package org.ops4j.pax.wicket;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;

import wicket.protocol.http.IWebApplicationFactory;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WicketServlet;

public class SimpleWicketServlet extends WicketServlet 
{
	private WicketApplicationFactory m_WebApplicationFactory;
	
	//needed for Eclipse extension point registrations
	public SimpleWicketServlet()
	{
		this( Activator.BUNDLE_CONTEXT, null, null, null );
	}
	
	public SimpleWicketServlet( WebApplication webApplication )
	{
		m_WebApplicationFactory = new WicketApplicationFactory( webApplication );
	}

	public SimpleWicketServlet( BundleContext bundleContext, Class homePageClass, Class signInPageClass, Class webSessionClass )
	{
		m_WebApplicationFactory = new WicketApplicationFactory( bundleContext, homePageClass, signInPageClass, webSessionClass );
	}
	
	public void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException 
	{
		Activator.debug("SimpleWicketServlet.service( " + req + ", " + resp
				+ " )");
		super.service(req, resp);
	}

	protected IWebApplicationFactory getApplicationFactory() 
	{
		return m_WebApplicationFactory;
	}
}