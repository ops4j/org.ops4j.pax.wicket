package org.ops4j.pax.wicket.api;

import org.apache.wicket.Application;

public interface ApplicationInitCallback
{
    void execute( Application application );
}
