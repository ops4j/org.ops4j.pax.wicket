package org.ops4j.pax.wicket.api;

import java.util.List;
import org.apache.wicket.Page;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;

public interface PageMounter
{

    void addMountPoint( String path, Class<? extends Page> pageClass );

    void addMountPoint( String path, IRequestTargetUrlCodingStrategy codingStrategy );

    List<MountPointInfo> getMountPoints();
}
