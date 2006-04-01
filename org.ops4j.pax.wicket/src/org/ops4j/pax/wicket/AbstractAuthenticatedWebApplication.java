package org.ops4j.pax.wicket;

import org.osgi.framework.BundleContext;

import wicket.authentication.AuthenticatedWebApplication;
import wicket.authentication.AuthenticatedWebSession;
import wicket.markup.html.WebPage;

public abstract class AbstractAuthenticatedWebApplication extends AuthenticatedWebApplication 
{
	private Class< ? extends AuthenticatedWebSession> m_WebSession;
	private Class< ? extends WebPage> m_SignInPage;
	private Class< ? extends WebPage> m_HomePage;
	private BundleContext m_BundleContext;

	public AbstractAuthenticatedWebApplication( BundleContext bundleContext, Class< ? extends WebPage> homePage, Class< ? extends WebPage> signInPage, Class< ? extends AuthenticatedWebSession> webSession )
	{
		m_BundleContext = bundleContext;
		m_HomePage = homePage;
		m_SignInPage = signInPage;
		m_WebSession = webSession;
	}
	
	public void init()
	{
		super.init();
		getApplicationSettings().setClassResolver( new Ops4jWicketClassLoader( m_BundleContext ) );
	}

	
	protected Class< ? extends AuthenticatedWebSession> getWebSessionClass() 
	{
		return m_WebSession;
	}

	protected Class< ? extends WebPage> getSignInPageClass() 
	{
		return m_SignInPage;
	}

	public Class getHomePage() 
	{
		return m_HomePage;
	}

}
