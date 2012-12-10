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
package org.ops4j.pax.wicket.internal;

import static javax.activation.FileTypeMap.getDefaultFileTypeMap;

import java.io.IOException;
import java.net.URL;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericContext implements HttpContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericContext.class);

    private final String mountPoint;
    private final MimetypesFileTypeMap typeMap;
    private final Bundle bundle;

    public GenericContext(Bundle bundle, String mountPoint) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("GenericContext(" + mountPoint + " )");
        }
        this.bundle = bundle;
        this.mountPoint = normalizeMountPoint(mountPoint);
        typeMap = (MimetypesFileTypeMap) getDefaultFileTypeMap();
        typeMap.addMimeTypes("text/css css");
    }

    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("handleSecurity()");
        }
        return true;
    }

    public URL getResource(String resourceName) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getResource( " + resourceName + " )");
        }

        if (resourceName.startsWith(mountPoint)) {
            resourceName = resourceName.substring(mountPoint.length());
        }
        return bundle.getResource(resourceName);
    }

    public String getMimeType(String resourceName) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getMimeType( " + resourceName + " )");
        }
        URL resource = getResource(resourceName);
        if (resource == null) {
            return null;
        }
        String url = resource.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("         URL: " + url);
        }

        String contentType = typeMap.getContentType(url);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(" ContentType: " + contentType);
        }
        return contentType;
    }

    public static String normalizeMountPoint(String mountPoint) {
        if (!mountPoint.startsWith("/")) {
            mountPoint = "/" + mountPoint;
        }
        return mountPoint;
    }
}
