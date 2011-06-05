package org.ops4j.pax.wicket.util;

import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.request.target.coding.BookmarkablePageRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.ops4j.pax.wicket.api.MountPointInfo;
import org.ops4j.pax.wicket.api.PageMounter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPageMounter implements PageMounter {

    private final List<MountPointInfo> mountPoints;

    public DefaultPageMounter() {
        // Using Vector because it is synchronized
        // But not sure if synchronization is really necessary or not...
        mountPoints = new Vector<MountPointInfo>();
    }

    /**
     * A convenience method that uses a default coding strategy.
     * 
     * @param path the path on which the page is to be mounted
     * @param pageClass the class to mount on this mount point using the default strategy
     */
    public void addMountPoint(String path, Class<? extends Page> pageClass) {
        addMountPoint(path, new BookmarkablePageRequestTargetUrlCodingStrategy(path, pageClass, null));
    }

    public void addMountPoint(String path, IRequestTargetUrlCodingStrategy codingStrategy) {
        MountPointInfo info = new DefaultMountPointInfo(path, codingStrategy);
        mountPoints.add(info);
    }

    public final List<MountPointInfo> getMountPoints() {
        return Collections.unmodifiableList(mountPoints);
    }

    private static class DefaultMountPointInfo implements MountPointInfo {
        private final String path;
        private final IRequestTargetUrlCodingStrategy codingStrategy;

        private DefaultMountPointInfo(String path, IRequestTargetUrlCodingStrategy codingStrategy) {
            validateNotEmpty(path, "path");
            validateNotNull(codingStrategy, "codingStrategy");
            this.path = path;
            this.codingStrategy = codingStrategy;
        }

        public final String getPath() {
            return path;
        }

        public final IRequestTargetUrlCodingStrategy getCodingStrategy() {
            return codingStrategy;
        }
    }

}
