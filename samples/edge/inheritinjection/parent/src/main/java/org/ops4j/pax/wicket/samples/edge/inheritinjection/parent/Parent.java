package org.ops4j.pax.wicket.samples.edge.inheritinjection.parent;

import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.ops4j.pax.wicket.api.PaxWicketBean;

public class Parent extends WebPage {

    @PaxWicketBean(name = "links")
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
