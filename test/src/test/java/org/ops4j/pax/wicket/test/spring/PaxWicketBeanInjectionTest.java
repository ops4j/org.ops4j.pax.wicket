package org.ops4j.pax.wicket.test.spring;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.ops4j.pax.wicket.test.ExamplePage;
import org.ops4j.pax.wicket.test.ExamplePage.TestInjectionBean;

public class PaxWicketBeanInjectionTest {

    @Test
    public void testStartPage_shouldShowMockingContent() throws Exception {
        ApplicationContextMock appContext = new ApplicationContextMock();
        appContext.putBean("testBean", new TestInjectionBean("testContent"));
        WicketTester tester = new WicketTester();
        tester.getApplication().addComponentInstantiationListener(
            new PaxWicketSpringBeanComponentInjector(tester.getApplication(), appContext));

        tester.startPage(ExamplePage.class);

        tester.assertLabel("test", "testContent");
    }
}
