package org.ops4j.pax.wicket.api;

import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;

public interface MountPointInfo
{
    String getPath();
    public IRequestTargetUrlCodingStrategy getCodingStrategy();
}
