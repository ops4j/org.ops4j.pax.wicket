package org.ops4j.pax.wicket.internal.injection.blueprint;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.ops4j.pax.wicket.internal.injection.BundleClassResolverHelperDecorator;

public class BlueprintClassResolverDefinitionParserTest {
    @Test
    public void testRequestRuntimeClass_shouldReturnApplicationDecorator() throws Exception {
        BlueprintClassResolverDefinitionParser parserToTest = new BlueprintClassResolverDefinitionParser();

        Class<?> runtimeClass = parserToTest.getRuntimeClass();

        assertThat(runtimeClass, typeCompatibleWith(BundleClassResolverHelperDecorator.class));
    }

    @Test
    public void testParse() throws Exception {
        BlueprintParserTestUtil parserTestUtil =
            new BlueprintParserTestUtil("wicket:classResolver", new BlueprintClassResolverDefinitionParser());

        parserTestUtil.verifyId("classResolver");
        parserTestUtil.verifyPropertyValue("applicationName");
    }
}
