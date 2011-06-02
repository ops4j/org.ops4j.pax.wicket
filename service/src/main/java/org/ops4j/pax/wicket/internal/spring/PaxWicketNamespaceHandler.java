package org.ops4j.pax.wicket.internal.spring;

import org.ops4j.pax.wicket.internal.spring.application.ApplicationBeanDefinitionParser;
import org.ops4j.pax.wicket.internal.spring.contentAggregator.RootContentAggregatorBeanDefinitionParser;
import org.ops4j.pax.wicket.internal.spring.contentSource.ContentSourceFactoryBeanDefinitionParser;
import org.ops4j.pax.wicket.internal.spring.contentSourceModelMapping.ContentSourceModelMappingFactoryBeanDefinitionParser;
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
    }

}
