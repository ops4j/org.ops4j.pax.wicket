package org.ops4j.pax.wicket.internal.spring.classResolver;

import org.ops4j.pax.wicket.util.classResolver.BundleClassResolverHelper;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class ClassResolverDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return BundleClassResolverHelper.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        bean.addConstructorArgReference("bundleContext");
        String attribute = element.getAttribute("applicationName");
        bean.addPropertyValue("applicationName", attribute);
        bean.setLazyInit(false);
        bean.setInitMethodName("register");
        bean.setDestroyMethodName("dispose");
    }

}
