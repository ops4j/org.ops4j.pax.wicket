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
package org.ops4j.pax.wicket.internal.serialization.development;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import org.apache.wicket.Application;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.application.IClassResolver;
import org.apache.wicket.settings.IApplicationSettings;
import static org.ops4j.lang.NullArgumentException.validateNotNull;

/**
 * @author edward.yakop@gmail.com
 */
public final class DevModeObjectInputStream extends ObjectInputStream
{

    private final IClassResolver m_classResolver;

    public DevModeObjectInputStream( ObjectInputStream ois )
        throws IOException
    {
        this( ois, getClassResolver() );
    }

    public DevModeObjectInputStream( InputStream ois, IClassResolver resolver )
        throws IOException, IllegalArgumentException
    {
        super( ois );
        validateNotNull( resolver, "resolver" );
        m_classResolver = resolver;

        enableResolveObject( true );
    }

    @Override
    protected final Object resolveObject( Object obj )
        throws IOException
    {
        if( obj instanceof DevReplaceObject )
        {
            DevReplaceObject replaceObject = (DevReplaceObject) obj;
            String actualObjectClassName = replaceObject.getClassName();

            byte[] actualObjectArray = replaceObject.getObjectByteArray();
            try
            {
                ByteArrayInputStream actualBAIS = new ByteArrayInputStream( actualObjectArray );
                DevModeObjectInputStream actualOIS = new DevModeObjectInputStream( actualBAIS, m_classResolver );
                obj = actualOIS.readObject();
            }
            catch( IOException e )
            {
                throw new IOException( "Fail to deserialize [" + actualObjectClassName + "]" );
            }
            catch( ClassNotFoundException e )
            {
                String message = e.getMessage();
                throw new IOException(
                    "Fail to deserialize [" + actualObjectClassName + "] with CNFE message [" + message + "]"
                );
            }
        }

        return super.resolveObject( obj );
    }

    @Override
    protected final Class resolveClass( ObjectStreamClass objectStreamClass )
        throws IOException, ClassNotFoundException
    {
        String className = objectStreamClass.getName();

        Class candidate = resolveClassByClassResolver( className );
        if( candidate != null )
        {
            return candidate;
        }

        return super.resolveClass( objectStreamClass );
    }

    private Class resolveClassByClassResolver( String className )
    {
        Class resolvedClass = null;

        try
        {
            resolvedClass = m_classResolver.resolveClass( className );
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

    private static IClassResolver getClassResolver()
    {
        Application application = Application.get();
        IApplicationSettings applicationSettings = application.getApplicationSettings();
        return applicationSettings.getClassResolver();
    }

}
