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

import org.apache.wicket.Application;
import org.apache.wicket.application.IClassResolver;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.serialize.java.JavaSerializer;
import org.apache.wicket.settings.IApplicationSettings;

import java.io.*;

/**
 * A simple wrapper for the original wicket serializer making it possible to serialize class which inject osgi
 * bundle based classes.
 */
public class PaxWicketSerializer extends JavaSerializer {
    public PaxWicketSerializer(String applicationKey) {
        super(applicationKey);
    }

    @Override
    protected ObjectInputStream newObjectInputStream(InputStream in) throws IOException {
        return new PaxWicketObjectInputStream(in, getClassResolver());
    }

    @Override
    protected ObjectOutputStream newObjectOutputStream(OutputStream out) throws IOException {
        return new PaxWicketObjectOutputStream(out);
    }

    private IClassResolver getClassResolver() {
        Application application = WebApplication.get();
        IApplicationSettings appSettings = application.getApplicationSettings();
        return appSettings.getClassResolver();
    }
}
