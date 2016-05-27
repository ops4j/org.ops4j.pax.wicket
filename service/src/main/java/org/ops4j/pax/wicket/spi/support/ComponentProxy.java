
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
 *
 * @author nmw
 * @version $Id: $Id
 */
package org.ops4j.pax.wicket.spi.support;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

import net.sf.cglib.proxy.MethodProxy;

import org.ops4j.pax.wicket.spi.OverwriteProxy;
public class ComponentProxy implements OverwriteProxy, Serializable {

    private static final long serialVersionUID = 1848500647893384991L;

    private final Map<String, String> overwrites;
    private final String injectionSource;

    /**
     * <p>Constructor for ComponentProxy.</p>
     *
     * @param injectionSource a {@link java.lang.String} object.
     * @param overwrites a {@link java.util.Map} object.
     */
    public ComponentProxy(String injectionSource, Map<String, String> overwrites) {
        this.injectionSource = injectionSource;
        this.overwrites = overwrites;
    }

    /** {@inheritDoc} */
    public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (isFinalizeMethod(method)) {
            // swallow finalize call
            return null;
        } else if (isEqualsMethod(method)) {
            return equals(args[0]) ? Boolean.TRUE : Boolean.FALSE;
        } else if (isHashCodeMethod(method)) {
            return new Integer(hashCode());
        } else if (isToStringMethod(method)) {
            return toString();
        } else if (isGetOverwritesMethod(method)) {
            return getOverwrites();
        } else if (isGetInjectionSourceMethod(method)) {
            return getInjectionSource();
        }

        return proxy.invokeSuper(object, args);
    }

    /**
     * <p>Getter for the field <code>overwrites</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getOverwrites() {
        return overwrites;
    }

    /**
     * <p>Getter for the field <code>injectionSource</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getInjectionSource() {
        return injectionSource;
    }

    /**
     * <p>isGetOverwritesMethod.</p>
     *
     * @param method a {@link java.lang.reflect.Method} object.
     * @return a boolean.
     */
    protected static boolean isGetOverwritesMethod(Method method) {
        return method.getReturnType() == Map.class && method.getParameterTypes().length == 0 &&
                method.getName().equals("getOverwrites");
    }

    /**
     * <p>isGetInjectionSourceMethod.</p>
     *
     * @param method a {@link java.lang.reflect.Method} object.
     * @return a boolean.
     */
    protected static boolean isGetInjectionSourceMethod(Method method) {
        return method.getReturnType() == Map.class && method.getParameterTypes().length == 0 &&
                method.getName().equals("getInjectionSource");
    }

    /**
     * Checks if the method is derived from Object.equals()
     *
     * @param method method being tested
     * @return true if the method is derived from Object.equals(), false otherwise
     */
    protected static boolean isEqualsMethod(Method method) {
        return method.getReturnType() == boolean.class && method.getParameterTypes().length == 1 &&
                method.getParameterTypes()[0] == Object.class && method.getName().equals("equals");
    }

    /**
     * Checks if the method is derived from Object.hashCode()
     *
     * @param method method being tested
     * @return true if the method is defined from Object.hashCode(), false otherwise
     */
    protected static boolean isHashCodeMethod(Method method) {
        return method.getReturnType() == int.class && method.getParameterTypes().length == 0 &&
                method.getName().equals("hashCode");
    }

    /**
     * Checks if the method is derived from Object.toString()
     *
     * @param method method being tested
     * @return true if the method is defined from Object.toString(), false otherwise
     */
    protected static boolean isToStringMethod(Method method) {
        return method.getReturnType() == String.class && method.getParameterTypes().length == 0 &&
                method.getName().equals("toString");
    }

    /**
     * Checks if the method is derived from Object.finalize()
     *
     * @param method method being tested
     * @return true if the method is defined from Object.finalize(), false otherwise
     */
    protected static boolean isFinalizeMethod(Method method) {
        return method.getReturnType() == void.class && method.getParameterTypes().length == 0 &&
                method.getName().equals("finalize");
    }

}
