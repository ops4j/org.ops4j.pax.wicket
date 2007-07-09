package org.ops4j.pax.wicket.api;

import wicket.request.target.coding.IRequestTargetUrlCodingStrategy;

public interface MountPointInfo
{
    String getPath();
    public IRequestTargetUrlCodingStrategy getCodingStrategy();
}
