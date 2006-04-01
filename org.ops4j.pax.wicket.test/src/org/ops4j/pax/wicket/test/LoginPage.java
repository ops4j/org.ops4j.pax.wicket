package org.ops4j.pax.wicket.test;

import wicket.authentication.pages.SignInPage;
 
public class LoginPage extends SignInPage
{
    public LoginPage()
    {
        System.out.println( "Login Page" );
//        add( new Label("message", "Hello World! Wonderful day!") );
    }
}