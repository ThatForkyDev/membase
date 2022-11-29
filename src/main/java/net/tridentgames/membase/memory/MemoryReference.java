package net.tridentgames.membase.memory;

import net.tridentgames.membase.reference.Reference;

/**
 * Reference to a stored item in memory
 *
 * @param <T> reference type
 */
public class MemoryReference<T> implements Reference<T> {
    private final T reference;

    public MemoryReference(final T reference) {
        this.reference = reference;
    }

    @Override
    public T get() {
        return this.reference;
    }

    @Override
    public String toString() {
        return String.valueOf(this.reference);
    }
}
