/**
 * Copyright OPS4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.spi.blueprint;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.ops4j.pax.wicket.spi.blueprint.injection.blueprint.BlueprintAutoPageMounterDefinitionParser;
import org.ops4j.pax.wicket.spi.support.BundleScanningMountPointProviderDecorator;

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
