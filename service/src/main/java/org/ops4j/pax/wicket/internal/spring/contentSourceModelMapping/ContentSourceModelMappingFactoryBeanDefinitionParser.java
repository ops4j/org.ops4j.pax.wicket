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
package org.ops4j.pax.wicket.internal.spring.contentSourceModelMapping;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class ContentSourceModelMappingFactoryBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    public Class<?> getBeanClass(Element element) {
        return ContentSourceModelMappingFactory.class;
    }

    @Override
    public void doParse(Element element, BeanDefinitionBuilder builder) {
        builder.addConstructorArgReference("bundleContext");
        String attribute = element.getAttribute("contentSourceFactoryImpl");
        builder.addConstructorArgReference(attribute);
        setConstructorElement("serviceClass", element, builder);
        setConstructorElement("applicationName", element, builder);
        builder.setInitMethodName("start");
        builder.setDestroyMethodName("stop");
        builder.setLazyInit(false);
        super.doParse(element, builder);
    }

    private void setConstructorElement(String id, Element element, BeanDefinitionBuilder builder) {
        String beanAttribute = element.getAttribute(id);
        builder.addConstructorArgValue(beanAttribute);
    }

}
