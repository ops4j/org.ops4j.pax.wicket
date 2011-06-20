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

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FilterFactoryBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    public Class<?> getBeanClass(Element element) {
        return DefaultFilterFactory.class;
    }

    @Override
    public void doParse(Element element, BeanDefinitionBuilder builder) {
        builder.addConstructorArgReference("bundleContext");
        setConstructorElement("filterClass", element, builder);
        setConstructorElement("priority", element, builder);
        setConstructorElement("applicationName", element, builder);
        builder.addConstructorArgValue(retrieveInitParam(element));
        builder.setInitMethodName("start");
        builder.setDestroyMethodName("stop");
        builder.setLazyInit(false);
        super.doParse(element, builder);
    }

    private Map<String, String> retrieveInitParam(Element element) {
        Map<String, String> contextParams = new HashMap<String, String>();
        NodeList elementsByTagName = element.getElementsByTagNameNS("*", "init-param");
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            Element subElement = (Element) elementsByTagName.item(i);
            contextParams.put(retrieveNodeValude(subElement, "param-name"),
                retrieveNodeValude(subElement, "param-value"));
        }
        return contextParams;
    }

    private String retrieveNodeValude(Element element, String name) {
        NodeList elementsByTagName = element.getElementsByTagNameNS("*", name);
        return elementsByTagName.item(0).getChildNodes().item(0).getNodeValue();
    }

    private void setConstructorElement(String id, Element element, BeanDefinitionBuilder builder) {
        String beanAttribute = element.getAttribute(id);
        builder.addConstructorArgValue(beanAttribute);
    }

}
