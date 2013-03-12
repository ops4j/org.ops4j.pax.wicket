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
package org.ops4j.pax.wicket.samples.edge.inheritinjection.parent;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

public class Parent extends WebPage {

    @Named("links")
    @Inject
    private List<LinkProvider> links;

    public Parent() {
        add(new BookmarkablePageLink("homeLink", Parent.class));
        add(new ListView<LinkProvider>("links", links) {
            @Override
            protected void populateItem(ListItem<LinkProvider> item) {
                item.add(new BookmarkablePageLink("link", item.getModelObject().getLinkClass()));
            }
        });
    }

}
