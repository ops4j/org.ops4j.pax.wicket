package org.ops4j.pax.wicket.util;

import java.util.List;
import java.util.Vector;

import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.MountPointInfo;
import org.ops4j.pax.wicket.api.PageMounter;

import org.apache.wicket.Page;
import org.apache.wicket.request.target.coding.BookmarkablePageRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;

public class DefaultPageMounter
        implements PageMounter
{
    private final List<MountPointInfo> m_mountPoints;

    public DefaultPageMounter()
    {
        // Using Vector because it is synchronized
        // But not sure if synchronization is really necessary or not...
        m_mountPoints = new Vector<MountPointInfo>();
    }

    /**
     * A convenience method that uses a default coding strategy.
     * 
     * @param path      the path on which the page is to be mounted
     * @param pageClass the class to mount on this mount point using the
     *                  default strategy
     */
    public <T extends Page> void addMountPoint( String path, Class<T> pageClass )
    {
        addMountPoint( 
                path, 
                new BookmarkablePageRequestTargetUrlCodingStrategy( 
                        path, 
                        pageClass, 
                        null) );
    }

    public void addMountPoint( 
            String path,
            IRequestTargetUrlCodingStrategy codingStrategy )
    {
        MountPointInfo info = new DefaultMountPointInfo( path, codingStrategy );
        m_mountPoints.add( info );
    }

    public List<MountPointInfo> getMountPoints()
    {
        return m_mountPoints;
    }

    private class DefaultMountPointInfo
        implements MountPointInfo
    {
        private final String m_path;
        private final IRequestTargetUrlCodingStrategy m_codingStrategy;

        public DefaultMountPointInfo( 
                String path, 
                IRequestTargetUrlCodingStrategy codingStrategy )
        {
            NullArgumentException.validateNotEmpty( path, "path" );
            NullArgumentException.validateNotNull( codingStrategy, "codingStrategy" );

            m_path = path;
            m_codingStrategy = codingStrategy;
        }

        public String getPath()
        {
            return m_path;
        }

        public IRequestTargetUrlCodingStrategy getCodingStrategy()
        {
            return m_codingStrategy;
        }
    }
}
