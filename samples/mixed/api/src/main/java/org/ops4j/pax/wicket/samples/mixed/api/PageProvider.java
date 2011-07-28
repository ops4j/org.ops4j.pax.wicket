package org.ops4j.pax.wicket.samples.mixed.api;

import org.apache.wicket.Page;

public interface PageProvider {

    Class<? extends Page> getPageClass();

}
