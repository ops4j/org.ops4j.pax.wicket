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
package org.ops4j.pax.wicket.samples.mixed.main.internal;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.ops4j.pax.wicket.samples.mixed.api.ComponentProvider;
import org.ops4j.pax.wicket.samples.mixed.api.PageProvider;

/**
 * Very simple page providing entry points into various other examples.
 */
public class Homepage extends WebPage {

    private static final long       serialVersionUID = 1L;

    @Named("componentProvider")
    @Inject
    private List<ComponentProvider> componentProvider;
    @Named("pageProvider")
    @Inject
    private List<PageProvider>      pageProvider;

    public Homepage() {
        add(new Label("oneComponent", "Welcome to the mixed component and technology example. Enjoy the full power of pax wicket!."));

        ListView<PageProvider> links = new ListView<PageProvider>("links", pageProvider) {
            @Override
            protected void populateItem(ListItem<PageProvider> item) {
                item.add(new BookmarkablePageLink("link", item.getModelObject().getPageClass()));
            }
        };
        add(links);

        ListView<ComponentProvider> components = new ListView<ComponentProvider>("components", componentProvider) {
            @Override
            protected void populateItem(ListItem<ComponentProvider> item) {
                item.add(item.getModelObject().getComponent("component"));
            }
        };
        add(components);
    }
}
