package org.ops4j.pax.wicket.api;

import org.apache.wicket.Component;

public interface ComponentContentSource<E extends Component> extends ContentSource {
    /**
     * Create the wicket component represented by this {@code ContentSource} instance. This method must not return
     * {@code null} object.
     * <p>
     * General convention:<br/>
     * <ul>
     * <li>In the use case of Wicket 1 environment. The callee of this method responsibles to add the component created
     * this method;</li>
     * </ul>
     * </p>
     * 
     * @param wicketId The wicket id. This argument must not be {@code null}.
     * 
     * @return The wicket component represented by this {@code ContentSource} instance, or null if user has no access to
     *         this ContentSource.
     * 
     * @throws IllegalArgumentException Thrown if the {@code wicketId} argument is {@code null}.
     * @since 1.0.0
     */
    E createSourceComponent(String wicketId);
}
