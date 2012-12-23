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

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.aries.blueprint.NamespaceHandler;
import org.apache.aries.blueprint.ParserContext;
import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class BlueprintNamespaceHandler implements NamespaceHandler {

    Map<String, AbstractBlueprintBeanDefinitionParser> namespaceRegistrations;

    public BlueprintNamespaceHandler() {
        namespaceRegistrations = new HashMap<String, AbstractBlueprintBeanDefinitionParser>();
        namespaceRegistrations.put("application", new BlueprintApplicationBeanDefinitionParser());
        namespaceRegistrations.put("page", new BlueprintPageFactoryBeanDefinitionParser());
        namespaceRegistrations.put("classResolver", new BlueprintClassResolverDefinitionParser());
        namespaceRegistrations.put("injectionProvider", new BlueprintInjectionResolverDefinitionParser());
        namespaceRegistrations.put("filter", new BlueprintFilterFactoryBeanDefinitionParser());
        namespaceRegistrations.put("autoPageMounter", new BlueprintAutoPageMounterDefinitionParser());
    }

    public ComponentMetadata decorate(Node node, ComponentMetadata component, ParserContext context) {
        throw new ComponentDefinitionException("Bad xml syntax: node decoration is not supported");
    }

    @SuppressWarnings("rawtypes")
    public Set<Class> getManagedClasses() {
        Set<Class> managedClasses = new HashSet<Class>();
        Collection<AbstractBlueprintBeanDefinitionParser> abstractBlueprintBeanDefinitionParsers =
            namespaceRegistrations.values();
        for (AbstractBlueprintBeanDefinitionParser abstractBlueprintBeanDefinitionParser : abstractBlueprintBeanDefinitionParsers) {
            managedClasses.add(abstractBlueprintBeanDefinitionParser.getRuntimeClass());
        }
        return managedClasses;
    }

    public URL getSchemaLocation(String schemaLocation) {
        return getClass().getResource("wicket.xsd");
    }

    public Metadata parse(Element element, ParserContext context) {
        AbstractBlueprintBeanDefinitionParser definitionParser = retrieveDefinitionParser(element);
        try {
            return definitionParser.parse(element, context);
        } catch (Exception e) {
            throw new ComponentDefinitionException("Could not parse " + element.getNodeName() + " because of "
                    + e.getMessage(), e);
        }
    }

    private AbstractBlueprintBeanDefinitionParser retrieveDefinitionParser(Node node) {
        if (namespaceRegistrations.containsKey(node.getNodeName())) {
            return namespaceRegistrations.get(node.getNodeName());
        }
        if (namespaceRegistrations.containsKey(node.getLocalName())) {
            return namespaceRegistrations.get(node.getLocalName());
        }
        throw new IllegalStateException("Unexpected element " + node.getNodeName());
    }

}
