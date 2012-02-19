/*
 * Copyright (c) 2007-2010 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Cascading is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cascading is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cascading.  If not, see <http://www.gnu.org/licenses/>.
 */

package cascading.tuple;

import java.util.Iterator;
import java.util.LinkedList;

// TODO: remove this class once a cascading-core test artifact is published

/**
 * TupleEntryCollector is a convenience class for managing a list of tuples. More specifically it can simultaneously
 * append and modify in place elements of the list through the use of a ListIterator.
 */
public class TupleListCollector extends TupleEntryCollector implements Iterable<Tuple>
{
    /** Field tuples */
    private final LinkedList<Tuple> tuples = new LinkedList<Tuple>();
    /** Field copyTupleOnCollect */
    private boolean copyTupleOnCollect = false;

    /**
     * Constructor TupleEntryCollector creates a new TupleEntryCollector instance.
     *
     * @param fields of type Fields
     * @param tuple  of type Tuple...
     */
    public TupleListCollector( Fields fields, Tuple... tuple )
    {
        super( fields );

        collect( tuple );
    }

    /**
     * Constructor TupleListCollector creates a new TupleListCollector instance.
     * <p/>
     * Set copyTupleOnCollect to {@code true} if a new Tuple instance should be stored in the
     * underlying list.
     *
     * @param fields             of type Fields
     * @param copyTupleOnCollect of type boolean
     */
    public TupleListCollector( Fields fields, boolean copyTupleOnCollect )
    {
        super( fields );
        this.copyTupleOnCollect = copyTupleOnCollect;
    }

    /**
     * Method collect adds every given Tuple instance. It tests for and ignores empty Tuples.
     *
     * @param tuples of type Tuple
     */
    private void collect( Tuple... tuples )
    {
        for( Tuple tuple : tuples )
            add( tuple );
    }

    protected void collect( Tuple tuple )
    {
        if( copyTupleOnCollect )
            tuple = new Tuple( tuple );

        tuples.add( tuple );
    }

    /**
     * Method isEmpty returns true if this collection is empty.
     *
     * @return the empty (type boolean) of this TupleCollector object.
     */
    public boolean isEmpty()
    {
        return tuples.isEmpty();
    }

    /** Method clear clears all Tuple instances from this instance. */
    public void clear()
    {
        tuples.clear();
    }

    /**
     * Returns the size of this collection.
     *
     * @return int
     */
    public int size()
    {
        return tuples.size();
    }

    /**
     * Method iterator returns an iterator for this collection.
     *
     * @return Iterator<Tuple>
     */
    public Iterator<Tuple> iterator()
    {
        return tuples.iterator();
    }

    /**
     * Method entryIterator return a TupleEntry iterator for this collection.
     * </p>
     * Note the same TupleEntry will be returned on each next() call.
     *
     * @return Iterator<TupleEntry>
     */
    public Iterator<TupleEntry> entryIterator()
    {
        return new TupleEntryIterator( declared, tuples.iterator() );
    }
}