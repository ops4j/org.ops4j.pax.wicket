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
package org.ops4j.pax.wicket.internal.spring;

import org.ops4j.pax.wicket.internal.spring.application.ApplicationBeanDefinitionParser;
import org.ops4j.pax.wicket.internal.spring.classResolver.ClassResolverDefinitionParser;
import org.ops4j.pax.wicket.internal.spring.componentContentSourceFactory.ComponentContentSourceFactoryBeanDefinitionParser;
import org.ops4j.pax.wicket.internal.spring.contentAggregator.RootContentAggregatorBeanDefinitionParser;
import org.ops4j.pax.wicket.internal.spring.contentSource.ContentSourceFactoryBeanDefinitionParser;
import org.ops4j.pax.wicket.internal.spring.contentSourceModelMapping.ContentSourceModelMappingFactoryBeanDefinitionParser;
import org.ops4j.pax.wicket.internal.spring.filter.FilterFactoryBeanDefinitionParser;
import org.ops4j.pax.wicket.internal.spring.injectionResolver.InjectionResolverDefinitionParser;
import org.ops4j.pax.wicket.internal.spring.page.PageFactoryBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class PaxWicketNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("application", new ApplicationBeanDefinitionParser());
        registerBeanDefinitionParser("page", new PageFactoryBeanDefinitionParser());
        registerBeanDefinitionParser("contentSource", new ContentSourceFactoryBeanDefinitionParser());
        registerBeanDefinitionParser("contentSourceModelMapping",
            new ContentSourceModelMappingFactoryBeanDefinitionParser());
        registerBeanDefinitionParser("contentAggregator", new RootContentAggregatorBeanDefinitionParser());
        registerBeanDefinitionParser("classResolver", new ClassResolverDefinitionParser());
        registerBeanDefinitionParser("injectionProvider", new InjectionResolverDefinitionParser());
        registerBeanDefinitionParser("filter", new FilterFactoryBeanDefinitionParser());
        registerBeanDefinitionParser("componentContentSourceFactory",
            new ComponentContentSourceFactoryBeanDefinitionParser());
    }

}
