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
package org.ops4j.pax.wicket.spi.springdm.injection.spring;

import java.util.Map;

import org.ops4j.pax.wicket.spi.support.AbstractProxyTargetLocator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

public class SpringBeanProxyTargetLocator extends AbstractProxyTargetLocator<ApplicationContext> {

    private static final long serialVersionUID = 3688782411985692696L;

    public SpringBeanProxyTargetLocator(BundleContext bundleContext, String beanName, Class<?> beanType,
            Class<?> parent, Map<String, String> overwrites) {
        super(bundleContext, beanName, beanType, parent, overwrites);
    }

    @Override
    protected BeanReactor<ApplicationContext> createStrategy() {
        if (getBeanName().isEmpty()) {
            return new AbstractProxyTargetLocator.BeanReactor<ApplicationContext>() {
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
        if (overwrites == null || overwrites.size() == 0 || !overwrites.containsKey(getBeanName())) {
            return new AbstractProxyTargetLocator.BeanReactor<ApplicationContext>() {
                public boolean containsBean(ApplicationContext applicationContext) {
                    return applicationContext.containsBean(getBeanName());
                }

                public Object createBean(ApplicationContext applicationContext) {
                    return applicationContext.getBean(getBeanName(), beanType);
                }
            };
        }
        return new AbstractProxyTargetLocator.BeanReactor<ApplicationContext>() {
            public boolean containsBean(ApplicationContext applicationContext) {
                return applicationContext.containsBean(overwrites.get(getBeanName()));
            }

            public Object createBean(ApplicationContext applicationContext) {
                return applicationContext.getBean(overwrites.get(getBeanName()), beanType);
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
