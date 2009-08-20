package org.ops4j.pax.wicket.internal;

import org.ops4j.pax.wicket.api.SessionDestroyedListener;

public interface SessionDestroyedHander
{
    String getApplicationName();
    void sessionDestroyed( String sessionId );
    void addListener( SessionDestroyedListener listener );
    void removeListener( SessionDestroyedListener listener );
}
