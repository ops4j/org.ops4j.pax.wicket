package org.ops4j.pax.wicket.test;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.ops4j.pax.wicket.util.proxy.PaxWicketBean;

/**
 * Very simple example page to validate if injection for spring and blueprint works as expected.
 */
public class ExamplePage extends WebPage {

    @PaxWicketBean
    private TestInjectionBean test;

    public ExamplePage() {
        super();
        add(new Label("test", test.getContent()));
    }

    public static class TestInjectionBean {
        private String content;

        public TestInjectionBean() {
        }

        public TestInjectionBean(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

    }

}
