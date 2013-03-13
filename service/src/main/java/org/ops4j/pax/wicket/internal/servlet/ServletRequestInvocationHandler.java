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
package org.ops4j.pax.wicket.internal.servlet;

import static org.ops4j.lang.NullArgumentException.validateNotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * TODO what is the goal of this class
 * 
 */
public final class ServletRequestInvocationHandler
                implements InvocationHandler {

    private final HttpServletRequest request;
    private final String mountPoint;

    public ServletRequestInvocationHandler(HttpServletRequest request, String mountPoint)
                throws IllegalArgumentException {
        validateNotNull(request, "request");
        validateNotNull(mountPoint, "mountPoint");
        if (mountPoint.length() <= 1) {
            if (mountPoint.startsWith("/")) {
                mountPoint = mountPoint.substring(1);
            }
        } else {
            if (!mountPoint.startsWith("/")) {
                mountPoint = "/" + mountPoint;
            }
        }
        this.request = request;
        this.mountPoint = mountPoint;
    }

    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
        try {
            String methodName = method.getName();
            Object returnValue;
            if (mountPoint.length() == 0) {
                if ("getContextPath".equals(methodName) ||
                            "getServletPath".equals(methodName)) {
                    returnValue = "";
                } else if ("getPathInfo".equals(methodName)) {
                    returnValue = request.getServletPath();
                } else {
                    returnValue = method.invoke(request, arguments);
                }
            } else {
                returnValue = method.invoke(request, arguments);
            }
            return returnValue;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
