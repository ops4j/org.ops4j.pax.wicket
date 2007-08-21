package org.ops4j.pax.wicket.internal;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WicketFilter;

public class PaxWicketFilter
        extends WicketFilter
{
    private final IWebApplicationFactory m_appFactory;

    PaxWicketFilter( IWebApplicationFactory appFactory )
    {
        m_appFactory = appFactory;
    }

    @Override
    protected IWebApplicationFactory getApplicationFactory()
    {
        return m_appFactory;
    }

    @Override
    protected String getFilterPath( HttpServletRequest request )
    {
        return super.getFilterPath( request );
    }

    @Override
    public String getRelativePath( HttpServletRequest request )
    {
        return super.getRelativePath( request );
    }
}
