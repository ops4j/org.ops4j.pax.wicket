/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2006 Edward F. Yakop.
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
package org.ops4j.pax.wicket.util;

import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.wicket.Session;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.api.PaxWicketAuthentication;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;

public abstract class AbstractContentSource implements ContentSource, ManagedService {

    private static final String[] ROLES_TYPE = new String[0];
    private static final PaxWicketAuthentication DUMMY_AUTHENTICATION = new PaxWicketAuthentication()
    {
        public String getLoggedInUser()
        {
            return null;
        }

        public Roles getRoles()
        {
            return new Roles();
        }
    };

    private BundleContext bundleContext;
    private Dictionary<String, Object> properties;
    private ServiceRegistration registration;
    private Roles requiredRoles;
    private Roles basicRoles;

    /**
     * Construct an instance with {@code AbstractContentSource}.
     * 
     * @param bundleContext The bundle context. This argument must not be {@code null}.
     * @param wicketId The WicketId. This argument must not be {@code null} or empty.
     * @param applicationName The application name. This argument must not be {@code null} or empty.
     * 
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null}.
     * @since 1.0.0
     */
    protected AbstractContentSource(BundleContext bundleContext, String wicketId, String applicationName)
        throws IllegalArgumentException {
        validateNotNull(bundleContext, "bundleContext");
        validateNotEmpty(wicketId, "wicketId");
        validateNotEmpty(applicationName, "applicationName");

        properties = new Hashtable<String, Object>();
        properties.put(Constants.SERVICE_PID, SOURCE_ID + "/" + wicketId);
        this.bundleContext = bundleContext;

        setWicketId(wicketId);
        setApplicationName(applicationName);
    }

    /**
     * Returns the destinations.
     * 
     * @return The destinations.
     * 
     * @since 1.0.0
     */
    public final String[] getDestinations() {
        return getStringArrayProperty(DESTINATIONS);
    }

    /**
     * Sets the destination id.
     * 
     * @param destinationIds The destination ids. This argument must not be {@code null}.
     * 
     * @throws IllegalArgumentException Thrown if the {@code destinationId} argument is not {@code null}.
     * @since 1.0.0
     */
    public final void setDestination(String... destinationIds)
        throws IllegalArgumentException {
        validateNotNull(destinationIds, "destinationIds");

        properties.put(DESTINATIONS, destinationIds);
    }

    // public final E createSourceComponent(String wicketId)
    // throws IllegalArgumentException {
    // boolean isRolesApproved = isRolesAuthorized();
    // if (isRolesApproved) {
    // return createWicketComponent(wicketId);
    // } else {
    // return onAuthorizationFailed(wicketId);
    // }
    // }
    //
    // public final E createSourceComponent(String wicketId, MarkupContainer parent)
    // throws IllegalArgumentException {
    // boolean isRolesApproved = isRolesAuthorized();
    // if (isRolesApproved) {
    // return createWicketComponent(wicketId, parent);
    // } else {
    // return onAuthorizationFailed(wicketId);
    // }
    // }

    // protected E onAuthorizationFailed(String wicketId) {
    // return null;
    // }

    /**
     * Returns {@code true} if the user roles is authorized to create this content source component, {@code false}
     * otherwise.
     * 
     * @return A {@code boolean} indicator whether the user roles can create this content source component.
     * 
     * @since 1.0.0
     */
    private boolean isRolesAuthorized() {
        PaxWicketAuthentication authentication = getAuthentication();
        Roles userRoles = authentication.getRoles();

        boolean isRequiredRolesAuthorized = true;
        if (requiredRoles != null) {
            isRequiredRolesAuthorized = requiredRoles.hasAllRoles(userRoles);
        }

        boolean isBasicRolesAuthorized = true;
        if (basicRoles != null && !basicRoles.isEmpty()) {
            isBasicRolesAuthorized = userRoles.hasAnyRole(basicRoles);
        }

        return isRequiredRolesAuthorized && isBasicRolesAuthorized;
    }

