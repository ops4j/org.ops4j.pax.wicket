package org.ops4j.pax.wicket.samples.wicketauth.example.internal;

import org.ops4j.pax.wicket.util.AbstractPageFactory;
import org.osgi.framework.BundleContext;

import org.ops4j.pax.wicket.samples.wicketauth.example.AdminAnnotationsBookmarkablePage;

import wicket.PageParameters;

public class AdminAnnotationsBookmarkablePageFactory
        extends AbstractPageFactory<AdminAnnotationsBookmarkablePage>
{
    public static final String PAGE_NAME = "home";

    public AdminAnnotationsBookmarkablePageFactory( 
            final BundleContext bundleContext, 
            final String applicationName )
            throws IllegalArgumentException
    {
        super( bundleContext, PAGE_NAME, applicationName, PAGE_NAME );
    }

    public AdminAnnotationsBookmarkablePage createPage( final PageParameters pageParameters )
    {
        return new AdminAnnotationsBookmarkablePage( pageParameters );
    }

    public Class<AdminAnnotationsBookmarkablePage> getPageClass()
    {
        return AdminAnnotationsBookmarkablePage.class;
    }
}
