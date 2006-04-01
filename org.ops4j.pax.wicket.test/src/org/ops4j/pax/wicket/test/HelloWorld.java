package org.ops4j.pax.wicket.test;

import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
 
public class HelloWorld extends WebPage
{
    public HelloWorld()
    {
        Activator.debug( "HelloWorld()" );
        //add( new Label("message", "Hello World! Wonderful day!") );
    }
}