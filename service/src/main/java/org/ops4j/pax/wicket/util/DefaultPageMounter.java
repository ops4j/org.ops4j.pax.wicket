package org.ops4j.pax.wicket.util;

import java.util.List;
import java.util.Vector;

import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.MountPointInfo;
import org.ops4j.pax.wicket.api.PageMounter;

import wicket.Page;

public class DefaultPageMounter
        implements PageMounter
{
    private final List<MountPointInfo<? extends Page>> m_mountPoints;

    public DefaultPageMounter()
    {
        // Using Vector because it is synchronized
        // But not sure if synchronization is really necessary or not...
        m_mountPoints = new Vector<MountPointInfo<? extends Page>>();
    }

    public <T extends Page> void addMountPoint( String path, Class<T> pageClass )
    {
        MountPointInfo<T> info = new DefaultMountPointInfo<T>( path, pageClass );
        m_mountPoints.add( info );
    }

    public List<MountPointInfo<? extends Page>> getMountPoints()
    {
        return m_mountPoints;
    }


    private class DefaultMountPointInfo<T extends Page>
        implements MountPointInfo<T>
    {
        private final String m_path;
        private final Class<T> m_pageClass;

        public DefaultMountPointInfo( String path, Class<T> pageClass )
        {
            NullArgumentException.validateNotEmpty( path, "path" );
            NullArgumentException.validateNotNull( pageClass, "pageClass" );

            m_path = path;
            m_pageClass = pageClass;
        }

        public String getPath()
        {
            return m_path;
        }

        public Class<T> getPageClass()
        {
            return m_pageClass;
        }
    }
}
