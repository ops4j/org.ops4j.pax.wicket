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
package org.ops4j.pax.wicket.spi.blueprint.injection.blueprint;

import java.util.Map;

import org.ops4j.pax.wicket.spi.support.AbstractProxyTargetLocator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.container.NoSuchComponentException;

public class BlueprintBeanProxyTargetLocator extends AbstractProxyTargetLocator<BlueprintContainer> {

    private static final long serialVersionUID = 7855320656221559137L;

    public BlueprintBeanProxyTargetLocator(BundleContext bundleContext, String beanName, Class<?> beanType,
            Class<?> parent, Map<String, String> overwrites) {
        super(bundleContext, beanName, beanType, parent, overwrites);
    }

    @Override
    protected BeanReactor<BlueprintContainer> createStrategy() {
        if (getBeanName().isEmpty()) {
            throw new IllegalStateException("Blueprint requires annotation name");
        }
        if (overwrites == null || overwrites.size() == 0 || !overwrites.containsKey(getBeanName())) {
            return new BlueprintBeanReactor(getBeanName());
        }
        return new BlueprintBeanReactor(overwrites.get(getBeanName()));
    }

    @Override
    protected String getApplicationContextFilter(String symbolicBundleName) {
        return String.format("(&(%s=%s)(%s=%s))", "osgi.blueprint.container.symbolicname", symbolicBundleName,
            Constants.OBJECTCLASS, BlueprintContainer.class.getName());
    }

    private static class BlueprintBeanReactor implements BeanReactor<BlueprintContainer> {

        private final String beanName;
        private Object bean;

        public BlueprintBeanReactor(String beanName) {
            this.beanName = beanName;
        }

        public boolean containsBean(BlueprintContainer blueprintContainer) {
            try {
                bean = blueprintContainer.getComponentInstance(beanName);
            } catch (NoSuchComponentException e) {
                return false;
            }
            return true;
        }

        public Object createBean(BlueprintContainer blueprintContainer) {
            if (bean == null) {
                throw new IllegalStateException("Contains bean method must be called successfully first");
            }
            return bean;
        }

    }

    @Override
    protected Class<? extends BlueprintContainer> getContainerClass() {
        return BlueprintContainer.class;
    }
}
