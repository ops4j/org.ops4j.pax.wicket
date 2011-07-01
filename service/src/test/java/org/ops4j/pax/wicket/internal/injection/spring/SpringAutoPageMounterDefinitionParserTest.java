package org.ops4j.pax.wicket.internal.injection.spring;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.ops4j.pax.wicket.internal.injection.BundleScanningMountPointProviderDecorator;

public class SpringAutoPageMounterDefinitionParserTest {
    @Test
    public void testRequestBeanType_shouldReturnDefaultContentSourceFactory() throws Exception {
        SpringAutoPageMounterDefinitionParser parserToTest = new SpringAutoPageMounterDefinitionParser();

        Class<?> beanClass = parserToTest.getBeanClass(null);

        assertThat(beanClass, typeCompatibleWith(BundleScanningMountPointProviderDecorator.class));
    }

    @Test
    public void testParse() throws Exception {
        SpringParserTestUtil parserTestUtil =
            new SpringParserTestUtil("wicket:autoPageMounter", new SpringAutoPageMounterDefinitionParser());

        parserTestUtil.verifyPropertyValue("applicationName");
    }
}
