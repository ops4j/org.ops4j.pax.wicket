package org.ops4j.pax.wicket.internal.injection.blueprint;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.ops4j.pax.wicket.internal.injection.ApplicationDecorator;

public class BlueprintApplicationBeanDefinitionParserTest {

    @Test
    public void testRequestRuntimeClass_shouldReturnApplicationDecorator() throws Exception {
        BlueprintApplicationBeanDefinitionParser parserToTest = new BlueprintApplicationBeanDefinitionParser();

        Class<?> runtimeClass = parserToTest.getRuntimeClass();

        assertThat(runtimeClass, typeCompatibleWith(ApplicationDecorator.class));
    }

    @Test
    public void testParse() throws Exception {
        BlueprintParserTestUtil parserTestUtil =
            new BlueprintParserTestUtil("wicket:application", new BlueprintApplicationBeanDefinitionParser());

        parserTestUtil.verifyId("application");
        parserTestUtil.verifyPropertyValue("homepageClass");
        parserTestUtil.verifyPropertyValue("mountPoint");
        parserTestUtil.verifyPropertyValue("applicationName");
        parserTestUtil.verifyPropertyReference("applicationFactory");
        parserTestUtil.verifyMapValue("contextParams", "name1", "value1", "name2", "value2");
    }
}
