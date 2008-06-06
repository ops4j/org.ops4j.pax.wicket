package org.ops4j.pax.wicket.util;

import java.util.List;
import java.util.Vector;
import org.apache.wicket.Page;
import org.apache.wicket.request.target.coding.BookmarkablePageRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import org.ops4j.pax.wicket.api.MountPointInfo;
import org.ops4j.pax.wicket.api.PageMounter;

public class DefaultPageMounter
    implements PageMounter
{

    private final List<MountPointInfo> mountPoints;

    public DefaultPageMounter()
    {
        // Using Vector because it is synchronized
        // But not sure if synchronization is really necessary or not...
        mountPoints = new Vector<MountPointInfo>();
    }

    /**
     * A convenience method that uses a default coding strategy.
     *
     * @param aPath      the path on which the page is to be mounted
     * @param aPageClass the class to mount on this mount point using the
     *                   default strategy
     */
    public void addMountPoint( String aPath, Class<? extends Page> aPageClass )
    {
        addMountPoint( aPath, new BookmarkablePageRequestTargetUrlCodingStrategy( aPath, aPageClass, null ) );
    }

    public void addMountPoint( String path, IRequestTargetUrlCodingStrategy codingStrategy )
    {
        MountPointInfo info = new DefaultMountPointInfo( path, codingStrategy );
        mountPoints.add( info );
    }

    public final List<MountPointInfo> getMountPoints()
    {
        return mountPoints;
    }

    private static class DefaultMountPointInfo
        implements MountPointInfo
    {

        private final String path;
        private final IRequestTargetUrlCodingStrategy codingStrategy;

        private DefaultMountPointInfo( String aPath, IRequestTargetUrlCodingStrategy aCodingStrategy )
        {
            validateNotEmpty( aPath, "aPath" );
            validateNotNull( aCodingStrategy, "aCodingStrategy" );

            path = aPath;
            codingStrategy = aCodingStrategy;
        }

        public final String getPath()
        {
            return path;
        }

        public final IRequestTargetUrlCodingStrategy getCodingStrategy()
        {
            return codingStrategy;
        }
    }
}
