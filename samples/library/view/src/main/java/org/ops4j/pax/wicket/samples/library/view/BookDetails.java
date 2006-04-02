/*
 * $Id: BookDetails.java 1285 2005-03-06 08:58:22Z jonathanlocke $
 * $Revision: 1285 $
 * $Date: 2005-03-06 16:58:22 +0800 (Sun, 06 Mar 2005) $
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
package org.ops4j.pax.wicket.samples.library.view;

import java.util.Iterator;
import org.ops4j.pax.wicket.samples.library.controller.LibraryHomePage;
import org.ops4j.pax.wicket.samples.library.model.BookImpl;
import org.ops4j.pax.wicket.samples.library.model.WritingStyle;
import wicket.AttributeModifier;
import wicket.PageParameters;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.BookmarkablePageLink;
import wicket.model.Model;
import wicket.util.string.StringList;
import wicket.util.string.StringValueConversionException;

/**
 * A book details page.  Shows information about a book.
 *
 * @author Jonathan Locke
 */
public final class BookDetails extends LibraryHomePage
{

    /**
     * Constructor for calls from external page links
     *
     * @param parameters Page parameters
     */
    public BookDetails( final PageParameters parameters )
        throws StringValueConversionException
    {
        this( parameters.getLong( "id" ) );
    }

    /**
     * Constructor
     *
     * @param book The model
     */
    public BookDetails( long id )
    {
        Book book = Activator.
        Model bookModel = new Model( book );

        add( new Label( "title", book.getTitle() ) );
        add( new Label( "author", book.getAuthor() ) );
        add( new Label( "fiction", Boolean.toString( book.getFiction() ) ) );
        add( link( "companion", book.getCompanionBook(),
                   getLocalizer().getString( "noBookTitle", this )
        )
        );
        add( link( "related", book.getRelatedBook(),
                   getLocalizer().getString( "noBookTitle", this )
        )
        );

        String writingStyles;
        final boolean hasStyles = ( book.getWritingStyles() != null )
                                  && ( book.getWritingStyles().size() > 0 );

        if( hasStyles )
        {
            StringList styles = new StringList();

            for(
                Iterator iterator = book.getWritingStyles().iterator();
                iterator.hasNext();
                )
            {
                WritingStyle style = (WritingStyle) iterator.next();

                styles.add( getLocalizer().getString( style.toString(), this ) );
            }

            writingStyles = styles.toString();
        }
        else
        {
            writingStyles = getLocalizer().getString( "noWritingStyles", this );
        }

        Label writingStylesLabel = new Label( "writingStyles", writingStyles );

        final AttributeModifier italic = new AttributeModifier( "class", new Model( "italic" ) );
        italic.setEnabled( !hasStyles );

        add( writingStylesLabel.add( italic ) );
        add( EditBook.link( "edit", book.getId() ) );
    }

    /**
     * Creates an external page link
     *
     * @param name        The name of the link component to create
     * @param book        The book to link to
     * @param noBookTitle The title to show if book is null
     *
     * @return The external page link
     */
    public static BookmarkablePageLink link( final String name, final BookImpl book,
                                             final String noBookTitle )
    {
        final BookmarkablePageLink link = new BookmarkablePageLink( name,
                                                                    BookDetails.class
        );

        if( book != null )
        {
            link.setParameter( "id", book.getId() );
            link.add( new Label( "title", new Model( book ) ) );
        }
        else
        {
            link.add( new Label( "title", noBookTitle ) );
            link.setEnabled( false );
        }

        return link;
    }
}


