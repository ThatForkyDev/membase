package net.tridentgames.membase.type.immutable;

import java.util.Iterator;

public class ImmutableIterator<V> implements Iterator<V> {
    private final Iterator<V> iterator;

    public ImmutableIterator(final Iterator<V> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override
    public V next() {
        return this.iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
