package com.mygdx.game;

/**
 * Copyright 2005-2010 Noelios Technologies.
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL 1.0 (the
 * "Licenses"). You can select the license that you prefer but you may not use
 * this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0.html
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1.php
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1.php
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.noelios.com/products/restlet-engine
 * 
 * Restlet is a registered trademark of Noelios Technologies.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Emulate the CopyOnWriteArrayList class, especially for the GWT module.
 * 
 * @author Thierry Boileau
 */
public class CopyOnWriteArrayList<E> implements List<E> {

    private List<E> list;

    public CopyOnWriteArrayList() {
        super();
        list = new ArrayList<E>();
    }

    public CopyOnWriteArrayList(Collection<? extends E> c) {
        super();
        this.list = new ArrayList<E>();
        this.list.addAll(c);
    }

    public boolean add(E o) {
        return list.add(o);
    }

    public void add(int index, E element) {
        list.add(index, element);
    }

    public boolean addAll(Collection<? extends E> c) {
        return list.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        return list.addAll(index, c);
    }

    public void clear() {
        list.clear();
    }

    public boolean contains(Object o) {
        return list.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    public E get(int index) {
        return list.get(index);
    }

    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public Iterator<E> iterator() {
        return list.iterator();
    }

    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    public ListIterator<E> listIterator() {
        return list.listIterator();
    }

    public ListIterator<E> listIterator(int index) {
        return list.listIterator(index);
    }

    public boolean remove(Object o) {
        return list.remove(o);
    }

    public E remove(int index) {
        return list.remove(index);
    }

    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    public E set(int index, E element) {
        return list.set(index, element);
    }

    public int size() {
        return list.size();
    }

    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > list.size())
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex
                    + ") > toIndex(" + toIndex + ")");
        ArrayList<E> subList = new ArrayList<E>();
        for (int i = fromIndex; i <= toIndex; i++) {
            subList.add(list.get(i));
        }
        return subList;
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

}
