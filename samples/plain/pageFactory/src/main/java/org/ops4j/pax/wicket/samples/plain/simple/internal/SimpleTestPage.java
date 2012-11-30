package org.ops4j.pax.wicket.samples.plain.simple.internal;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

public class SimpleTestPage extends WebPage {
    public SimpleTestPage(String someContent) {
        add(new Label("content", someContent));
    }
}
