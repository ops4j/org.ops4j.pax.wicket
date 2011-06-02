package org.ops4j.pax.wicket.api;

import org.apache.wicket.extensions.markup.html.tabs.ITab;

public interface TabContentSource<E extends ITab> {

    E createSourceTab();

    E createSourceTab(String title);

}
