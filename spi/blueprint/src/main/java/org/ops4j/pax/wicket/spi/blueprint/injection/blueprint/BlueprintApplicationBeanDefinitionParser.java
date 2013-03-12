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

import org.apache.aries.blueprint.ParserContext;
import org.apache.aries.blueprint.mutable.MutableBeanMetadata;
import org.ops4j.pax.wicket.api.PaxWicketBeanInjectionSource;
import org.ops4j.pax.wicket.spi.support.ApplicationDecorator;
import org.ops4j.pax.wicket.spi.support.InjectionParserUtil;
import org.w3c.dom.Element;

public class BlueprintApplicationBeanDefinitionParser extends AbstractBlueprintBeanDefinitionParser {

    @Override
    public Class<?> getRuntimeClass() {
        return ApplicationDecorator.class;
    }

    @Override
    protected void extractRemainingMetaData(Element element, ParserContext context, MutableBeanMetadata beanMetadata)
        throws Exception {
        addPropertyValueFromElement("class", "applicationClass", element, context, beanMetadata);
        addPropertyValueFromElement("mountPoint", element, context, beanMetadata);
        addPropertyValueFromElement("applicationName", element, context, beanMetadata);
        addPropertyReferenceForMap("contextParams", context, beanMetadata,
            InjectionParserUtil.retrieveContextParam(element));
        String injectionSource = element.getAttribute("injectionSource");
        if (injectionSource == null || injectionSource.isEmpty()) {
            beanMetadata.addProperty("injectionSource",
                createStringValue(context, PaxWicketBeanInjectionSource.INJECTION_SOURCE_BLUEPRINT));
        } else {
            beanMetadata.addProperty("injectionSource", createStringValue(context, injectionSource));
        }
    }

}
