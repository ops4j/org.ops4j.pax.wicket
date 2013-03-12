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
package org.ops4j.pax.wicket.spi.springdm.injection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.wicket.Component;
import org.junit.Before;
import org.junit.Test;
import org.ops4j.pax.wicket.spi.support.AbstractPaxWicketInjector;

public class AbstractPaxWicketInjectorTest {

    private static class PaxWicketTestInjector extends AbstractPaxWicketInjector {
        public void inject(Object toInject, Class<?> toHandle) {
        }

        @Override
        protected List<Field> getFields(Class<?> clazz) {
            return super.getFields(clazz);
        }

        @Override
        protected void setField(Object component, Field field, Object proxy) {
            super.setField(component, field, proxy);
        }

        @Override
        protected Class<?> getBeanType(Field field) {
            return super.getBeanType(field);
        }

        @Override
        protected Set<String> countComponentContainPaxWicketBeanAnnotatedFieldsHierachical(Class<?> component) {
            return super.countComponentContainPaxWicketBeanAnnotatedFieldsHierachical(component);
        }
    }

    @SuppressWarnings("serial")
    private static class TestComponent extends Component {
        @SuppressWarnings("unused")
        @Inject
        private TestService testServiceA;

        public TestComponent(String id) {
            super(id);
        }

        @Override
        protected void onRender() {
        }
    }

    @SuppressWarnings("serial")
    private static class TestComponentExtender extends TestComponent {
        @SuppressWarnings("unused")
        @Inject
        private TestService testServiceB;

        @SuppressWarnings("unused")
        private Object noBean;

        public TestComponentExtender() {
            super("foo");
        }
    }

    @SuppressWarnings("serial")
    private static class TestComponentBase extends TestComponent {
        public TestComponentBase() {
            super("foo");
        }
    }

    private static class TestObject {
        @Inject
        private TestService testService;

        public TestService getTestService() {
            return testService;
        }
    }

    private static class TestService {
    }

    private PaxWicketTestInjector injector;

    @Before
    public void setUp() {
        injector = new PaxWicketTestInjector();
    }

    @Test
    public void testgetFields_shouldReturnBothServiceFields() {
        List<Field> fields = injector.getFields(TestComponentExtender.class);

        assertThat(fields.size(), is(2));
        assertThat(fields.get(0).getName(), is("testServiceB"));
        assertThat(fields.get(1).getName(), is("testServiceA"));
    }

    @Test
    public void testsetField_shouldSetFieldValue() {
        TestObject obj = new TestObject();
        List<Field> fields = injector.getFields(TestObject.class);
        TestService service = mock(TestService.class);

        injector.setField(obj, fields.get(0), service);

        assertThat(obj.getTestService(), sameInstance(service));
    }

    @Test
    public void testgetBeanType_shouldReturnType() {
        List<Field> fields = injector.getFields(TestObject.class);

        assertThat(injector.getBeanType(fields.get(0)).getName(), is(TestService.class.getName()));
    }

    @Test
    public void testdoesComponentContainPaxWicketBeanAnnotatedFields_shouldReturnTrue() {
        assertThat(injector.countComponentContainPaxWicketBeanAnnotatedFieldsHierachical(TestComponentBase.class)
            .size(),
            is(1));
    }
}
