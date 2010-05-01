package org.ops4j.pax.wicket.api;

import org.apache.wicket.Application;
import org.apache.wicket.session.ISessionStore;

public interface SessionStoreFactory
{
    ISessionStore newSessionStore( Application application );
}
