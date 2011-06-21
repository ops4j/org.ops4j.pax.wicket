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
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.ops4j.pax.wicket.internal.injection.ApplicationDecorator;
import org.ops4j.pax.wicket.internal.injection.ParserTestUtil;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

public class ApplicationBeanDefinitionParserTest {

    @Test
    public void testRequestBeanType_shouldReturnDefaultContentSourceFactory() throws Exception {
        ApplicationBeanDefinitionParser parserToTest = new ApplicationBeanDefinitionParser();

        Class<?> beanClass = parserToTest.getBeanClass(null);

        assertThat(beanClass, typeCompatibleWith(ApplicationDecorator.class));
    }

    @Test
    public void testParse() throws Exception {
        Element springElement = SpringTestUtil.loadFirstElementThatMatches("wicket:application");
        BeanDefinitionBuilder beanDefinitionBuilderMock = mock(BeanDefinitionBuilder.class);
        ApplicationBeanDefinitionParser parserToTest = new ApplicationBeanDefinitionParser();

        parserToTest.doParse(springElement, beanDefinitionBuilderMock);

        ParserTestUtil parserTestUtil = new ParserTestUtil(beanDefinitionBuilderMock);
        parserTestUtil.verifyDefaultParserBeanBehaviour();
        parserTestUtil.verifyPropertyValue("homepageClass");
        parserTestUtil.verifyPropertyValue("mountPoint");
        parserTestUtil.verifyPropertyValue("applicationName");
        parserTestUtil.verifyPropertyReference("applicationFactory");
        parserTestUtil.verifyMapValue("contextParams", "name2", "value2");
    }
}
