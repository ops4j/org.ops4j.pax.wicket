package org.ops4j.pax.wicket;

import org.osgi.framework.BundleContext;

import wicket.authentication.AuthenticatedWebApplication;
import wicket.protocol.http.IWebApplicationFactory;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WicketServlet;

public class WicketApplicationFactory implements IWebApplicationFactory 
{
	private WebApplication m_WebApplication;
	
	public WicketApplicationFactory( WebApplication webApplication )
	{
		m_WebApplication = webApplication;
	}
	
	public WicketApplicationFactory( BundleContext bundleContext, Class homePageClass, Class signInPageClass, Class webSessionClass )
	{
		m_WebApplication = createApplication( bundleContext, homePageClass, signInPageClass, webSessionClass );
	}

	public WebApplication createApplication( WicketServlet servlet ) 
	{
		return m_WebApplication;
	}
	
	private WebApplication createApplication( final BundleContext bundleContext, final Class homePageClass, final Class signInPageClass, final Class webSessionClass )
	{
		if( signInPageClass != null && webSessionClass != null )
		{
			return new AuthenticatedWebApplication()
			{
				public void init()
				{
					super.init();
					getApplicationSettings().setClassResolver( new Ops4jWicketClassLoader( bundleContext ) );
				}

				public Class getHomePage() 
				{
					return homePageClass;
				}

				protected Class getWebSessionClass() 
				{
					return webSessionClass;
				}

				protected Class getSignInPageClass() 
				{
					return signInPageClass;
				}
			};
		}
		else
		{
			return new WebApplication()
			{
				public void init()
				{
					super.init();
					getApplicationSettings().setClassResolver( new Ops4jWicketClassLoader( bundleContext ) );
				}

				public Class getHomePage() 
				{
					return homePageClass;
				}
			};
		}
	}

}
