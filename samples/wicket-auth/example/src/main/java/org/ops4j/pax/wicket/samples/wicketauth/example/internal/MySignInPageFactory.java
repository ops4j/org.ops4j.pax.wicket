package org.ops4j.pax.wicket.samples.wicketauth.example.internal;

import org.ops4j.pax.wicket.util.AbstractPageFactory;
import org.osgi.framework.BundleContext;

import org.ops4j.pax.wicket.samples.wicketauth.example.MySignInPage;

import wicket.PageParameters;

public class MySignInPageFactory
        extends AbstractPageFactory<MySignInPage>
{
    public static final String PAGE_NAME = "home";

    public MySignInPageFactory( 
            final BundleContext bundleContext, 
            final String applicationName )
            throws IllegalArgumentException
    {
        super( bundleContext, PAGE_NAME, applicationName, PAGE_NAME );
    }

    public MySignInPage createPage( final PageParameters pageParameters )
    {
        return new MySignInPage( pageParameters );
    }

    public Class<MySignInPage> getPageClass()
    {
        return MySignInPage.class;
    }
}
