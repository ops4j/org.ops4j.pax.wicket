package org.ops4j.pax.wicket.api;

import wicket.Page;

public interface MountPointInfo<T extends Page>
{
    String getPath();
    Class<T> getPageClass();
}
