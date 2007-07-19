package org.ops4j.pax.wicket.api;

import wicket.Application;

public interface ApplicationInitCallback
{
    void execute( Application application );
}
