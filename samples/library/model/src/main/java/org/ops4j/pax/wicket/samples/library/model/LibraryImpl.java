/*
 * Copyright 2006 Niclas Hedhman.
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
package org.ops4j.pax.wicket.samples.library.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;

public class LibraryImpl
    implements Library
{

    private long nextId = 0;

    private Map<Long, Book> m_BooksById;
    private Map<String, Book> m_BooksByTitle;
    private List m_writingStyles;

    public LibraryImpl()
    {
        m_BooksById = new HashMap<Long, Book>();
        m_BooksByTitle = new HashMap<String, Book>();
        m_writingStyles = new ArrayList<WritingStyle>();
    }

    public void initialize()
    {
        Book book1 = new BookImpl( obtainNewId(), "Cat in Hat", "Dr. Seuss", Book.FICTION, WritingStyle.FUNNY );
        addBook( book1 );
        Book book2 = new BookImpl( obtainNewId(), "That is Highly Illogical", "Dr. Spock", Book.NON_FICTION, WritingStyle.BORING );
        addBook( book2 );
        Book book3 = new BookImpl( obtainNewId(), "Where's my Tardis, dude?", "Dr. Who", Book.FICTION, WritingStyle.BAD );
        addBook( book3 );
        Book book4 = new BookImpl( obtainNewId(), "Frisbee Techniques", "Marty van Hoff", BookImpl.FICTION, WritingStyle.SAD );
        addBook( book4 );
        m_writingStyles.add( WritingStyle.BAD );
        m_writingStyles.add( WritingStyle.SAD );
        m_writingStyles.add( WritingStyle.FUNNY );
        m_writingStyles.add( WritingStyle.BORING );
    }

    public void dispose()
    {

    }

    public Book findBookByTitle( String name )
    {
        synchronized( this )
        {
            return m_BooksByTitle.get( name );
        }
    }

    public Book findBookById( long id )
    {
        synchronized( this )
        {
            return m_BooksById.get( id );
        }
    }

    public void addBook( Book book )
    {
        synchronized( this )
        {
            String title = book.getTitle();
            Long id = book.getId();
            m_BooksById.put( id, book );
            m_BooksByTitle.put( title, book );
        }

    }

    public void removeBook( Book book )
    {
        synchronized( this )
        {
            long id = book.getId();
            String title = book.getTitle();
            m_BooksById.remove( id );
            m_BooksByTitle.remove( title );
        }
    }

    public long obtainNewId()
    {
        return nextId++;
    }

    public List getWritingStyles()
    {
        return m_writingStyles;
    }

    public Collection<Book> findAllBooks()
    {
        ArrayList<Book> result = new ArrayList<Book>();
        result.addAll( m_BooksById.values() );
        return result;
    }

    public Book getRelatedBook( Book reference )
    {
        return null;
    }
}
