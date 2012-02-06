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
package org.ops4j.pax.wicket.util.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.wicket.Application;
import org.apache.wicket.application.IClassResolver;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.IApplicationSettings;
import org.ops4j.pax.wicket.util.serialization.deployment.PaxWicketObjectInputStream;
import org.ops4j.pax.wicket.util.serialization.deployment.PaxWicketObjectOutputStream;

/**
 * @author edward.yakop@gmail.com
 * @since 0.5.4
 */
public final class PaxWicketObjectStreamFactory /*implements IObjectStreamFactory*/ {

    private boolean m_developmentMode;

    public PaxWicketObjectStreamFactory(boolean isDevelopmentMode) {
        m_developmentMode = isDevelopmentMode;
    }

    public final ObjectInputStream newObjectInputStream(InputStream in) throws IOException {
        IClassResolver classResolver = getClassResolver();
        if (m_developmentMode) {
            return new PaxWicketObjectInputStream(in, classResolver);
        } else {
            return new PaxWicketObjectInputStream(in, classResolver);
        }
    }

    private IClassResolver getClassResolver() {
        Application application = WebApplication.get();
        IApplicationSettings appSettings = application.getApplicationSettings();
        return appSettings.getClassResolver();
    }

    public final ObjectOutputStream newObjectOutputStream(OutputStream out) throws IOException {
        if (m_developmentMode) {
            return new PaxWicketObjectOutputStream(out);
        } else {
            return new PaxWicketObjectOutputStream(out);
        }
    }
}