    private String[] getStringArrayProperty(String key)
        throws IllegalArgumentException {
        validateNotEmpty(key, "key");

        return (String[]) properties.get(key);
    }

    private String getStringProperty(String key, String defaultValue)
        throws IllegalArgumentException {
        validateNotEmpty(key, "key");

        String value = (String) properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Returns the content source id.
     * 
     * @return The content source id.
     * 
     * @since 1.0.0
     */
    public final String getSourceId() {
        return getStringProperty(SOURCE_ID, null);
    }

    /**
     * Set the WicketId.
     * 
     * @param wicketId The WicketId. This argument must not be {@code null}.
     * 
     * @throws IllegalArgumentException Thrown if the {@code wicketId} argument is {@code null}.
     * @since 1.0.0
     */
    private void setWicketId(String wicketId)
        throws IllegalArgumentException {
        validateNotEmpty(wicketId, "wicketId");
        properties.put(SOURCE_ID, wicketId);
    }

    /**
     * Returns the application name.
     * 
     * @return The application name.
     * 
     * @since 1.0.0
     */
    public final String getApplicationName() {
        return getStringProperty(APPLICATION_NAME, null);
    }

    /**
     * Returns the Authentication of the current request.
     * 
     * It is possible to obtain the Username of the logged in user as well as which roles that this user has assigned to
     * it.
     * 
     * @return the Authentication of the current request.
     */
    protected PaxWicketAuthentication getAuthentication() {
        Session session = Session.get();
        if (session instanceof PaxWicketAuthentication) {
            return (PaxWicketAuthentication) session;
        }

        return DUMMY_AUTHENTICATION;
    }

    /**
     * Sets the application name.
     * 
     * @param applicationName The application name. This argument must not be {@code null}.
     * 
     * @throws IllegalArgumentException Thrown if the {@code applicationName} argument is {@code null}.
     * @since 1.0.0
     */
    public final void setApplicationName(String applicationName)
        throws IllegalArgumentException {
        validateNotEmpty(applicationName, "applicationName");

        properties.put(APPLICATION_NAME, applicationName);
    }

    @SuppressWarnings("unchecked")
    public final void updated(Dictionary config) {
        synchronized (this) {
            if (config != null) {
                properties = config;
                String[] required = (String[]) properties.get(REQUIRED_ROLES);
                if (required != null) {
                    requiredRoles = new Roles(required);
                } else {
                    requiredRoles = new Roles();
                }
                String[] basic = (String[]) properties.get(BASIC_ROLES);
                if (basic != null) {
                    basicRoles = new Roles(basic);
                } else {
                    basicRoles = new Roles();
                }
                registration.setProperties(properties);
            }
        }
    }

    /**
     * Register the specified {@code AbstractContentSource} instance.
     * 
     * @return The specified {@code AbstractContentSource}.
     * 
     * @since 1.0.0
     */
    public void register() {
        synchronized (this) {
            String[] serviceNames =
                {
                    ContentSource.class.getName(), ManagedService.class.getName()
                };
            registration = bundleContext.registerService(serviceNames, this, properties);
        }
    }

    public void dispose() {
        registration.unregister();
    }

    public Roles getRequiredRoles() {
        return requiredRoles;
    }

    public Roles getBasicRoles() {
        return basicRoles;
    }

    public final void setRoles(Roles requiredRoles, Roles basicRoles) {
        boolean changed = false;
        if (requiredRoles != null) {
            changed = true;
            this.requiredRoles = requiredRoles;
            properties.put(REQUIRED_ROLES, requiredRoles.toArray(ROLES_TYPE));
        }

        if (basicRoles != null) {
            this.basicRoles = basicRoles;
            properties.put(REQUIRED_ROLES, basicRoles.toArray(ROLES_TYPE));
            changed = true;
        }

        if (changed && registration != null) {
            registration.setProperties(properties);
        }
    }

}
