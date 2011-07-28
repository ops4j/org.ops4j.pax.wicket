package org.ops4j.pax.wicket.samples.mixed.page.internal;

import org.apache.wicket.Page;
import org.ops4j.pax.wicket.samples.mixed.api.PageProvider;

/**
 * Simple page provider configured via blueprint
 */
public class DefaultPageProvider implements PageProvider {

    private Class<? extends Page> page;

    public Class<? extends Page> getPageClass() {
        return page;
    }

    public void setPage(Class<? extends Page> page) {
        this.page = page;
    }

}
