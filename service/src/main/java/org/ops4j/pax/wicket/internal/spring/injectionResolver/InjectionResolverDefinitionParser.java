package org.ops4j.pax.wicket.internal.spring.injectionResolver;

import org.ops4j.pax.wicket.util.BundleInjectionProviderHelper;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class InjectionResolverDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return BundleInjectionProviderHelper.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        bean.addConstructorArgReference("bundleContext");
        String attribute = element.getAttribute("applicationName");
        bean.addConstructorArgValue(attribute);
        bean.setLazyInit(false);
        bean.setInitMethodName("register");
        bean.setDestroyMethodName("dispose");
    }

}
