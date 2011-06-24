package org.ops4j.pax.wicket.internal.injection.blueprint;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.ops4j.pax.wicket.internal.injection.RootContentAggregatorDecorator;

public class BlueprintContentAggregatorBeanDefinitionParserTest {

    @Test
    public void testRequestRuntimeClass_shouldReturnRootContentAggregatorDecorator() throws Exception {
        BlueprintContentAggregatorBeanDefinitionParser parserToTest =
            new BlueprintContentAggregatorBeanDefinitionParser();

        Class<?> runtimeClass = parserToTest.getRuntimeClass();

        assertThat(runtimeClass, typeCompatibleWith(RootContentAggregatorDecorator.class));
    }

    @Test
    public void testParse() throws Exception {
        BlueprintParserTestUtil parserTestUtil =
            new BlueprintParserTestUtil("wicket:contentAggregator",
                new BlueprintContentAggregatorBeanDefinitionParser());

        parserTestUtil.verifyId("contentAggregator");
        parserTestUtil.verifyPropertyValue("applicationName");
        parserTestUtil.verifyPropertyValue("aggregationPointName");
    }

}
