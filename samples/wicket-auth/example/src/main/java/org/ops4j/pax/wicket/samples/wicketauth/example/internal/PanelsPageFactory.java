package org.ops4j.pax.wicket.samples.wicketauth.example.internal;

import org.ops4j.pax.wicket.util.AbstractPageFactory;
import org.osgi.framework.BundleContext;

import org.ops4j.pax.wicket.samples.wicketauth.example.PanelsPage;

import wicket.PageParameters;

public class PanelsPageFactory
        extends AbstractPageFactory<PanelsPage>
{
    public static final String PAGE_NAME = "home";

    public PanelsPageFactory( 
            final BundleContext bundleContext, 
            final String applicationName )
            throws IllegalArgumentException
    {
        super( bundleContext, PAGE_NAME, applicationName, PAGE_NAME );
    }

    public PanelsPage createPage( final PageParameters pageParameters )
    {
        return new PanelsPage( pageParameters );
    }

    public Class<PanelsPage> getPageClass()
    {
        return PanelsPage.class;
    }
}
