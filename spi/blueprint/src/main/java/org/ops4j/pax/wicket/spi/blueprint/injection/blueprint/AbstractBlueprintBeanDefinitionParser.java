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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.aries.blueprint.ParserContext;
import org.apache.aries.blueprint.mutable.MutableBeanMetadata;
import org.apache.aries.blueprint.mutable.MutableCollectionMetadata;
import org.apache.aries.blueprint.mutable.MutableMapMetadata;
import org.apache.aries.blueprint.mutable.MutableRefMetadata;
import org.apache.aries.blueprint.mutable.MutableValueMetadata;
import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.RefMetadata;
import org.osgi.service.blueprint.reflect.ValueMetadata;
import org.w3c.dom.Element;

public abstract class AbstractBlueprintBeanDefinitionParser {

    public AbstractBlueprintBeanDefinitionParser() {
    }

    public Metadata parse(Element element, ParserContext context) throws Exception {
        MutableBeanMetadata beanMetadata = context.createMetadata(MutableBeanMetadata.class);
        beanMetadata.setRuntimeClass(getRuntimeClass());
        beanMetadata.setActivation(BeanMetadata.ACTIVATION_EAGER);
        beanMetadata.setScope(BeanMetadata.SCOPE_SINGLETON);
        beanMetadata.setId(getId(element));
        beanMetadata.setInitMethod("start");
        beanMetadata.setDestroyMethod("stop");
        beanMetadata.addProperty("bundleContext", createRef(context, "blueprintBundleContext"));
        extractRemainingMetaData(element, context, beanMetadata);
        return beanMetadata;
    }

    public abstract Class<?> getRuntimeClass();

    protected abstract void extractRemainingMetaData(Element element, ParserContext context,
            MutableBeanMetadata beanMetadata) throws Exception;

    private String getId(Element element) {
        return element.getAttribute("id");
    }

    protected void addPropertyValueFromElement(String id, Element node, ParserContext context,
            MutableBeanMetadata beanMetadata) {
        String attribute = node.getAttribute(id);
        beanMetadata.addProperty(id, createStringValue(context, attribute));
    }

    protected void addPropertyValueFromElement(String id, String injectionId, Element node, ParserContext context,
            MutableBeanMetadata beanMetadata) {
        String attribute = node.getAttribute(id);
        beanMetadata.addProperty(injectionId, createStringValue(context, attribute));
    }

    protected void addPropertyReferenceFromElement(String id, Element node, ParserContext context,
            MutableBeanMetadata beanMetadata) {
        String attribute = node.getAttribute(id);
        beanMetadata.addProperty(id, createRef(context, attribute));
    }

    protected void addPropertyReferenceForMap(String id, ParserContext context, MutableBeanMetadata beanMetadata,
            Map<String, String> values) {
        MutableMapMetadata mapMetadata = context.createMetadata(MutableMapMetadata.class);
        for (Entry<String, String> value : values.entrySet()) {
            mapMetadata.addEntry(createStringValue(context, value.getKey()),
                createStringValue(context, value.getValue()));
        }
        beanMetadata.addProperty(id, mapMetadata);
    }

    protected void addPropertyReferenceForList(String id, ParserContext context, MutableBeanMetadata beanMetadata,
            List<String> values) {
        MutableCollectionMetadata collectionMetadata = context.createMetadata(MutableCollectionMetadata.class);
        collectionMetadata.setCollectionClass(List.class);
        for (String value : values) {
            collectionMetadata.addValue(createStringValue(context, value));
        }
        beanMetadata.addProperty(id, collectionMetadata);
    }

    protected ValueMetadata createStringValue(ParserContext context, String str) {
        MutableValueMetadata value = context.createMetadata(MutableValueMetadata.class);
        value.setStringValue(str);
        return value;
    }

    protected RefMetadata createRef(ParserContext context, String id) {
        MutableRefMetadata idref = context.createMetadata(MutableRefMetadata.class);
        idref.setComponentId(id);
        return idref;
    }

}
