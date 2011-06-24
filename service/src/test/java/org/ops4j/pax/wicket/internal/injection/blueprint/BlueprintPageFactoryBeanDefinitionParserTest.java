package org.ops4j.pax.wicket.internal.injection.blueprint;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.ops4j.pax.wicket.internal.injection.PageFactoryDecorator;

public class BlueprintPageFactoryBeanDefinitionParserTest {

    @Test
    public void testRequestBeanType_shouldReturnDefaultContentSourceFactory() throws Exception {
        BlueprintPageFactoryBeanDefinitionParser parserToTest = new BlueprintPageFactoryBeanDefinitionParser();

        Class<?> beanClass = parserToTest.getRuntimeClass();

        assertThat(beanClass, typeCompatibleWith(PageFactoryDecorator.class));
    }

    @Test
    public void testParse() throws Exception {
        BlueprintParserTestUtil parserTestUtil =
            new BlueprintParserTestUtil("wicket:page", new BlueprintPageFactoryBeanDefinitionParser());

        parserTestUtil.verifyPropertyValue("pageId");
        parserTestUtil.verifyPropertyValue("applicationName");
        parserTestUtil.verifyPropertyValue("pageName");
        parserTestUtil.verifyPropertyValue("pageClass");
        parserTestUtil.verifyMapValue("overwrites", "old1", "new1", "old2", "new2");
    }
}
