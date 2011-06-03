/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2010 David Leangen.
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
package org.ops4j.pax.wicket.internal;

import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.wicket.application.IClassResolver;
import org.ops4j.pax.wicket.api.ContentAggregator;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.api.PageFactory;
import org.ops4j.pax.wicket.api.PaxWicketApplicationFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class BundleDelegatingClassResolver extends ServiceTracker
        implements IClassResolver {

    private static final String FILTER;

    static {
        FILTER = "(|" +
                 "(objectClass=" + ContentSource.class.getName() + ")" +
                 "(objectClass=" + ContentAggregator.class.getName() + ")" +
                 "(objectClass=" + PageFactory.class.getName() + ")" +
                 "(objectClass=" + PaxWicketApplicationFactory.class.getName() + ")" +
                 ")";
    }

    private HashSet<Bundle> m_bundles;
    private final String m_applicationName;
    private final Bundle m_paxWicketbundle;

    public BundleDelegatingClassResolver(BundleContext context, String applicationName, Bundle paxWicketBundle) {
        super(context, createFilter(context), null);

        m_applicationName = applicationName;
        m_paxWicketbundle = paxWicketBundle;
        m_bundles = new HashSet<Bundle>();
        m_bundles.add(paxWicketBundle);

        open(true);
    }

    public Class<?> resolveClass(String classname)
        throws ClassNotFoundException {
        for (Bundle bundle : m_bundles) {
            try {
                return bundle.loadClass(classname);
            } catch (ClassNotFoundException e) {
                // ignore, expected in many cases.
            } catch (IllegalStateException e) {
                // if the bundle has been uninstalled.
            }
        }

        throw new ClassNotFoundException("Class [" + classname + "] can't be resolved.");
    }

    /**
     * Untested!! Currently, tests are broken in pax-wicket.
     */
    @SuppressWarnings("unchecked")
    public Iterator<URL> getResources(String name) {
        try {
            for (Bundle bundle : m_bundles) {
                final Enumeration<URL> enumeration = bundle.getResources(name);
                if (null != enumeration && enumeration.hasMoreElements()) {
                    return new EnumerationAdapter<URL>(enumeration);
                }
                ;
            }
        } catch (IOException e) {
            return Collections.<URL> emptyList().iterator();
        }

        return Collections.<URL> emptyList().iterator();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object addingService(ServiceReference serviceReference) {
        String appName = (String) serviceReference.getProperty(APPLICATION_NAME);
        if (!m_applicationName.equals(appName)) {
            return null;
        }
        synchronized (this) {
            Bundle bundle = serviceReference.getBundle();
            HashSet<Bundle> clone = (HashSet<Bundle>) m_bundles.clone();
            clone.add(bundle);
            m_bundles = clone;
        }
        return super.addingService(serviceReference);
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {
        String appName = (String) serviceReference.getProperty(APPLICATION_NAME);
        if (!m_applicationName.equals(appName)) {
            return;
        }
        HashSet<Bundle> revisedSet = new HashSet<Bundle>();
        revisedSet.add(m_paxWicketbundle);
        try {
            ServiceReference[] serviceReferences = context.getAllServiceReferences(null, FILTER);
            for (ServiceReference ref : serviceReferences) {
                revisedSet.add(ref.getBundle());
            }
            m_bundles = revisedSet;
        } catch (InvalidSyntaxException e) {
            // Can not happen.
        }
        super.removedService(serviceReference, o);
    }

    private static Filter createFilter(BundleContext context) {
        try {
            return context.createFilter(FILTER);
        } catch (InvalidSyntaxException e) {
            // Should not happened
            e.printStackTrace();
        }

        return null;
    }
}
