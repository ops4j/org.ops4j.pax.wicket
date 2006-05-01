package org.ops4j.pax.wicket;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator 
{
    public static BundleContext BUNDLE_CONTEXT;

	public void start( BundleContext bundleContext )
        throws Exception
    {
    	BUNDLE_CONTEXT = bundleContext;
    }
 
    public void stop( BundleContext bundleContext )
        throws Exception
    {
    }
 
    public static void debug( String message )
    {
    }

}
