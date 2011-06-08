/**
 * Copyright OPS4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: Index.java 459082 2006-02-08 21:11:29Z ehillenius $ $Revision: 459082 $
 * $Date: 2006-02-09 06:11:29 +0900 (Thu, 09 Feb 2006) $
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

import wicket.Session;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.BookmarkablePageLink;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.Model;
import wicket.model.PropertyModel;

/**
 * Home page for the roles example.
 * 
 * @author Eelco Hillenius
 */
public class Example extends WebPage
{
    private User m_user;

    /**
	 * Construct.
	 */
	public Example()
	{
		add(new Label("currentUser", new PropertyModel(this, "user")));
		add(new ListView("users", RolesApplication.USERS)
		{
			@Override
			protected void populateItem(ListItem item)
			{
				final User user = (User)item.getModelObject();
				item.add(new Link("selectUserLink")
				{
					@Override
					public void onClick()
					{
						setUser(user);
					}
				}.add(new Label("userId", new Model(user))));
			}
		});

		// pages that are proteced using wicket meta data
		add(new BookmarkablePageLink("adminBookmarkableLink", AdminBookmarkablePage.class));
		add(new Link("adminInternalLink")
		{
			@Override
			public void onClick()
			{
				setResponsePage(new AdminInternalPage("foo"));
			}
		});
		add(new BookmarkablePageLink("panelsPageLink", PanelsPage.class));

		// pages that are protected using annotations
		add(new BookmarkablePageLink("adminAnnotBookmarkableLink", AdminAnnotationsBookmarkablePage.class));
		add(new Link("adminAnnotInternalLink")
		{
			@Override
			public void onClick()
			{
				setResponsePage(new AdminAnnotationsInternalPage("bar"));
			}
		});
		add(new BookmarkablePageLink("panelsAnnotPageLink", AnnotationsPanelsPage.class));
	}

	public User getUser()
	{
	    return m_user;
	}

	public void setUser( final User user )
	{
	    m_user = user;
	}
}
