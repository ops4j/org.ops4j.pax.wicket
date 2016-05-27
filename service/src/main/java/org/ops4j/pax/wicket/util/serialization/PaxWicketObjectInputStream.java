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
import java.io.ObjectStreamClass;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.application.IClassResolver;

/**
 * <p>PaxWicketObjectInputStream class.</p>
 *
 * @author edward.yakop@gmail.com
 * @since 0.5.4
 * @version $Id: $Id
 */
public final class PaxWicketObjectInputStream extends ObjectInputStream {

    private final IClassResolver classResolver;

    /**
     * <p>Constructor for PaxWicketObjectInputStream.</p>
     *
     * @param inputStream a {@link java.io.InputStream} object.
     * @param resolver a {@link org.apache.wicket.application.IClassResolver} object.
     * @throws java.io.IOException if any.
     */
    public PaxWicketObjectInputStream(InputStream inputStream, IClassResolver resolver) throws IOException {
        super(inputStream);

        classResolver = resolver;
        enableResolveObject(true);
    }

    /** {@inheritDoc} */
    @Override
    protected final Object resolveObject(Object object) throws IOException {
        if (object instanceof ReplaceBundleContext) {
            ReplaceBundleContext replaceBundleContext = (ReplaceBundleContext) object;
            return replaceBundleContext.getBundleContext();
        } else if (object instanceof ReplaceBundle) {
            ReplaceBundle replaceBundle = (ReplaceBundle) object;
            return replaceBundle.getBundle();
        } else {
            return super.resolveObject(object);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected final Class<?> resolveClass(ObjectStreamClass objectStreamClass) throws IOException,
        ClassNotFoundException {
        String className = objectStreamClass.getName();

        Class<?> candidate = resolveClassByClassResolver(className);
        if (candidate != null) {
            return candidate;
        }

        return super.resolveClass(objectStreamClass);
    }

    private Class<?> resolveClassByClassResolver(String className) {
        Class<?> resolvedClass = null;

        try {
            resolvedClass = classResolver.resolveClass(className);
        } catch (WicketRuntimeException ex) {
            // Ignore
        } catch (ClassNotFoundException e) {
            // Ignore
        }

        return resolvedClass;
    }
}
