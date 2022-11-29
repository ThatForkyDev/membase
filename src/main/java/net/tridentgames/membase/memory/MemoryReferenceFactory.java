package net.tridentgames.membase.memory;

import net.tridentgames.membase.reference.Reference;
import net.tridentgames.membase.reference.ReferenceFactory;

/**
 * Factory for creating in memory references
 *
 * @param <V> value type
 */
public class MemoryReferenceFactory<V> implements ReferenceFactory<V> {
    @Override
    public Reference<V> createReference(final V obj) {
        return new MemoryReference<>(obj);
    }
}
