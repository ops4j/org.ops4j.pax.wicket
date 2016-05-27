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
package org.ops4j.pax.wicket.test.spring;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;

/**
 * Mock application context object. This mock context allows easy creation of
 * unit tests by allowing the user to put bean instances into the context. Only
 * {@link #getBean(String)}, {@link #getBean(String, Class)}, and
 * {@link #getBeansOfType(Class)
 *} are implemented so far. Any other method
 * throws {@link java.lang.UnsupportedOperationException}.
 *
 * @author Igor Vaynberg (ivaynberg)
 * @version $Id: $Id
 */
@SuppressWarnings("unchecked")
public class ApplicationContextMock implements ApplicationContext, Serializable {
    private static final long         serialVersionUID = 1L;

    private final Map<String, Object> beans            = new HashMap<String, Object>();

    /**
     * <p>putBean.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param bean a {@link java.lang.Object} object.
     */
    public void putBean(String name, Object bean) {
        beans.put(name, bean);
    }

    /**
     * <p>putBean.</p>
     *
     * @param bean a {@link java.lang.Object} object.
     */
    public void putBean(Object bean) {
        putBean(bean.getClass().getName(), bean);
    }

    /** {@inheritDoc} */
    public Object getBean(String name) throws BeansException {
        Object bean = beans.get(name);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException("no bean with name '" + name + "' present in test context!");
        }
        return bean;
    }

    /** {@inheritDoc} */
    public Object getBean(String name, Class requiredType) throws BeansException {
        Object bean = getBean(name);
        if (!requiredType.isAssignableFrom(bean.getClass())) {
            throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
        }
        return bean;
    }

    /** {@inheritDoc} */
    public Map getBeansOfType(Class type) throws BeansException {
        Map found = new HashMap();

        Iterator it = beans.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = (Entry) it.next();
            if (type.isAssignableFrom(entry.getValue().getClass())) {
                found.put(entry.getKey(), entry.getValue());
            }
        }

        return found;
    }

    /**
     * <p>getParent.</p>
     *
     * @return a {@link org.springframework.context.ApplicationContext} object.
     */
    public ApplicationContext getParent() {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>getDisplayName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDisplayName() {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>getStartupDate.</p>
     *
     * @return a long.
     */
    public long getStartupDate() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public void publishEvent(ApplicationEvent event) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public boolean containsBeanDefinition(String beanName) {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>getBeanDefinitionCount.</p>
     *
     * @return a int.
     */
    public int getBeanDefinitionCount() {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>getBeanDefinitionNames.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public String[] getBeanDefinitionNames() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public String[] getBeanNamesForType(Class type) {
        ArrayList names = new ArrayList();
        Iterator entries = beans.entrySet().iterator();
        while (entries.hasNext()) {
            Entry entry = (Entry) entries.next();
            Object bean = entry.getValue();

            if (type.isAssignableFrom(bean.getClass())) {
                String name = (String) entry.getKey();
                names.add(name);
            }
        }
        return (String[]) names.toArray(new String[names.size()]);
    }

    /** {@inheritDoc} */
    public String[] getBeanNamesForType(Class type, boolean includePrototypes, boolean includeFactoryBeans) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public Map getBeansOfType(Class type, boolean includePrototypes, boolean includeFactoryBeans) throws BeansException {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public boolean containsBean(String name) {
        return beans.containsKey(name);
    }

    /** {@inheritDoc} */
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return true;
    }

    /** {@inheritDoc} */
    public Class getType(String name) throws NoSuchBeanDefinitionException {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>getParentBeanFactory.</p>
     *
     * @return a {@link org.springframework.beans.factory.BeanFactory} object.
     */
    public BeanFactory getParentBeanFactory() {
        return null;
    }

    /** {@inheritDoc} */
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public Resource[] getResources(String locationPattern) throws IOException {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public Resource getResource(String location) {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>getAutowireCapableBeanFactory.</p>
     *
     * @return a {@link org.springframework.beans.factory.config.AutowireCapableBeanFactory} object.
     * @throws java.lang.IllegalStateException if any.
     */
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public boolean containsLocalBean(String arg0) {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>getClassLoader.</p>
     *
     * @return a {@link java.lang.ClassLoader} object.
     */
    public ClassLoader getClassLoader() {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>getId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
        throw new UnsupportedOperationException();

    }

    /**
     * <p>getBean.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param args a {@link java.lang.Object} object.
     * @return a {@link java.lang.Object} object.
     * @throws org.springframework.beans.BeansException if any.
     */
    public Object getBean(String name, Object... args) throws BeansException {
        throw new UnsupportedOperationException();

    }

    /** {@inheritDoc} */
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public boolean isTypeMatch(String name, Class targetType) throws NoSuchBeanDefinitionException {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>getBean.</p>
     *
     * @param requiredType a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws org.springframework.beans.BeansException if any.
     */
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        for (Entry<String, Object> bean : beans.entrySet()) {
            if (requiredType.isInstance(bean.getValue())) {
                return (T) bean.getValue();
            }
        }
        throw new IllegalStateException("No bean matching the type found");
    }

}
