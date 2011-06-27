package org.ops4j.pax.wicket.internal.injection.blueprint;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.ops4j.pax.wicket.internal.injection.ComponentContentSourceFactoryDecorator;

public class BlueprintComponentContentSourceFactoryBeanDefinitionParserTest {
    @Test
    public void testRequestRuntimeClass_shouldReturnApplicationDecorator() throws Exception {
        BlueprintComponentContentSourceFactoryBeanDefinitionParser parserToTest =
            new BlueprintComponentContentSourceFactoryBeanDefinitionParser();

        Class<?> runtimeClass = parserToTest.getRuntimeClass();

        assertThat(runtimeClass, typeCompatibleWith(ComponentContentSourceFactoryDecorator.class));
    }

    @Test
    public void testParse() throws Exception {
        BlueprintParserTestUtil parserTestUtil =
            new BlueprintParserTestUtil("wicket:componentContentSourceFactory",
                new BlueprintComponentContentSourceFactoryBeanDefinitionParser());

        parserTestUtil.verifyId("testBean");
        parserTestUtil.verifyPropertyValue("applicationName");
        parserTestUtil.verifyPropertyReference("componentContentSourceFactory");
    }
}
