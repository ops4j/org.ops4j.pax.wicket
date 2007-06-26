/*
 * $Id: AdminBookmarkablePage.java 459632 2006-03-05 01:20:50Z jcompagner $
 * $Revision: 459632 $ $Date: 2006-03-05 10:20:50 +0900 (Sun, 05 Mar 2006) $
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ops4j.pax.wicket.samples.wicketauth.example;

import wicket.PageParameters;
import wicket.markup.html.WebPage;

/**
 * Bookmarkable page that may only be accessed by users that have role ADMIN.
 * 
 * @author Eelco Hillenius
 */
public class AdminBookmarkablePage
        extends WebPage
{
    private static final long serialVersionUID = 1L;

    /*
	 * See for configuration of this class the {@link RolesApplication#init)
	 * MetaDataRoleAuthorizationStrategy.authorize(AdminBookmarkablePage.class, "ADMIN");
	 */
	
	/**
	 * Construct.
	 */
	public AdminBookmarkablePage()
	{
	    this( null );
	}

    public AdminBookmarkablePage( final PageParameters parameters )
    {
        super( parameters );
    }
}
