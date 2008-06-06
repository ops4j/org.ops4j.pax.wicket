/*  Copyright 2008 Edward Yakop.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.internal.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import org.apache.wicket.Application;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.application.IClassResolver;
import org.apache.wicket.settings.IApplicationSettings;

/**
 * @author edward.yakop@gmail.com
 * @since 0.5.4
 */
final class PaxWicketObjectInputStream extends ObjectInputStream
{

    private final IClassResolver classResolver;

    public PaxWicketObjectInputStream( InputStream anInputStream )
        throws IOException
    {
        super( anInputStream );

        // Can the application always be taken??
        Application application = Application.get();
        IApplicationSettings applicationSettings = application.getApplicationSettings();
        classResolver = applicationSettings.getClassResolver();
    }

    @Override
    protected final Object resolveObject( Object anObject )
        throws IOException
    {
        if( anObject instanceof ReplaceBundleContext )
        {
            ReplaceBundleContext replaceBundleContext = (ReplaceBundleContext) anObject;
            return replaceBundleContext.getBundleContext();
        }
        else if( anObject instanceof ReplaceBundle )
        {
            ReplaceBundle replaceBundle = (ReplaceBundle) anObject;
            return replaceBundle.getBundle();
        }
        else
        {
            return super.resolveObject( anObject );
        }
    }

    @Override
    protected final Class resolveClass( ObjectStreamClass anObjectStreamClass )
        throws IOException, ClassNotFoundException
    {
        String className = anObjectStreamClass.getName();

        Class candidate = resolveClassByClassResolver( className );
        if( candidate != null )
        {
            return candidate;
        }

        return super.resolveClass( anObjectStreamClass );
    }

    private Class resolveClassByClassResolver( String aClassName )
    {
        Class resolvedClass = null;

        try
        {
            resolvedClass = classResolver.resolveClass( aClassName );
        }
        catch( WicketRuntimeException ex )
        {
            // Ignore
        }
        catch( ClassNotFoundException e )
        {
            // Ignore
        }

        return resolvedClass;
    }
}
