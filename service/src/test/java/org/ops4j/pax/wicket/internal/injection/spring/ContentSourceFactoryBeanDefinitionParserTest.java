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
package org.ops4j.pax.wicket.internal.injection.spring;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.ops4j.pax.wicket.internal.injection.ContentSourceFactoryDecorator;

public class ContentSourceFactoryBeanDefinitionParserTest {

    @Test
    public void testRequestBeanType_shouldReturnDefaultContentSourceFactory() throws Exception {
        ContentSourceFactoryBeanDefinitionParser parserToTest = new ContentSourceFactoryBeanDefinitionParser();

        Class<?> beanClass = parserToTest.getBeanClass(null);

        assertThat(beanClass, typeCompatibleWith(ContentSourceFactoryDecorator.class));
    }

    @Test
    public void testParse() throws Exception {
        SpringParserTestUtil parserTestUtil =
            new SpringParserTestUtil("wicket:contentSource", new ContentSourceFactoryBeanDefinitionParser());

        parserTestUtil.verifyPropertyValue("applicationName");
        parserTestUtil.verifyMapValue("overwrites", "old2", "new2");
        parserTestUtil.verifyPropertyValue("contentSourceId", "someId");
        parserTestUtil.verifyPropertyValue("contentSourceClass", "this.is.some.class.Yes");
        parserTestUtil.verifyListValue("destinations", "infrastructure.mainmenu", "infrastructure.mainmenu2");
    }
}
