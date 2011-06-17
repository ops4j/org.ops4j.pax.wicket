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
package org.ops4j.pax.wicket.internal.spring;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.ops4j.pax.wicket.internal.spring.contentSourceModelMapping.ContentSourceModelMappingFactory;
import org.ops4j.pax.wicket.internal.spring.contentSourceModelMapping.ContentSourceModelMappingFactoryBeanDefinitionParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

public class ContentSourceModelMappingFactoryBeanDefinitionParserTest {

    @Test
    public void testRequestBeanType_shouldReturnDefaultContentSourceFactory() throws Exception {
        ContentSourceModelMappingFactoryBeanDefinitionParser parserToTest =
            new ContentSourceModelMappingFactoryBeanDefinitionParser();

        Class<?> beanClass = parserToTest.getBeanClass(null);

        assertThat(beanClass, typeCompatibleWith(ContentSourceModelMappingFactory.class));
    }

    @Test
    public void testParse() throws Exception {
        Element springElement = SpringTestUtil.loadFirstElementThatMatches("wicket:contentSourceModelMapping");
        BeanDefinitionBuilder beanDefinitionBuilderMock = mock(BeanDefinitionBuilder.class);
        ContentSourceModelMappingFactoryBeanDefinitionParser parserToTest =
            new ContentSourceModelMappingFactoryBeanDefinitionParser();

        parserToTest.doParse(springElement, beanDefinitionBuilderMock);

        verify(beanDefinitionBuilderMock).addConstructorArgReference("bundleContext");
        verify(beanDefinitionBuilderMock).addConstructorArgReference("contentSourceFactoryImpl");
        verify(beanDefinitionBuilderMock).addConstructorArgValue("serviceClass");
        verify(beanDefinitionBuilderMock).addConstructorArgValue("applicationName");
        verify(beanDefinitionBuilderMock).setInitMethodName("start");
        verify(beanDefinitionBuilderMock).setDestroyMethodName("stop");
        verify(beanDefinitionBuilderMock).setLazyInit(false);
    }
}
