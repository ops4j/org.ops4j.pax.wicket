package org.ops4j.pax.wicket.internal.spring.contentSource;

import java.util.List;
import java.util.Map;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.Model;
import org.ops4j.pax.wicket.api.ComponentContentSource;
import org.ops4j.pax.wicket.api.TabContentSource;
import org.ops4j.pax.wicket.internal.spring.util.ComponentInstantiationListener;
import org.ops4j.pax.wicket.util.AbstractContentSource;
import org.osgi.framework.BundleContext;

public class ContentSourceFactory extends AbstractContentSource implements TabContentSource<ITab>,
        ComponentContentSource<Component> {

    private Class<?> contentSourceClass;
    private Map<String, String> overwrite;
    private List<String> destinations;
    private BundleContext bundleContext;

    public ContentSourceFactory(BundleContext bundleContext, String wicketId, String applicationName)
        throws IllegalArgumentException {
        this(bundleContext, wicketId, applicationName, null);
        this.bundleContext = bundleContext;
    }

    public ContentSourceFactory(BundleContext bundleContext, String wicketId, String applicationName,
            Class<?> contentSourceClass) throws IllegalArgumentException {
        super(bundleContext, wicketId, applicationName);
        this.contentSourceClass = contentSourceClass;
        this.bundleContext = bundleContext;
    }

    public void start() {
        if (destinations != null && destinations.size() != 0) {
            super.setDestination(destinations.toArray(new String[0]));
        }
        super.register();
    }

    public void stop() {
        super.dispose();
    }

    public void setOverwrite(Map<String, String> overwrite) {
        this.overwrite = overwrite;
    }

    public void setDestinations(List<String> destinations) {
        this.destinations = destinations;
    }

    public Component createSourceComponent(String wicketId) {
        ComponentInstantiationListener componentInstantiationListener =
            new ComponentInstantiationListener(overwrite, contentSourceClass, bundleContext);
        try {
            Application.get().addComponentInstantiationListener(componentInstantiationListener);
            return (Component) contentSourceClass.getConstructor(String.class).newInstance(wicketId);
        } catch (Exception e) {
            throw new RuntimeException("bumm", e);
        } finally {
            Application.get().removeComponentInstantiationListener(componentInstantiationListener);
        }
    }

    public ITab createSourceTab() {
        ComponentInstantiationListener componentInstantiationListener =
            new ComponentInstantiationListener(overwrite, contentSourceClass, bundleContext);
        try {
            ITab tab = (ITab) contentSourceClass.newInstance();
            componentInstantiationListener.internalInstanciation(tab);
            return tab;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("bumm");
        }
    }

    public ITab createSourceTab(String title) {
        ComponentInstantiationListener componentInstantiationListener =
            new ComponentInstantiationListener(overwrite, contentSourceClass, bundleContext);
        try {
            ITab tab = (ITab) contentSourceClass.getConstructor(Model.class).newInstance(new Model<String>(title));
            componentInstantiationListener.internalInstanciation(tab);
            return tab;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("bumm");
        }
    }

}
