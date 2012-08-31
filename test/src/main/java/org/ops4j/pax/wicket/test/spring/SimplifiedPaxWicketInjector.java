package org.ops4j.pax.wicket.test.spring;

import org.apache.wicket.util.tester.WicketTester;

/**
 * An injector using the ApplicationContextMock and the PaxWicketSpringBeanComponentInjector in a quite simplified
 * way making working based on them easier.
 */
public class SimplifiedPaxWicketInjector {
    private final ApplicationContextMock applicationContext = new ApplicationContextMock();

    public static SimplifiedPaxWicketInjector registerBeanInjector(WicketTester tester) {
        return new SimplifiedPaxWicketInjector(tester);
    }

    private SimplifiedPaxWicketInjector(WicketTester tester) {
        tester.getApplication().getComponentInstantiationListeners().add(
                new PaxWicketSpringBeanComponentInjector(tester.getApplication(), applicationContext));
    }

    public void registerBean(String name, Object bean) {
        applicationContext.putBean(name, bean);
    }
}
