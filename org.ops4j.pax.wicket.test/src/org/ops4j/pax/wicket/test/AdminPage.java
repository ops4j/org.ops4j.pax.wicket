package org.ops4j.pax.wicket.test;

import wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import wicket.markup.html.WebPage;

/**
 * A page only accessible by a user in the ADMIN role.
 * 
 * @author Jonathan Locke
 */
@AuthorizeInstantiation("ADMIN")
public class AdminPage extends WebPage
{
}
