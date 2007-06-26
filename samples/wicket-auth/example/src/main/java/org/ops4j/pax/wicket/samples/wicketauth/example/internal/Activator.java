package org.ops4j.pax.wicket.samples.wicketauth.example.internal;

import org.ops4j.pax.wicket.api.PaxWicketApplicationFactory;
import org.ops4j.pax.wicket.api.PaxWicketAuthenticator;
import org.ops4j.pax.wicket.samples.wicketauth.example.Example;
import org.ops4j.pax.wicket.samples.wicketauth.example.MySignInPage;
import org.ops4j.pax.wicket.util.UserAdminAuthenticator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator
    implements BundleActivator
{
    private MySignInPageFactory m_msipFactory;
    private AdminBookmarkablePageFactory m_abpFactory;
    private AdminAnnotationsBookmarkablePageFactory m_aabpFactory;
    private AnnotationsPanelsPageFactory m_aPanelsPageFactory;
    private PanelsPageFactory m_panelsPageFactory;
    private PaxWicketApplicationFactory m_applicationFactory;
    private ServiceRegistration m_serviceRegistration;
    private ServiceRegistration m_pageRegistration;

    public void start( final BundleContext context )
            throws Exception
    {
        final String mountPoint = "wicketauth";
        final String applicationName = "example";

        m_msipFactory = 
            new MySignInPageFactory( context, applicationName );
        m_msipFactory.register();

        m_abpFactory = 
            new AdminBookmarkablePageFactory( context, applicationName );
        m_abpFactory.register();

        m_aabpFactory = 
            new AdminAnnotationsBookmarkablePageFactory( context, applicationName );
        m_aabpFactory.register();

        m_panelsPageFactory = 
            new PanelsPageFactory( context, applicationName );
        m_panelsPageFactory.register();

        m_aPanelsPageFactory = 
            new AnnotationsPanelsPageFactory( context, applicationName );
        m_aPanelsPageFactory.register();

        m_applicationFactory = 
            new PaxWicketApplicationFactory( context, Example.class, mountPoint, applicationName );
        final PaxWicketAuthenticator auth = new UserAdminAuthenticator( context, applicationName );
        m_applicationFactory.setAuthenticator( auth, MySignInPage.class );
        m_applicationFactory.setDeploymentMode( false );
        m_serviceRegistration = m_applicationFactory.register();
    }

    public void stop( final BundleContext context )
            throws Exception
    {
        if( null != m_msipFactory )
        {
            m_msipFactory.dispose();
            m_msipFactory = null;
        }

        if( null != m_abpFactory )
        {
            m_abpFactory.dispose();
            m_abpFactory = null;
        }

        if( null != m_aabpFactory )
        {
            m_aabpFactory.dispose();
            m_aabpFactory = null;
        }

        if( null != m_panelsPageFactory )
        {
            m_panelsPageFactory.dispose();
            m_panelsPageFactory = null;
        }

        if( null != m_aPanelsPageFactory )
        {
            m_aPanelsPageFactory.dispose();
            m_aPanelsPageFactory = null;
        }

        if( null != m_pageRegistration )
        {
            m_pageRegistration.unregister();
            m_pageRegistration = null;
        }

        if( null != m_serviceRegistration )
        {
            m_serviceRegistration.unregister();
            m_serviceRegistration = null;
        }

        if( null != m_applicationFactory )
        {
            m_applicationFactory.dispose();
            m_applicationFactory = null;
        }
    }
}
