package org.ops4j.pax.wicket.internal.injection.blueprint;

import org.apache.aries.blueprint.ParserContext;
import org.apache.aries.blueprint.mutable.MutableBeanMetadata;
import org.ops4j.pax.wicket.internal.injection.ApplicationDecorator;
import org.ops4j.pax.wicket.internal.injection.InjectionParserUtil;
import org.w3c.dom.Element;

public class BlueprintApplicationBeanDefinitionParser extends AbstractBlueprintBeanDefinitionParser {

    @Override
    public Class<?> getRuntimeClass() {
        return ApplicationDecorator.class;
    }

    @Override
    protected void extractRemainingMetaData(Element element, ParserContext context, MutableBeanMetadata beanMetadata)
        throws Exception {
        addPropertyValueFromElement("homepageClass", element, context, beanMetadata);
        addPropertyValueFromElement("mountPoint", element, context, beanMetadata);
        addPropertyValueFromElement("applicationName", element, context, beanMetadata);
        addPropertyReferenceFromElement("applicationFactory", element, context, beanMetadata);
        addPropertyReferenceForMap("contextParams", context, beanMetadata,
            InjectionParserUtil.retrieveContextParam(element));
    }

}
