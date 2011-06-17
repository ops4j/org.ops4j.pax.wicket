/*
 * Copyright OPS4J
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.pax.wicket.internal.spring.contentAggregator;

import org.ops4j.pax.wicket.util.RootContentAggregator;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class RootContentAggregatorBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    public Class<?> getBeanClass(Element element) {
        return RootContentAggregator.class;
    }

    @Override
    public void doParse(Element element, BeanDefinitionBuilder bean) {
        bean.addConstructorArgReference("bundleContext");
        setConstructorElement("applicationName", element, bean);
        setConstructorElement("aggregationPointName", element, bean);
        bean.setLazyInit(false);
        bean.setInitMethodName("register");
        bean.setDestroyMethodName("dispose");
    }

    private void setConstructorElement(String id, Element element, BeanDefinitionBuilder bean) {
        String beanElement = element.getAttribute(id);
        bean.addConstructorArgValue(beanElement);
    }

}
