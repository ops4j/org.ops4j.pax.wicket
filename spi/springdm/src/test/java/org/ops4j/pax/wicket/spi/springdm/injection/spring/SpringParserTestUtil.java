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
package org.ops4j.pax.wicket.spi.springdm.injection.spring;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionContaining.hasItems;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.ops4j.pax.wicket.spi.springdm.injection.spring.AbstractSpringBeanDefinitionParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

public class SpringParserTestUtil {

    private BeanDefinitionBuilder beanDefinitionBuilderMock;

    public SpringParserTestUtil(String element, AbstractSpringBeanDefinitionParser parserImplementation)
        throws Exception {
        Element springElement = SpringTestUtil.loadFirstElementThatMatches(element);
        beanDefinitionBuilderMock = mock(BeanDefinitionBuilder.class);
        parserImplementation.doParse(springElement, beanDefinitionBuilderMock);
        verifyDefaultParserBeanBehaviour();
    }

    private void verifyDefaultParserBeanBehaviour() {
        verify(beanDefinitionBuilderMock).addPropertyReference("bundleContext", "bundleContext");
        verify(beanDefinitionBuilderMock).setInitMethodName("start");
        verify(beanDefinitionBuilderMock).setDestroyMethodName("stop");
        verify(beanDefinitionBuilderMock).setLazyInit(false);
    }

    public void verifyPropertyValue(String equalNameAndObject) {
        verify(beanDefinitionBuilderMock).addPropertyValue(equalNameAndObject, equalNameAndObject);
    }

    public void verifyPropertyReference(String equalNameAndObject) {
        verify(beanDefinitionBuilderMock).addPropertyReference(equalNameAndObject, equalNameAndObject);
    }

    public void verifyPropertyValue(String name, String value) {
        verify(beanDefinitionBuilderMock).addPropertyValue(name, value);
    }

    public void verifyMapValue(String field, String name, String value) {
        verify(beanDefinitionBuilderMock).addPropertyValue(argThat(is(field)), argThat(hasEntry(name, value)));
    }

    public void verifyListValue(String field, String... values) {
        verify(beanDefinitionBuilderMock).addPropertyValue(argThat(is(field)), argThat(hasItems(values)));
    }

}
