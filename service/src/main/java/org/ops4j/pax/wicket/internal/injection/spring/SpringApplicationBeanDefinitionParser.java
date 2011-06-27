/*
 * Copyright OPS4J
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.pax.wicket.internal.injection.spring;

import org.ops4j.pax.wicket.internal.injection.ApplicationDecorator;
import org.ops4j.pax.wicket.internal.injection.InjectionParserUtil;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

public class SpringApplicationBeanDefinitionParser extends AbstractSpringBeanDefinitionParser {

    @Override
    public Class<?> getBeanClass(Element element) {
        return ApplicationDecorator.class;
    }

    @Override
    public void prepareInjection(Element element, BeanDefinitionBuilder bean) {
        addPropertyValueFromElement("homepageClass", element, bean);
        addPropertyValueFromElement("mountPoint", element, bean);
        addPropertyValueFromElement("applicationName", element, bean);
        addPropertyReferenceFromElement("applicationFactory", element, bean);
        bean.addPropertyValue("contextParams", InjectionParserUtil.retrieveContextParam(element));
    }

}
