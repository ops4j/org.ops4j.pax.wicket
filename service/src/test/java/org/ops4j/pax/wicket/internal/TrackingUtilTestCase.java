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
package org.ops4j.pax.wicket.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.ops4j.pax.wicket.internal.TrackingUtil.createAllPageFactoryFilter;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

@RunWith(JMock.class)
public final class TrackingUtilTestCase {
    private Mockery mockery;

    @Before
    public final void setup() {
        mockery = new Mockery();
    }

    @Test
    public final void testCreateAllPageFactoryFilter() throws InvalidSyntaxException {
        BundleContext context = mockery.mock(BundleContext.class);
        Object[][] arguments =
            {
                { null, null },
                { null, "appName" },
                { context, null }
            };

        String msg = "Invoking with [null] argument must fail.";
        for (Object[] argument : arguments) {
            BundleContext ctx = (BundleContext) argument[0];
            String appName = (String) argument[1];

            try {
                createAllPageFactoryFilter(ctx, appName);
                fail(msg);
            } catch (IllegalArgumentException e) {
                // expected
            } catch (Throwable e) {
                fail(msg);
            }
        }

        Expectations exp1 = new Expectations();
        exp1.one(context).createFilter(
            "(&(pax.wicket.applicationname=appName)(objectClass=org.ops4j.pax.wicket.api.PageFactory))"
            );
        Filter expFilter = mockery.mock(Filter.class);
        exp1.will(Expectations.returnValue(expFilter));

        mockery.checking(exp1);
        Filter filter = createAllPageFactoryFilter(context, "appName");
        assertEquals(expFilter, filter);

        Expectations exp2 = new Expectations();
        exp2.one(context).createFilter(exp2.with(Expectations.any(String.class)));
        exp2.will(Expectations.throwException(new InvalidSyntaxException("msg", "filter")));

        mockery.checking(exp2);

        try {
            createAllPageFactoryFilter(context, "appName");
            fail("Must throw [IllegalArgumentException].");
        } catch (IllegalArgumentException e) {
            // Expected
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Must throw [IllegalArgumentException].");
        }
    }
}
