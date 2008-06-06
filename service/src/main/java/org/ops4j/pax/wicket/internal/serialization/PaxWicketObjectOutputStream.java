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
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.apache.wicket.util.io.SerializableChecker;
import static org.apache.wicket.util.io.SerializableChecker.isAvailable;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author edward.yakop@gmail.com
 * @since 0.5.4
 */
final class PaxWicketObjectOutputStream extends ObjectOutputStream
{

    private static final Logger LOGGER = LoggerFactory.getLogger( PaxWicketObjectOutputStream.class );

    private final ObjectOutputStream outputStream;

    PaxWicketObjectOutputStream( OutputStream anOutputStream )
        throws IOException, SecurityException, IllegalArgumentException
    {
        validateNotNull( anOutputStream, "anOutputStream" );
        outputStream = new ObjectOutputStream( anOutputStream )
        {
            {
                enableReplaceObject( true );
            }

            @Override
            protected final Object replaceObject( Object anObject )
                throws IOException
            {
                if( anObject instanceof BundleContext )
                {
                    BundleContext context = (BundleContext) anObject;
                    return new ReplaceBundleContext( context );
                }
                else if( anObject instanceof Bundle )
                {
                    Bundle bundle = (Bundle) anObject;
                    return new ReplaceBundle( bundle );
                }
                else
                {
                    return super.replaceObject( anObject );
                }
            }
        };
    }

    @Override
    protected final void writeObjectOverride( final Object obj )
        throws IOException
    {
        try
        {
            outputStream.writeObject( obj );
        }
        catch( IOException e )
        {
            if( isAvailable() )
            {
                // trigger serialization again, but this time gather some more info
                new SerializableChecker( (NotSerializableException) e ).writeObject( obj );

                // if we get here, we didn't fail, while we should;
                throw e;
            }

            throw e;
        }
        catch( RuntimeException e )
        {
            LOGGER.error( "error writing object " + obj + ": " + e.getMessage(), e );
            throw e;
        }
    }

    @Override
    public final void flush()
        throws IOException
    {
        outputStream.flush();
    }

    @Override
    public final void close()
        throws IOException
    {
        outputStream.close();
    }
}

