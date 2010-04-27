package org.ops4j.pax.wicket.internal;

import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationAdapter<E>
    implements Iterator<E>
{
    private final Enumeration<E> m_enumeration;

    public EnumerationAdapter( Enumeration<E> enumeration )
    {
        m_enumeration = enumeration;
    }

    public boolean hasNext()
    {
        return m_enumeration.hasMoreElements();
    }

    public E next()
    {
        return m_enumeration.nextElement();
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
