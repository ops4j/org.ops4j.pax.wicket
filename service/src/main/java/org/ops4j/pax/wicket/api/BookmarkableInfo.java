package org.ops4j.pax.wicket.api;

import wicket.Page;

public interface BookmarkableInfo<T extends Page>
{
    String getPath();
    Class<T>getBookmarkablePageClass();
}
