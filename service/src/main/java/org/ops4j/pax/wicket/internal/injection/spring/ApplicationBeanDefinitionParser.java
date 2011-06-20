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

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ApplicationBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    public Class<?> getBeanClass(Element element) {
        return ApplicationBuilder.class;
    }

    @Override
    public void doParse(Element element, BeanDefinitionBuilder bean) {
        setElement("homepageClass", element, bean);
        setElement("mountPoint", element, bean);
        setElement("applicationName", element, bean);
        setElement("applicationFactory", element, bean);
        bean.addPropertyValue("contextParams", retrieveContextParam(element));
        bean.setLazyInit(false);
        bean.setInitMethodName("register");
        bean.setDestroyMethodName("unregister");
    }

    private void setElement(String id, Element element, BeanDefinitionBuilder bean) {
        String beanElement = element.getAttribute(id);
        bean.addPropertyValue(id, beanElement);
    }

    private Map<String, String> retrieveContextParam(Element element) {
        Map<String, String> contextParams = new HashMap<String, String>();
        NodeList elementsByTagName = element.getElementsByTagNameNS("*", "context-param");
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

}
