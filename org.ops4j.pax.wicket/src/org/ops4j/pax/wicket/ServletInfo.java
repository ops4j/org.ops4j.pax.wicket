package org.ops4j.pax.wicket;

import java.util.Dictionary;

import javax.servlet.Servlet;

public class ServletInfo 
{
	private Dictionary m_InitParams;
	private Servlet m_Servlet;
	private String m_Alias;

	public ServletInfo( String alias, Servlet servlet, Dictionary initparams )
	{
		m_Alias = alias;
		m_Servlet = servlet;
		m_InitParams = initparams;
	}

	public String getAlias() 
	{
		return m_Alias;
	}

	public void setAlias(String alias) 
	{
		m_Alias = alias;
	}

	public Dictionary getInitParams() 
	{
		return m_InitParams;
	}

	public void setInitParams(Dictionary initParams) 
	{
		m_InitParams = initParams;
	}

	public Servlet getServlet() 
	{
		return m_Servlet;
	}

	public void setServlet(Servlet servlet) 
	{
		m_Servlet = servlet;
	}
}
