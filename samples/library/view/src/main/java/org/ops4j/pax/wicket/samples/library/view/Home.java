/*
 * $Id: Home.java 2567 2005-08-21 15:11:33Z joco01 $ $Revision: 2567 $
 * $Date: 2005-08-21 23:11:33 +0800 (Sun, 21 Aug 2005) $
 *
 * ==================================================================== Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ops4j.pax.wicket.samples.library.view;

import org.ops4j.pax.wicket.samples.library.model.BookImpl;
import org.ops4j.pax.wicket.samples.library.app.LibraryHomePage;
import org.ops4j.pax.wicket.samples.library.view.internal.EditBook;
import org.ops4j.pax.wicket.samples.library.view.internal.BookDetails;
import wicket.PageParameters;
import wicket.markup.html.basic.Label;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.PageableListView;
import wicket.markup.html.navigation.paging.PagingNavigator;
import wicket.model.Model;
import wicket.model.PropertyModel;

/**
 * Page that displays a list of books and lets the user re-order them.
 *
 * @author Jonathan Locke
 */
public final class Home extends LibraryHomePage
{
    /**
     * Constructor
     *
     * @param parameters
     */
    public Home( PageParameters parameters )
    {
        super( parameters );
        // Add table of books
        final PageableListView listView;
        add(listView = new PageableListView("books", new PropertyModel(this, "books"), 4)
        {
            public void populateItem(final ListItem listItem)
            {
                final BookImpl book = (BookImpl)listItem.getModelObject();
                listItem.add( BookDetails.link("details", book, getLocalizer().getString(
                        "noBookTitle", this)));
                listItem.add(new Label("author", new Model(book)));
                listItem.add(moveUpLink("moveUp", listItem));
                listItem.add(moveDownLink("moveDown", listItem));
                listItem.add(removeLink("remove", listItem));
                listItem.add( EditBook.link("edit", book.getId()));
            }
        });
        add(new PagingNavigator("navigator", listView));
    }
}
