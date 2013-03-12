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
package org.ops4j.pax.wicket.spi.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class InjectionParserUtil {

    private InjectionParserUtil() {
    }

    public static Map<String, String> retrieveInitParam(Element element) {
        Map<String, String> contextParams = new HashMap<String, String>();
        NodeList elementsByTagName = element.getElementsByTagNameNS("*", "init-param");
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            Element subElement = (Element) elementsByTagName.item(i);
            contextParams.put(retrieveNodeValude(subElement, "param-name"),
                retrieveNodeValude(subElement, "param-value"));
        }
        return contextParams;
    }

    public static Map<String, String> retrieveContextParam(Element element) {
        Map<String, String> contextParams = new HashMap<String, String>();
        NodeList elementsByTagName = element.getElementsByTagNameNS("*", "context-param");
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            Element subElement = (Element) elementsByTagName.item(i);
            contextParams.put(retrieveNodeValude(subElement, "param-name"),
                retrieveNodeValude(subElement, "param-value"));
        }
        return contextParams;
    }

    private static String retrieveNodeValude(Element element, String name) {
        NodeList elementsByTagName = element.getElementsByTagNameNS("*", name);
        return elementsByTagName.item(0).getChildNodes().item(0).getNodeValue();
    }

    public static Map<String, String> retrieveOverwriteElements(Element element) {
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

    public static List<String> retrieveDestinationElements(Element element) {
        List<String> destinations = new ArrayList<String>();
        NodeList elementsByTagName = element.getElementsByTagNameNS("*", "destination");
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            destinations.add(elementsByTagName.item(i).getChildNodes().item(0).getNodeValue());
        }
        return destinations;
    }

}
