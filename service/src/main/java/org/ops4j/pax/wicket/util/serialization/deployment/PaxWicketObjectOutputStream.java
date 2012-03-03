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
package org.ops4j.pax.wicket.util.serialization.deployment;

import static org.apache.wicket.util.io.SerializableChecker.isAvailable;
import static org.ops4j.lang.NullArgumentException.validateNotNull;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author edward.yakop@gmail.com
 * @since 0.5.4
 */
public class PaxWicketObjectOutputStream extends ObjectOutputStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaxWicketObjectOutputStream.class);
    protected final ObjectOutputStream outputStream;

    public PaxWicketObjectOutputStream(OutputStream outputStream) throws IOException {
        validateNotNull(outputStream, "outputStream");
        this.outputStream = new OSGiAwareOutputStream(outputStream);
    }

    @Override
    protected void writeObjectOverride(final Object object) throws IOException {
        try {
            outputStream.writeObject(object);
        } catch (IOException e) {
            if (isAvailable()) {
                // trigger serialization again, but this time gather some more info
                new PaxWicketSerializableChecker((NotSerializableException) e) {
                    @Override
                    protected boolean validateAdditionalSerializableConditions(Object obj) {
                        return !(obj instanceof BundleContext) && !(obj instanceof Bundle);
                    }

                    @Override
                    protected Object additionalObjectReplacements(Object streamObj) {
                        if (streamObj instanceof BundleContext) {
                            BundleContext context = (BundleContext) streamObj;
                            streamObj = new ReplaceBundleContext(context);
                        } else if (streamObj instanceof Bundle) {
                            Bundle bundle = (Bundle) streamObj;
                            streamObj = new ReplaceBundle(bundle);
                        }
                        return streamObj;
                    }
                }.writeObject(object);
                // if we get here, we didn't fail, while we should;
                throw e;
            }

            throw e;
        } catch (RuntimeException e) {
            LOGGER.error("error writing object " + object + ": " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public final void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public final void close() throws IOException {
        outputStream.close();
    }

    private static final class OSGiAwareOutputStream extends ObjectOutputStream {

        private OSGiAwareOutputStream(OutputStream outputStream)
            throws IOException {
            super(outputStream);
            enableReplaceObject(true);
        }

        @Override
        protected Object replaceObject(Object object)
            throws IOException {
            if (object instanceof BundleContext) {
                BundleContext context = (BundleContext) object;
                return new ReplaceBundleContext(context);
            } else if (object instanceof Bundle) {
                Bundle bundle = (Bundle) object;
                return new ReplaceBundle(bundle);
            } else {
                return super.replaceObject(object);
            }
        }
    }

}
