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
package org.ops4j.pax.wicket.util.proxy;

import java.io.InvalidClassException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.wicket.Application;
import org.apache.wicket.IClusterable;
import org.apache.wicket.application.IClassResolver;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.IApplicationSettings;

public class LazyInitProxyFactory {

    private static final List<?> PRIMITIVES = Arrays.asList(new Class[]{ String.class, byte.class,
            Byte.class, short.class, Short.class, int.class, Integer.class, long.class, Long.class,
            float.class, Float.class, double.class, Double.class, char.class, Character.class,
            boolean.class, Boolean.class });

    public static Object createProxy(final Class<?> type, final IProxyTargetLocator locator) {
        if (PRIMITIVES.contains(type) || Enum.class.isAssignableFrom(type)) {
            // We special-case primitives as sometimes people use these as
            // SpringBeans (WICKET-603, WICKET-906). Go figure.
            return locator.locateProxyTarget();
        } else if (type.isInterface()) {
            JdkHandler handler = new JdkHandler(type, locator);

            try {
                return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{ type, Serializable.class, ILazyInitProxy.class,
                            IWriteReplace.class }, handler);
            } catch (IllegalArgumentException e) {
                /*
                 * STW: In some clustering environments it appears the context classloader fails to load the proxied
                 * interface (currently seen in BEA WLS 9.x clusters). If this happens, we can try and fall back to the
                 * classloader (current) that actually loaded this class.
                 */
                return Proxy.newProxyInstance(LazyInitProxyFactory.class.getClassLoader(),
                    new Class[]{ type, Serializable.class, ILazyInitProxy.class,
                            IWriteReplace.class }, handler);
            }

        } else {
            CGLibInterceptor handler = new CGLibInterceptor(type, locator);

            Enhancer e = new Enhancer();
            e.setInterfaces(new Class[]{ Serializable.class, ILazyInitProxy.class,
                    IWriteReplace.class });
            e.setSuperclass(type);
            e.setCallback(handler);
            e.setNamingPolicy(new DefaultNamingPolicy()
            {
                @Override
                public String getClassName(final String prefix, final String source,
                        final Object key, final Predicate names)
                {
                    return super.getClassName("WICKET_" + prefix, source, key, names);
                }
            });

            return e.create();
        }
    }

    protected static interface IWriteReplace {
        Object writeReplace() throws ObjectStreamException;
    }

    static class ProxyReplacement implements IClusterable {
        private static final long serialVersionUID = 1L;

        private final IProxyTargetLocator locator;

        private final String type;

        public ProxyReplacement(String type, IProxyTargetLocator locator) {
            this.type = type;
            this.locator = locator;
        }

        private Object readResolve() throws ObjectStreamException {
            Class<?> clazz;
            try {
                Application application = WebApplication.get();
                IApplicationSettings appSettings = application.getApplicationSettings();
                IClassResolver classResolver = appSettings.getClassResolver();
                clazz = classResolver.resolveClass(type);
            } catch (ClassNotFoundException e) {
                throw new InvalidClassException(type, "could not resolve class [" + type +
                        "] when deserializing proxy");
            }
            ClassLoader currentClassloader = Thread.currentThread().getContextClassLoader();
            try {
                ClassLoader classLoader = clazz.getClassLoader();
                if (classLoader != null) {
                    Thread.currentThread().setContextClassLoader(classLoader);
                }
                return LazyInitProxyFactory.createProxy(clazz, locator);
            } finally {
                Thread.currentThread().setContextClassLoader(currentClassloader);
            }
        }
    }

    private static class CGLibInterceptor
            implements
            MethodInterceptor,
            ILazyInitProxy,
            Serializable,
            IWriteReplace {
        private static final long serialVersionUID = 1L;

        private final IProxyTargetLocator locator;

        private final String typeName;

        private transient Object target;

        public CGLibInterceptor(Class<?> type, IProxyTargetLocator locator) {
            super();
            typeName = type.getName();
            this.locator = locator;
        }

        public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy)
            throws Throwable {
            if (isFinalizeMethod(method)) {
                // swallow finalize call
                return null;
            } else if (isEqualsMethod(method)) {
                return equals(args[0]) ? Boolean.TRUE : Boolean.FALSE;
            } else if (isHashCodeMethod(method)) {
                return new Integer(hashCode());
            } else if (isToStringMethod(method)) {
                return toString();
            } else if (isWriteReplaceMethod(method)) {
                return writeReplace();
            } else if (method.getDeclaringClass().equals(ILazyInitProxy.class)) {
                return getObjectLocator();
            }

            if (target == null) {
                target = locator.locateProxyTarget();
            }
            return proxy.invoke(target, args);
        }

        public IProxyTargetLocator getObjectLocator() {
            return locator;
        }

        public Object writeReplace() throws ObjectStreamException {
            return new ProxyReplacement(typeName, locator);
        }
    }

    /**
     * Invocation handler for proxies representing interface based object. For interface backed objects dynamic jdk
     * proxies are used.
     * 
     * @author Igor Vaynberg (ivaynberg)
     * 
     */
    private static class JdkHandler
            implements
            InvocationHandler,
            ILazyInitProxy,
            Serializable,
            IWriteReplace {
        private static final long serialVersionUID = 1L;

        private final IProxyTargetLocator locator;

        private final String typeName;

        private transient Object target;

        /**
         * Constructor
         * 
         * @param type class of object this handler will represent
         * 
         * @param locator object locator used to locate the object this proxy represents
         */
        public JdkHandler(Class<?> type, IProxyTargetLocator locator) {
            super();
            this.locator = locator;
            typeName = type.getName();
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (isFinalizeMethod(method)) {
                // swallow finalize call
                return null;
            } else if (isEqualsMethod(method)) {
                return equals(args[0]) ? Boolean.TRUE : Boolean.FALSE;
            } else if (isHashCodeMethod(method)) {
                return new Integer(hashCode());
            } else if (isToStringMethod(method)) {
                return toString();
            } else if (method.getDeclaringClass().equals(ILazyInitProxy.class)) {
                return getObjectLocator();
            } else if (isWriteReplaceMethod(method)) {
                return writeReplace();
            }

            if (target == null) {

                target = locator.locateProxyTarget();
            }
            try {
                return method.invoke(target, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

        public IProxyTargetLocator getObjectLocator() {
            return locator;
        }

        public Object writeReplace() throws ObjectStreamException {
            return new ProxyReplacement(typeName, locator);
        }
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

    /**
     * Checks if the method is the writeReplace method
     * 
     * @param method method being tested
     * @return true if the method is the writeReplace method, false otherwise
     */
    protected static boolean isWriteReplaceMethod(Method method) {
        return method.getReturnType() == Object.class && method.getParameterTypes().length == 0 &&
                method.getName().equals("writeReplace");
    }
}
