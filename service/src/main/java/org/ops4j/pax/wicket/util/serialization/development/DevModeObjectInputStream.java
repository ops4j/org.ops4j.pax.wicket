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
package org.ops4j.pax.wicket.util.serialization.development;

import static org.ops4j.lang.NullArgumentException.validateNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.apache.wicket.application.IClassResolver;
import org.ops4j.pax.wicket.util.serialization.deployment.PaxWicketObjectInputStream;

/**
 * @author edward.yakop@gmail.com
 */
public final class DevModeObjectInputStream extends ObjectInputStream {

    private PaxWicketObjectInputStream inputStream;

    public DevModeObjectInputStream(InputStream ois, IClassResolver resolver) throws IOException,
        IllegalArgumentException {
        validateNotNull(resolver, "resolver");
        inputStream = new PaxWicketObjectInputStream(ois, resolver);
    }

    @Override
    protected final Object readObjectOverride() throws IOException, ClassNotFoundException {
        String className = (String) inputStream.readObject();

        try {
            return inputStream.readObject();
        } catch (ClassNotFoundException e) {
            // Re-throw with additional message
            String message = e.getMessage();
            throw new ClassNotFoundException("Class [" + className + "] can't be found.\nActual error:\n" + message);
        } catch (IOException e) {
            // Re-throw with additional message
            String message = e.getMessage();
            e.printStackTrace();
            throw new IOException(
                "Fail to deserialize object of class [" + className + "].\nActual error:\n" + message);
        } catch (RuntimeException e) {
            // Re-throw with additional message
            throw new RuntimeException("Fail to deserialize object of class [" + className + "].", e);
        }
    }

    @Override
    public void close() throws IOException {
        if (null != inputStream) {
            inputStream.close();
        }
    }
}
