/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2006 Edward F. Yakop
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.samples.departmentstore.view.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.ops4j.pax.wicket.api.ContentAggregator;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.api.PageFactory;
import org.ops4j.pax.wicket.api.TabContentSource;
import org.ops4j.pax.wicket.util.proxy.PaxWicketBean;

@AuthorizeInstantiation("user")
public class OverviewPage extends WebPage {

    @PaxWicketBean
    private ContentAggregator container;
    @PaxWicketBean
    private StoreDescription storeDescription;
    @PaxWicketBean(name = "about")
    private List<PageFactory<Page>> aboutPageClass;

    public OverviewPage() {
        setupOverviewPage(container, storeDescription, aboutPageClass);
    }

    public OverviewPage(ContentAggregator container, StoreDescription storeDescription,
            List<PageFactory<Page>> aboutPageClass) {
        setupOverviewPage(container, storeDescription, aboutPageClass);
    }

    private void setupOverviewPage(ContentAggregator container, StoreDescription storeDescription,
            List<PageFactory<Page>> aboutPages) {
        Label label = new Label("storeName", storeDescription.getStoreName());
        add(label);
        createAboutPage(aboutPages);
        Locale locale = getLocale();
        List<ContentSource> contents = container.getContentByGroupId("floor");
        int numberOfContents = contents.size();
        List<ITab> tabs = new ArrayList<ITab>(numberOfContents);
        for (ContentSource content : contents) {
            if (content instanceof TabContentSource) {
                TabContentSource<AbstractTab> otc = (TabContentSource<AbstractTab>) content;
                AbstractTab tab = otc.createSourceTab();
                tabs.add(tab);
            }
        }
        if (tabs.isEmpty()) {
            Label niceMsg = new Label("floors", "No Floors installed yet.");
            add(niceMsg);
        } else {
            AjaxTabbedPanel tabbedPanel = new AjaxTabbedPanel("floors", tabs);
            add(tabbedPanel);
        }
    }

    private void createAboutPage(List<PageFactory<Page>> aboutPages) {
        Component link;
        if (aboutPages.size() == 0) {
            link = new Label("aboutlink", "");
        } else {
            Class<? extends Page> aboutPage = aboutPages.get(0).getPageClass();
            link = new BookmarkablePageLink("aboutlink", aboutPage);
        }
        add(link);
    }
}
