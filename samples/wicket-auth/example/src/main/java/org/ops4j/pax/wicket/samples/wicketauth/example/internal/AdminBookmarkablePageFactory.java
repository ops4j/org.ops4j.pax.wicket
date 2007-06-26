package org.ops4j.pax.wicket.samples.wicketauth.example.internal;

import org.ops4j.pax.wicket.util.AbstractPageFactory;
import org.osgi.framework.BundleContext;

import org.ops4j.pax.wicket.samples.wicketauth.example.AdminBookmarkablePage;

import wicket.PageParameters;

public class AdminBookmarkablePageFactory
        extends AbstractPageFactory<AdminBookmarkablePage>
{
    public static final String PAGE_NAME = "home";

    public AdminBookmarkablePageFactory( 
            final BundleContext bundleContext, 
            final String applicationName )
            throws IllegalArgumentException
    {
        super( bundleContext, PAGE_NAME, applicationName, PAGE_NAME );
    }

    public AdminBookmarkablePage createPage( final PageParameters pageParameters )
    {
        return new AdminBookmarkablePage( pageParameters );
    }

    public Class<AdminBookmarkablePage> getPageClass()
    {
        return AdminBookmarkablePage.class;
    }
}
