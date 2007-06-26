package org.ops4j.pax.wicket.samples.wicketauth.example.internal;

import org.ops4j.pax.wicket.util.AbstractPageFactory;
import org.osgi.framework.BundleContext;

import org.ops4j.pax.wicket.samples.wicketauth.example.AnnotationsPanelsPage;

import wicket.PageParameters;

public class AnnotationsPanelsPageFactory
        extends AbstractPageFactory<AnnotationsPanelsPage>
{
    public static final String PAGE_NAME = "home";

    public AnnotationsPanelsPageFactory( 
            final BundleContext bundleContext, 
            final String applicationName )
            throws IllegalArgumentException
    {
        super( bundleContext, PAGE_NAME, applicationName, PAGE_NAME );
    }

    public AnnotationsPanelsPage createPage( final PageParameters pageParameters )
    {
        return new AnnotationsPanelsPage( pageParameters );
    }

    public Class<AnnotationsPanelsPage> getPageClass()
    {
        return AnnotationsPanelsPage.class;
    }
}
