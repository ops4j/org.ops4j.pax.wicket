package org.ops4j.pax.wicket.internal.spring.util;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.ops4j.pax.wicket.util.proxy.LazyInitProxyFactory;
import org.ops4j.pax.wicket.util.proxy.PaxWicketBean;
import org.osgi.framework.BundleContext;

public class ComponentInstantiationListener implements IComponentInstantiationListener {

    private Map<String, String> overwrite;
    private Class<?> page;
    private final BundleContext bundleContext;

    public ComponentInstantiationListener(Map<String, String> overwrite, Class<?> page, BundleContext bundleContext) {
        this.overwrite = overwrite;
        this.page = page;
        this.bundleContext = bundleContext;
    }

    public void onInstantiation(Component component) {
        if (!page.isInstance(component)) {
            // ignore this case; there are multible classes in this application and we only want to handle this one type
            // we're sure to be in our applicationContext
            return;
        }
        internalInstanciation(component);
    }

    public void internalInstanciation(Object component) {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(page.getClassLoader());
            Field[] fields = page.getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(PaxWicketBean.class)) {
                    continue;
                }
                Object proxy = createProxy(field);
                setField(component, field, proxy);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    private void setField(Object component, Field field, Object proxy) {
        try {
            checkAccessabilityOfField(field);
            field.set(component, proxy);
        } catch (Exception e) {
            throw new RuntimeException("Bumm", e);
        }
    }

    private void checkAccessabilityOfField(Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    private Object createProxy(Field field) {
        return LazyInitProxyFactory.createProxy(getBeanType(field), createProxyTargetLocator(field));
    }

    private ProxyTargetLocator createProxyTargetLocator(Field field) {
        PaxWicketBean annotation = field.getAnnotation(PaxWicketBean.class);
        return new ProxyTargetLocator(bundleContext, annotation, overwrite, getBeanType(field), page);
    }

    private Class<?> getBeanType(Field field) {
        Class<?> beanType = field.getType();
        return beanType;
    }
}
