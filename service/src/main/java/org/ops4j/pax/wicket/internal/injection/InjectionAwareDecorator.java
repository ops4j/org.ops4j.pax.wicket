package org.ops4j.pax.wicket.internal.injection;

import org.osgi.framework.BundleContext;
import org.w3c.dom.Element;

public interface InjectionAwareDecorator {

    void setBundleContext(BundleContext bundleContext);

    void setContent(Element element);

    void start() throws Exception;

    void stop() throws Exception;

}
