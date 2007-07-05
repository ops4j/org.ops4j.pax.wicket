package org.ops4j.pax.wicket.util;

import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.BookmarkableInfo;

import wicket.Page;

public final class DefaultBookmarkableInfo<T extends Page>
        implements BookmarkableInfo<T>
{
    private final String m_path;
    private final Class<T> m_bookmarkablePageClass;

    public DefaultBookmarkableInfo( String path, Class<T> bookmarkablePageClass )
    {
        NullArgumentException.validateNotEmpty( path, "path" );
        NullArgumentException.validateNotNull( bookmarkablePageClass, "bookmarkablePageClass" );

        m_path = path;
        m_bookmarkablePageClass = bookmarkablePageClass;
    }
    public Class<T> getBookmarkablePageClass()
    {
        return m_bookmarkablePageClass;
    }

    public String getPath()
    {
        return m_path;
    }
}
