package org.ops4j.pax.wicket.api;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;

public interface PageMounter
{
    <T extends Page> void addMountPoint( String path, Class<T> pageClass );
    <T extends Page>void addMountPoint( 
            String path, 
            IRequestTargetUrlCodingStrategy codingStrategy );
    List<MountPointInfo> getMountPoints();
}
