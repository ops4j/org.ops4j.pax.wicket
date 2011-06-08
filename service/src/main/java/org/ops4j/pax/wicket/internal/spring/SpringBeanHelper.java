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
package org.ops4j.pax.wicket.internal.spring;

import org.osgi.framework.Constants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public final class SpringBeanHelper {

    public static void registerBean(ConfigurableApplicationContext applicationContext, final String beanId,
            final Object beanInstance) {
        applicationContext.getBeanFactory().registerSingleton(beanId, beanInstance);
    }

    public static String getApplicationContextFilter(String symbolicBundleName) {
        return String.format("(&(%s=%s)(%s=%s))", Constants.BUNDLE_SYMBOLICNAME, symbolicBundleName,
            Constants.OBJECTCLASS, ApplicationContext.class.getName());
    }

}
