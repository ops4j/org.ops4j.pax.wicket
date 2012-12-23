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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.aries.blueprint.ParserContext;
import org.apache.aries.blueprint.mutable.MutableBeanMetadata;
import org.apache.aries.blueprint.mutable.MutableCollectionMetadata;
import org.apache.aries.blueprint.mutable.MutableMapMetadata;
import org.apache.aries.blueprint.mutable.MutableRefMetadata;
import org.apache.aries.blueprint.mutable.MutableValueMetadata;
import org.ops4j.pax.wicket.spi.blueprint.injection.blueprint.AbstractBlueprintBeanDefinitionParser;
import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.w3c.dom.Element;

public class BlueprintParserTestUtil {

    private MutableBeanMetadata mutableBeanMetadataMock;
    private MutableRefMetadata mutableRefMetadataMock;
    private MutableValueMetadata mutableValueMetadataMock;
    private MutableMapMetadata mutableMapMetadataMock;
    private MutableCollectionMetadata mutableCollectionMetadataMock;
    private final AbstractBlueprintBeanDefinitionParser parserToTest;

    public BlueprintParserTestUtil(String element, AbstractBlueprintBeanDefinitionParser parserToTest)
        throws Exception {
        this.parserToTest = parserToTest;
        Element blueprintElement = BlueprintTestUtil.loadFirstElementThatMatches(element);
        ParserContext parserContextMock = mock(ParserContext.class);
        mutableBeanMetadataMock = mock(MutableBeanMetadata.class);
        mutableRefMetadataMock = mock(MutableRefMetadata.class);
        when(parserContextMock.createMetadata(MutableBeanMetadata.class)).thenReturn(mutableBeanMetadataMock);
        mutableValueMetadataMock = mock(MutableValueMetadata.class);
        when(parserContextMock.createMetadata(MutableRefMetadata.class)).thenReturn(mutableRefMetadataMock);
        mutableMapMetadataMock = mock(MutableMapMetadata.class);
        when(parserContextMock.createMetadata(MutableValueMetadata.class)).thenReturn(mutableValueMetadataMock);
        mutableCollectionMetadataMock = mock(MutableCollectionMetadata.class);
        when(parserContextMock.createMetadata(MutableCollectionMetadata.class)).thenReturn(
            mutableCollectionMetadataMock);
        when(parserContextMock.createMetadata(MutableMapMetadata.class)).thenReturn(mutableMapMetadataMock);

        parserToTest.parse(blueprintElement, parserContextMock);

        verifyBaseFunctionality();
    }

    private void verifyBaseFunctionality() {
        verify(mutableBeanMetadataMock).setRuntimeClass(parserToTest.getRuntimeClass());
        verify(mutableBeanMetadataMock).setActivation(BeanMetadata.ACTIVATION_EAGER);
        verify(mutableBeanMetadataMock).setScope(BeanMetadata.SCOPE_SINGLETON);
        verify(mutableBeanMetadataMock).setInitMethod("start");
        verify(mutableBeanMetadataMock).setDestroyMethod("stop");
        verify(mutableRefMetadataMock).setComponentId("blueprintBundleContext");
        verify(mutableBeanMetadataMock).addProperty("bundleContext", mutableRefMetadataMock);
    }

    public void verifyId(String id) {
        verify(mutableBeanMetadataMock).setId(id);
    }

    public void verifyPropertyValue(String equalNameAndObject) {
        verify(mutableValueMetadataMock).setStringValue(equalNameAndObject);
        verify(mutableBeanMetadataMock).addProperty(equalNameAndObject, mutableValueMetadataMock);
    }

    public void verifyPropertyReference(String equalNameAndObject) {
        verify(mutableRefMetadataMock).setComponentId(equalNameAndObject);
        verify(mutableBeanMetadataMock).addProperty(equalNameAndObject, mutableRefMetadataMock);
    }

    public void verifyPropertyValue(String name, String value) {
        verify(mutableValueMetadataMock).setStringValue(name);
        verify(mutableBeanMetadataMock).addProperty(name, mutableValueMetadataMock);
    }

    public void verifyMapValue(String field, String name, String value, String name2, String value2) {
        verify(mutableValueMetadataMock).setStringValue(name);
        verify(mutableValueMetadataMock).setStringValue(value);
        verify(mutableValueMetadataMock).setStringValue(name2);
        verify(mutableValueMetadataMock).setStringValue(value2);
        verify(mutableMapMetadataMock, times(2)).addEntry(mutableValueMetadataMock, mutableValueMetadataMock);
        verify(mutableBeanMetadataMock).addProperty(field, mutableMapMetadataMock);
    }

    public void verifyListValue(String field, String... values) {
        for (String value : values) {
            verify(mutableValueMetadataMock).setStringValue(value);
        }
        verify(mutableMapMetadataMock, times(values.length)).addEntry(mutableValueMetadataMock,
            mutableValueMetadataMock);
        verify(mutableBeanMetadataMock).addProperty(field, mutableCollectionMetadataMock);
    }

}
