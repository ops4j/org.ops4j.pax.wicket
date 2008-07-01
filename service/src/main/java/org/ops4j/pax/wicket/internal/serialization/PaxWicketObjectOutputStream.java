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

    private final ObjectOutputStream m_outputStream;

    PaxWicketObjectOutputStream( OutputStream outputStream )
        throws IOException, SecurityException, IllegalArgumentException
    {
        validateNotNull( outputStream, "outputStream" );
        m_outputStream = new ObjectOutputStream( outputStream )
        {
            {
                enableReplaceObject( true );
            }

            @Override
            protected final Object replaceObject( Object object )
                throws IOException
            {
                if( object instanceof BundleContext )
                {
                    BundleContext context = (BundleContext) object;
                    return new ReplaceBundleContext( context );
                }
                else if( object instanceof Bundle )
                {
                    Bundle bundle = (Bundle) object;
                    return new ReplaceBundle( bundle );
                }
                else
                {
                    return super.replaceObject( object );
                }
            }
        };
    }

    @Override
    protected final void writeObjectOverride( final Object object )
        throws IOException
    {
        try
        {
            m_outputStream.writeObject( object );
        }
        catch( IOException e )
        {
            if( isAvailable() )
            {
                // trigger serialization again, but this time gather some more info
                new SerializableChecker( (NotSerializableException) e ).writeObject( object );

                // if we get here, we didn't fail, while we should;
                throw e;
            }

            throw e;
        }
        catch( RuntimeException e )
        {
            LOGGER.error( "error writing object " + object + ": " + e.getMessage(), e );
            throw e;
        }
    }

    @Override
    public final void flush()
        throws IOException
    {
        m_outputStream.flush();
    }

    @Override
    public final void close()
        throws IOException
    {
        m_outputStream.close();
    }
}

