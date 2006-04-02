/*
 * $Id: User.java 3622 2006-01-04 09:43:32Z ivaynberg $
 * $Revision: 3622 $
 * $Date: 2006-01-04 17:43:32 +0800 (Wed, 04 Jan 2006) $
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.samples.library.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Trivial user model for example application
 *
 * @author Jonathan Locke
 */
public final class UserImpl
    implements Serializable, User
{

    private static final long serialVersionUID = 1L;

    private String m_name;

    // The user's personal book list
    private List m_books = new ArrayList();

    public UserImpl( ServiceTracker tracker )
    {
        long id = getNextId( tracker );
        m_books.add( new BookImpl( id, "Effective Java", "Joshua Bloch", Book.NON_FICTION ) );
        id = getNextId( tracker );
        m_books.add( new BookImpl( id, "The Illiad", "Homer Simpson", Book.FICTION ) );
        id = getNextId( tracker );
        m_books.add( new BookImpl( id, "Why Stock Markets Crash", "Didier Sornette", Book.NON_FICTION ) );
        id = getNextId( tracker );
        m_books.add( new BookImpl( id, "The Netherlands", "Mike Jones", Book.NON_FICTION ) );
        id = getNextId( tracker );
        m_books.add( new BookImpl( id, "Windows, Windows, Windows!", "Steve Ballmer", Book.FICTION ) );
        id = getNextId( tracker );
        m_books.add( new BookImpl( id, "This is a test", "Vincent Rumsfield", Book.FICTION ) );
        id = getNextId( tracker );
        m_books.add( new BookImpl( id, "Movies", "Mark Marksfield", Book.NON_FICTION ) );
        id = getNextId( tracker );
        m_books.add( new BookImpl( id, "DOS Capitol", "Billy G", Book.FICTION ) );
        id = getNextId( tracker );
        m_books.add( new BookImpl( id, "Whatever", "Jonny Zoom", Book.FICTION ) );
        id = getNextId( tracker );
        m_books.add( new BookImpl( id, "Tooty Fruity", "Rudy O", Book.FICTION ) );
    }

    private long getNextId( ServiceTracker tracker )
    {
        Library lib = (Library) tracker.getService();
        if( lib == null )
        {
            return -1;
        }
        return lib.obtainNewId();
    }

    /**
     * @return User name
     */
    public final String getName()
    {
        return m_name;
    }

    /**
     * @param string User name
     */
    public final void setName( final String string )
    {
        m_name = string;
    }

    /**
     * @return User's book list
     */
    public final List getBooks()
    {
        return m_books;
    }

    /**
     * @param books New book list
     */
    public void setBooks( List books )
    {
        m_books = books;
    }
}


