package org.ops4j.pax.wicket.api;

import java.util.List;

import wicket.Page;

public interface PageMounter
{
    <T extends Page>void addMountPoint( String path, Class<T> pageClass );
    List<MountPointInfo<? extends Page>> getMountPoints();
}
