package org.ops4j.pax.wicket.internal.injection.blueprint;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.ops4j.pax.wicket.internal.injection.BundleScanningMountPointProviderDecorator;

public class BlueprintAutoPageMounterDefinitionParserTest {

    @Test
    public void testRequestBeanType_shouldReturnDefaultContentSourceFactory() throws Exception {
        BlueprintAutoPageMounterDefinitionParser parserToTest = new BlueprintAutoPageMounterDefinitionParser();

        Class<?> beanClass = parserToTest.getRuntimeClass();

        assertThat(beanClass, typeCompatibleWith(BundleScanningMountPointProviderDecorator.class));
    }

    @Test
    public void testParse() throws Exception {
        BlueprintParserTestUtil parserTestUtil =
            new BlueprintParserTestUtil("wicket:autoPageMounter", new BlueprintAutoPageMounterDefinitionParser());

        parserTestUtil.verifyId("autoPageMounter");
        parserTestUtil.verifyPropertyValue("applicationName");
    }

}
