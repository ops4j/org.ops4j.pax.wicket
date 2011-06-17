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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ContentSourceFactoryBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    public Class<?> getBeanClass(Element element) {
        return ContentSourceFactory.class;
    }

    @Override
    public void doParse(Element element, BeanDefinitionBuilder builder) {
        builder.addConstructorArgReference("bundleContext");
        builder.addConstructorArgValue(retrieveOverwriteElements(element));
        setConstructorElement("contentSourceId", element, builder);
        setConstructorElement("applicationName", element, builder);
        setConstructorElement("contentSourceClass", element, builder);
        builder.addConstructorArgValue(retrieveDestinationElements(element));
        builder.setInitMethodName("start");
        builder.setDestroyMethodName("stop");
        builder.setLazyInit(false);
        super.doParse(element, builder);
    }

    private Map<String, String> retrieveOverwriteElements(Element element) {
        Map<String, String> overwrites = new HashMap<String, String>();
        NodeList elementsByTagName = element.getElementsByTagNameNS("*", "overwrite");
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            Element overwrite = (Element) elementsByTagName.item(i);
            String originalBeanId = overwrite.getAttribute("originalBeanId");
            String newBeanId = overwrite.getAttribute("newBeanId");
            overwrites.put(originalBeanId, newBeanId);
        }
        return overwrites;
    }

    private List<String> retrieveDestinationElements(Element element) {
        List<String> destinations = new ArrayList<String>();
        NodeList elementsByTagName = element.getElementsByTagNameNS("*", "destination");
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            destinations.add(elementsByTagName.item(i).getChildNodes().item(0).getNodeValue());
        }
        return destinations;
    }

    private void setConstructorElement(String id, Element element, BeanDefinitionBuilder builder) {
        String beanAttribute = element.getAttribute(id);
        builder.addConstructorArgValue(beanAttribute);
    }
}
