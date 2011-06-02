package org.ops4j.pax.wicket.internal.spring.util;

import java.util.Map;

import org.ops4j.pax.wicket.util.proxy.IProxyTargetLocator;
import org.ops4j.pax.wicket.util.proxy.PaxWicketBean;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

public class ProxyTargetLocator implements IProxyTargetLocator {

    private static final long serialVersionUID = 1L;

    private PaxWicketBean annotation;
    private Map<String, String> overwrite;
    private Class<?> beanType;
    private Class<?> parent;
    private BundleContext bundleContext;

    public ProxyTargetLocator(BundleContext bundleContext, PaxWicketBean annotation, Map<String, String> overwrite,
            Class<?> beanType, Class<?> parent) {
        this.bundleContext = bundleContext;
        this.annotation = annotation;
        this.overwrite = overwrite;
        this.beanType = beanType;
        this.parent = parent;
    }

    public Object locateProxyTarget() {
        if (bundleContext == null) {
            throw new IllegalStateException("Bundle context is not allowed to be null");
        }
        ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
        String filter =
            String.format("(&(%s=%s)(%s=%s))", Constants.BUNDLE_SYMBOLICNAME,
                bundleContext.getBundle().getSymbolicName(), Constants.OBJECTCLASS,
                ApplicationContext.class.getName());
        ServiceReference[] references = null;
        try {
            references = bundleContext.getServiceReferences(ApplicationContext.class.getName(), filter);
        } catch (InvalidSyntaxException e) {
            throw new IllegalStateException("not possible", e);
        }
        if (references == null || references.length == 0) {
            throw new IllegalStateException(String.format("Found %s service references for %s; this is not OK...",
                    references.length, bundleContext.getBundle().getSymbolicName()));
        }
        try {
            Thread.currentThread().setContextClassLoader(parent.getClassLoader());
            BeanReactor strategy = createStrategy();
            for (ServiceReference serviceReference : references) {
                ApplicationContext service = (ApplicationContext) bundleContext.getService(serviceReference);
                try {
                    if (!strategy.containsBean(service)) {
                        continue;
                    }
                    return strategy.createBean(service);
                } finally {
                    bundleContext.ungetService(serviceReference);
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassloader);
        }
        throw new IllegalStateException(String.format("Bundle %s can no longer attach bean %s to page", bundleContext
            .getBundle().getSymbolicName(), beanType.getName()));
    }

    private BeanReactor createStrategy() {
        if (annotation.name().equals("")) {
            return new BeanReactor() {
                public boolean containsBean(ApplicationContext applicationContext) {
                    try {
                        applicationContext.getBean(beanType);
                    } catch (NoSuchBeanDefinitionException e) {
                        return false;
                    }
                    return true;
                }

                public Object createBean(ApplicationContext applicationContext) {
                    return applicationContext.getBean(beanType);
                }
            };
        } else {
            if (overwrite != null && overwrite.containsKey(annotation.name())) {
                return new BeanReactor() {
                    public boolean containsBean(ApplicationContext applicationContext) {
                        String alternativeBeanName = overwrite.get(annotation.name());
                        return applicationContext.containsBean(alternativeBeanName);
                    }

                    public Object createBean(ApplicationContext applicationContext) {
                        String alternativeBeanName = overwrite.get(annotation.name());
                        return applicationContext.getBean(alternativeBeanName, beanType);
                    }
                };
            } else {
                return new BeanReactor() {
                    public boolean containsBean(ApplicationContext applicationContext) {
                        return applicationContext.containsBean(annotation.name());
                    }

                    public Object createBean(ApplicationContext applicationContext) {
                        return applicationContext.getBean(annotation.name(), beanType);
                    }
                };
            }
        }
    }

    private static interface BeanReactor {
        boolean containsBean(ApplicationContext applicationContext);

        Object createBean(ApplicationContext applicationContext);
    }

}
