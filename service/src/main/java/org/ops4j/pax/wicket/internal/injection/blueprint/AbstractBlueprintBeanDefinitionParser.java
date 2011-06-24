package org.ops4j.pax.wicket.internal.injection.blueprint;

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
