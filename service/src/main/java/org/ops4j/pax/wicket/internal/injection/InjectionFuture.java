/**
 * Copyright OPS4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.internal.injection;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.ops4j.pax.wicket.spi.FutureProxyTargetLocator;
import org.ops4j.pax.wicket.spi.ProxyTarget;
import org.ops4j.pax.wicket.spi.ProxyTargetLocator;
import org.ops4j.pax.wicket.spi.ReleasableProxyTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InjectionFuture<T> implements Future<T>, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(InjectionFuture.class);

    private static final long serialVersionUID = 4845652300720067256L;
    private final Class<T> type;
    private final ProxyTargetLocator locator;

    private InjectionFuture(Class<T> type, ProxyTargetLocator locator) {
        this.type = type;
        this.locator = locator;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return true;
    }

    public T get() {
        Object object = null;
        try {
            ProxyTarget target = locator.locateProxyTarget();
            if (target != null) {
                try {
                    object = target.getTarget();
                } finally {
                    if (target instanceof ReleasableProxyTarget) {
                        // Sadly we don't know much what is done with the target by the caller, so we just release it
                        // right now
                        ((ReleasableProxyTarget) target).releaseTarget();
                    }
                }
            }
        } catch (RuntimeException e) {
            LOG.trace("locating target failed, will return null then", e);
        }
        return type.cast(object);
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Object object = null;
        try {
            if (locator instanceof FutureProxyTargetLocator) {
                FutureProxyTargetLocator futureProxyTargetLocator = (FutureProxyTargetLocator) locator;
                ProxyTarget target = futureProxyTargetLocator.locateProxyTarget(timeout, unit);
                try {
                    object = target.getTarget();
                } finally {
                    if (target instanceof ReleasableProxyTarget) {
                        // Sadly we don't know much what is done with the target by the caller, so we just release it
                        // right now
                        ((ReleasableProxyTarget) target).releaseTarget();
                    }
                }
            } else {
                // We just try to fetch it now...
                object = get();
            }
        } catch (RuntimeException e) {
            LOG.trace("locating target failed, will return null then", e);
        }
        if (object == null) {
            throw new TimeoutException("can't locate target in given time frame");
        }
        return type.cast(object);
    }

    public static <T> InjectionFuture<T> create(Class<T> type, ProxyTargetLocator locator) {
        return new InjectionFuture<T>(type, locator);
    }

}
