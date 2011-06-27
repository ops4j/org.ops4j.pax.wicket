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
package org.ops4j.pax.wicket.internal.injection.spring;

import java.util.Map;

import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.ops4j.pax.wicket.internal.injection.AbstractProxyTargetLocator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

public class SpringBeanProxyTargetLocator extends AbstractProxyTargetLocator<ApplicationContext> {

    private static final long serialVersionUID = 3688782411985692696L;

    public SpringBeanProxyTargetLocator(BundleContext bundleContext, PaxWicketBean annotation, Class<?> beanType,
            Class<?> parent, Map<String, String> overwrites) {
        super(bundleContext, annotation, beanType, parent, overwrites);
    }

    @Override
    protected BeanReactor<ApplicationContext> createStrategy() {
        if (annotation.name().equals("")) {
            return new BeanReactor<ApplicationContext>() {
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
        }
        if (overwrites == null || overwrites.size() == 0 || !overwrites.containsKey(annotation.name())) {
            return new BeanReactor<ApplicationContext>() {
                public boolean containsBean(ApplicationContext applicationContext) {
                    return applicationContext.containsBean(annotation.name());
                }

                public Object createBean(ApplicationContext applicationContext) {
                    return applicationContext.getBean(annotation.name(), beanType);
                }
            };
        }
        return new BeanReactor<ApplicationContext>() {
            public boolean containsBean(ApplicationContext applicationContext) {
                return applicationContext.containsBean(overwrites.get(annotation.name()));
            }

            public Object createBean(ApplicationContext applicationContext) {
                return applicationContext.getBean(overwrites.get(annotation.name()), beanType);
            }
        };
    }

    @Override
    protected String getApplicationContextFilter(String symbolicBundleName) {
        return String.format("(&(%s=%s)(%s=%s))", Constants.BUNDLE_SYMBOLICNAME, symbolicBundleName,
            Constants.OBJECTCLASS, ApplicationContext.class.getName());
    }

    @Override
    protected Class<? extends ApplicationContext> getContainerClass() {
        return ApplicationContext.class;
    }

}

