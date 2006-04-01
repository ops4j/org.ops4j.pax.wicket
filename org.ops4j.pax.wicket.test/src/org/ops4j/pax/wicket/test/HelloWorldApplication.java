package org.ops4j.pax.wicket.test;

import wicket.protocol.http.WebApplication;

public class HelloWorldApplication extends WebApplication
{
 
    public HelloWorldApplication()
    {
        Activator.debug( "HelloWorldApplication()" );
    }

	public Class getHomePage() 
	{
		return HelloWorld.class;
	}
}