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

import org.ops4j.pax.wicket.api.PaxWicketBeanInjectionSource;
import org.ops4j.pax.wicket.spi.support.ApplicationDecorator;
import org.ops4j.pax.wicket.spi.support.InjectionParserUtil;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

public class SpringApplicationBeanDefinitionParser extends AbstractSpringBeanDefinitionParser {

    @Override
    public Class<?> getBeanClass(Element element) {
        return ApplicationDecorator.class;
    }

    @Override
    public void prepareInjection(Element element, BeanDefinitionBuilder bean) {
        addPropertyValueFromElement("class", "applicationClass", element, bean);
        addPropertyValueFromElement("mountPoint", element, bean);
        addPropertyValueFromElement("applicationName", element, bean);
        bean.addPropertyValue("contextParams", InjectionParserUtil.retrieveContextParam(element));
        String injectionSource = element.getAttribute("injectionSource");
        if (injectionSource == null || injectionSource.isEmpty()) {
            bean.addPropertyValue("injectionSource", PaxWicketBeanInjectionSource.INJECTION_SOURCE_SPRING);
        } else {
            bean.addPropertyValue("injectionSource", injectionSource);
        }
    }

}
