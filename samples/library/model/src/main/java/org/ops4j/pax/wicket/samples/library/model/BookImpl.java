/*
 * $Id: Book.java 1684 2005-04-19 18:16:40Z jdonnerstag $
 * $Revision: 1684 $
 * $Date: 2005-04-20 02:16:40 +0800 (Wed, 20 Apr 2005) $
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
import java.util.Collections;
import java.util.List;

/**
 * An example POJO model.
 *
 * @author Jonathan Locke
 */
public final class BookImpl
    implements Serializable, Book
{
    private static final long serialVersionUID = 1L;

    private long m_id;
    private String m_title;
    private String m_author;
    private Book m_companionBook;
    private List<Book> m_relatedBooks;
    private boolean m_isFiction;
    private WritingStyle m_style;

    /**
     * Constructor
     *
     * @param title     Book title
     * @param author    The author of the book
     * @param isFiction True (FICTION) if the book is fiction, false (NON_FICTION)
     *                  if it is not.
     */
    public BookImpl( long id, String title, String author, boolean isFiction, WritingStyle style )
    {
        m_id = id;
        m_title = title;
        m_author = author;
        m_isFiction = isFiction;
        m_style = style;
        m_relatedBooks = new ArrayList<Book>();
    }

    /**
     * @return Book id
     */
    public final long getId()
    {
        return m_id;
    }

    /**
     * @return The author
     */
    public final String getAuthor()
    {
        return m_author;
    }

    /**
     * @return The title
     */
    public final String getTitle()
    {
        return m_title;
    }

    public List<WritingStyle> getWritingStyles()
    {
        ArrayList result = new ArrayList();
        result.add( m_style );
        return result;
    }

    /**
     * @return A book that makes a good companion to this one
     */
    public final Book getCompanionBook()
    {
        return m_companionBook;
    }

    /**
     * @param book A book that makes a good companion to this one
     */
    public final void setCompanionBook( Book book )
    {
        m_companionBook = book;
    }

    /**
     * @return True if this book is fiction
     */
    public final boolean getFiction()
    {
        return m_isFiction;
    }

    public List<Book> getRelatedBooks()
    {
        return Collections.unmodifiableList( m_relatedBooks );
    }

    public void addRelatedBook( Book book )
    {
        synchronized( this )
        {
            m_relatedBooks.add( book );
        }
    }

    public void removeRelatedBook( Book book )
    {
        synchronized( this )
        {
            m_relatedBooks.remove( book );
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public final String toString()
    {
        return m_title + " (" + m_author + ")";
    }
}


