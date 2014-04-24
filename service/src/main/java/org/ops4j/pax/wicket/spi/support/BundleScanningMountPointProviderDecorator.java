/**
 * Copyright OPS4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.spi.support;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.wicket.Page;
import org.ops4j.pax.wicket.api.PaxWicketMountPoint;
import org.ops4j.pax.wicket.api.support.DefaultPageMounter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleScanningMountPointProviderDecorator implements InjectionAwareDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleScanningMountPointProviderDecorator.class);

    private BundleContext bundleContext;
    private String applicationName;
    private final List<DefaultPageMounter> mountPointRegistrations = new ArrayList<DefaultPageMounter>();

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void start() throws Exception {
        Bundle bundleToScan = bundleContext.getBundle();
        Enumeration<?> findEntries = bundleToScan.findEntries("", "*.class", true);
        if (findEntries == null) {
            LOGGER.error(new StringBuilder()
                .append("We've found an error which you should really give a shot but which does not ")
                .append("interrupt your runtime. Nevertheless we assume that this one is definitely an ")
                .append("error so give it a shot! OK, the problem is that you entered the bundle with the ")
                .append("symbolic name {} the blueprint/spring entry to scan for automount annotations. ")
                .append("Nevertheless this bundle you would like to have scanned has NO classes! Either ")
                .append("the anotation is wrong or you messed up something during the build of your bundle!")
                .toString(), bundleToScan.getSymbolicName());
            return;
        }
        while (findEntries.hasMoreElements()) {
            String className = calculateClassName((URL) findEntries.nextElement());
            Class<?> candidateClass = bundleToScan.loadClass(className);
            if (!Page.class.isAssignableFrom(candidateClass)) {
                continue;
            }
            @SuppressWarnings("unchecked")
            Class<? extends Page> pageClass = (Class<? extends Page>) candidateClass;
            PaxWicketMountPoint mountPoint = pageClass.getAnnotation(PaxWicketMountPoint.class);
            if (mountPoint != null) {
                DefaultPageMounter mountPointRegistration = new DefaultPageMounter(applicationName, bundleContext);
                mountPointRegistration.addMountPoint(mountPoint.mountPoint(), pageClass);
                mountPointRegistration.register();
                mountPointRegistrations.add(mountPointRegistration);
            }
        }
    }

    private String calculateClassName(URL url) throws URISyntaxException {
        String className = url.getProtocol().equals("jar") ? url.toURI().getSchemeSpecificPart() : url.toURI().getPath();
        URI baseUrl = new URI(bundleContext.getBundle().getLocation());
        String pattern = baseUrl.getSchemeSpecificPart();
        if (className.startsWith(pattern)) {
            className = className.replaceFirst(pattern, "");
        } else {
            className = className.replaceFirst("^/", "");
        }
        className = className.replaceAll("/", ".");
        className = className.replaceAll(".class$", "");
        return className;
    }

    public void stop() throws Exception {
        for (DefaultPageMounter pageMounter : mountPointRegistrations) {
            pageMounter.dispose();
        }
        mountPointRegistrations.clear();
    }

}
