/*
 * Copyright 2007 David Leangen
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.util;

import java.util.List;
import java.util.Vector;

import org.apache.wicket.Page;
import org.apache.wicket.request.target.coding.BookmarkablePageRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.MountPointInfo;
import org.ops4j.pax.wicket.api.PageMounter;

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
