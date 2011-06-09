package org.ops4j.pax.wicket.api;

import static org.junit.Assert.assertSame;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

public class InjectorHolderTest {

    @Test(expected = IllegalStateException.class)
    public void testRetrieveNonExistingInjectionHolder_shouldThrowIllegalStateException() throws Exception {
        new WicketTester();
        InjectorHolder.getInjector();
    }

    @Test
    public void testRetrieveInjectorAfterSettingIt_shouldSucceed() throws Exception {
        PaxWicketInjector paxWicketInjector = new PaxWicketInjector() {
            public void inject(Object toInject) {
                // not required for this test
            }
        };
        InjectorHolder.setInjector("applicationKey", paxWicketInjector);
        new WicketTester(new WebApplication() {
            @Override
            protected void init() {
                super.init();
                setApplicationKey("applicationKey");
            }

            @Override
            public Class<? extends Page> getHomePage() {
                // it doesn't mind
                return null;
            }
        });
        assertSame(paxWicketInjector, InjectorHolder.getInjector());
    }

}
