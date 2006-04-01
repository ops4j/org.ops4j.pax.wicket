package org.ops4j.pax.wicket;

import org.osgi.framework.BundleContext;

import wicket.WicketRuntimeException;
import wicket.application.IClassResolver;

public class Ops4jWicketClassLoader implements IClassResolver 
{
	private BundleContext m_BundleContext;
	
	public Ops4jWicketClassLoader( BundleContext bundleContext )
	{
		m_BundleContext = bundleContext;
	}
	
	public Class resolveClass( String classname ) 
	{
		try 
		{
			return m_BundleContext.getBundle().loadClass( classname );
		} catch( ClassNotFoundException e ) 
		{
			throw new WicketRuntimeException( "Unable to load class with name: " + classname );
		}
	}

}
