package org.ops4j.pax.wicket.internal.spring.contentSourceModelMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.ops4j.pax.wicket.api.AggregationPointDescriptor;
import org.ops4j.pax.wicket.api.ContentSourceDescriptor;
import org.ops4j.pax.wicket.api.ContentSourceFactory;
import org.ops4j.pax.wicket.api.ContentSourceModelMapping;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class ContentSourceModelMappingFactory extends ServiceTracker {

    private final BundleContext bundleContext;
    private final ContentSourceFactory factory;
    private final String applicationName;
    private final Map<ServiceReference, List<SpringContextAwareContentModel>> models =
        new HashMap<ServiceReference, List<SpringContextAwareContentModel>>();
    private final Map<ServiceReference, List<SpringContextAwareContentAggregator>> aggregator =
        new HashMap<ServiceReference, List<SpringContextAwareContentAggregator>>();
    private final Map<ServiceReference, List<SpringContextAwareContentSources>> sources =
        new HashMap<ServiceReference, List<SpringContextAwareContentSources>>();
    private final Map<ServiceReference, ConfigurableApplicationContext> applicationContexts =
        new HashMap<ServiceReference, ConfigurableApplicationContext>();
    private final Map<ServiceReference, ServiceRegistration> applicationContextsServiceReferences =
        new HashMap<ServiceReference, ServiceRegistration>();

    private boolean allowMultibleRegistrations = false;

    public ContentSourceModelMappingFactory(BundleContext bundleContext, ContentSourceFactory<?> factory,
            String serviceClass, String applicationName) {
        super(bundleContext, serviceClass, null);
        this.bundleContext = bundleContext;
        this.factory = factory;
        this.applicationName = applicationName;
    }

    public void start() {
        super.open();
    }

    public void stop() {
        super.close();
    }

    @Override
    public Object addingService(ServiceReference reference) {
        Object newService = super.addingService(reference);
        if (!allowMultibleRegistrations && applicationContexts.size() == 1) {
            return newService;
        }
        provideServices(reference, newService);
        return newService;
    }

    @Override
    public void modifiedService(ServiceReference reference, Object service) {
        super.modifiedService(reference, service);
        if (!allowMultibleRegistrations && !applicationContexts.containsKey(reference)) {
            return;
        }
        cleanup(reference);
        provideServices(reference, service);
    }

    private void provideServices(ServiceReference reference, Object newService) {
        ContentSourceModelMapping mapping = factory.createContentSourceMappings(newService);
        allowMultibleRegistrations = mapping.allowMultibleRegistrations();
        createAndRegisterApplicationContext(reference);
        registerModels(reference, mapping.getModelObjects());
        registerAggregationPoints(reference, mapping.getAggregationPoints());
        registerContentSources(reference, mapping.getContenSources());
    }

    private void createAndRegisterApplicationContext(ServiceReference reference) {
        ConfigurableApplicationContext appContext = new GenericApplicationContext();
        String[] clazzes = new String[]{
            ApplicationContext.class.getName(),
            ConfigurableApplicationContext.class.getName()
        };
        Properties properties = new Properties();
        properties.put(Constants.BUNDLE_SYMBOLICNAME, bundleContext.getBundle().getSymbolicName());
        ServiceRegistration registration = bundleContext.registerService(clazzes, appContext, properties);
        applicationContexts.put(reference, appContext);
        applicationContextsServiceReferences.put(reference, registration);
    }

    private void registerModels(ServiceReference reference,
            Map<String, Object> modelDescriptors) {
        List<SpringContextAwareContentModel> localModels =
            new ArrayList<SpringContextAwareContentModel>();
        for (Entry<String, Object> modelDescriptor : modelDescriptors.entrySet()) {
            SpringContextAwareContentModel model =
                new SpringContextAwareContentModel(modelDescriptor.getKey(), modelDescriptor.getValue(), bundleContext,
                    applicationContexts.get(reference));
            model.start();
            localModels.add(model);
        }
        models.put(reference, localModels);
    }

    private void registerAggregationPoints(ServiceReference reference,
            List<AggregationPointDescriptor> aggregationPoints) {
        List<SpringContextAwareContentAggregator> localAggregator =
            new ArrayList<SpringContextAwareContentAggregator>();
        for (AggregationPointDescriptor aggregationPointDescriptor : aggregationPoints) {
            SpringContextAwareContentAggregator contentAggregator =
                new SpringContextAwareContentAggregator(bundleContext, applicationName,
                    aggregationPointDescriptor.getAggregationPointName(),
                    aggregationPointDescriptor.getAggregationPointId(),
                    applicationContexts.get(reference));
            contentAggregator.start();
            localAggregator.add(contentAggregator);
        }
        aggregator.put(reference, localAggregator);
    }

    private void registerContentSources(ServiceReference reference,
            List<ContentSourceDescriptor> contentSourceDescriptors) {
        List<SpringContextAwareContentSources> localSources = new ArrayList<SpringContextAwareContentSources>();
        for (ContentSourceDescriptor countentSourceDescriptor : contentSourceDescriptors) {
            SpringContextAwareContentSources contentSource =
                new SpringContextAwareContentSources(bundleContext, countentSourceDescriptor, applicationName,
                    applicationContexts.get(reference));
            contentSource.start();
            localSources.add(contentSource);
        }
        sources.put(reference, localSources);
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        cleanup(reference);
        super.removedService(reference, service);
        ServiceReference newReference = getServiceReference();
        if (!allowMultibleRegistrations && newReference != null) {
            // if this is true the service would have been already registered
            provideServices(newReference, getService(newReference));
        }
    }

    private void cleanup(ServiceReference reference) {
        if (!applicationContexts.containsKey(reference)) {
            // in this case more than one service where registered
            return;
        }
        removeApplicationContext(reference);
        removeAggregators(reference);
        removeSources(reference);
        removeModels(reference);
    }

    private void removeApplicationContext(ServiceReference reference) {
        applicationContextsServiceReferences.get(reference).unregister();
        applicationContextsServiceReferences.remove(reference);
        applicationContexts.get(reference).close();
        applicationContexts.remove(reference);
    }

    private void removeAggregators(ServiceReference reference) {
        List<SpringContextAwareContentAggregator> contentAggregators = aggregator.get(reference);
        for (SpringContextAwareContentAggregator rootContentAggregator : contentAggregators) {
            rootContentAggregator.stop();
        }
        aggregator.remove(reference);
    }

    private void removeSources(ServiceReference reference) {
        List<SpringContextAwareContentSources> sourceList = sources.get(reference);
        for (SpringContextAwareContentSources source : sourceList) {
            source.stop();
        }
        sources.remove(reference);
    }

    private void removeModels(ServiceReference reference) {
        List<SpringContextAwareContentModel> modelList = models.get(reference);
        for (SpringContextAwareContentModel model : modelList) {
            model.stop();
        }
        models.remove(reference);
    }

}
