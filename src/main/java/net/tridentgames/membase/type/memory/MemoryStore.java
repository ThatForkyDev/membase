package net.tridentgames.membase.type.memory;

import java.util.Collection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.tridentgames.membase.AbstractStore;
import net.tridentgames.membase.Store;
import net.tridentgames.membase.identity.DefaultIdentityProvider;
import net.tridentgames.membase.index.IndexDefinition;
import net.tridentgames.membase.index.IndexManager;
import net.tridentgames.membase.index.KeyMapper;
import net.tridentgames.membase.index.ReferenceIndexManager;
import net.tridentgames.membase.index.reducer.Reducer;
import net.tridentgames.membase.listener.RemovalListener;
import net.tridentgames.membase.listener.enums.RemovalType;
import net.tridentgames.membase.memory.MemoryReferenceFactory;
import net.tridentgames.membase.reference.DefaultReferenceManager;
import net.tridentgames.membase.reference.ReferenceManager;
import org.jetbrains.annotations.NotNull;

/**
 * In memory implementation of a {@link Store}
 *
 * @param <V> type of item referenced
 */
public class MemoryStore<V> extends AbstractStore<V> {
    private Map<RemovalType, Set<RemovalListener<V>>> removalListeners = new HashMap<>();

    private MemoryStore(final ReferenceManager<V> referenceManager, final IndexManager<V> indexManager) {
        super(referenceManager, indexManager);
    }

    public MemoryStore() {
        this(new DefaultReferenceManager<>(new DefaultIdentityProvider(), new MemoryReferenceFactory<>()), new ReferenceIndexManager<>());
    }

    public MemoryStore(final Collection<V> items) {
        this();
        this.addAll(items);
    }

    @SafeVarargs
    public MemoryStore(final V... items) {
        this();
        this.addAll(items);
    }

    @Override
    protected Store<V> createCopy(final ReferenceManager<V> referenceManager, final IndexManager<V> indexManager) {
        return new MemoryStore<>(referenceManager.copy(), indexManager.copy());
    }

    @Override
    public @NotNull String toString() {
        return this.getReferenceManager().getReferences().toString();
    }

    /**
     * New store builder
     *
     * @param <V> data type
     * @return builder
     */
    public static <V> Builder<V> newStore() {
        return new Builder<>();
    }

    @Override
    public Map<RemovalType, Set<RemovalListener<V>>> getRemovalListeners() {
        return this.removalListeners;
    }

    public static class Builder<V> {
        private final MemoryStore<V> store;

        protected Builder() {
            this.store = new MemoryStore<>();
        }

        public @NotNull Builder<V> withValue(final V value) {
            this.store.add(value);
            return this;
        }

        public @NotNull Builder<V> withValues(final Collection<V> values) {
            this.store.addAll(values);
            return this;
        }

        @SafeVarargs
        public final @NotNull Builder<V> withValues(final V... values) {
            this.store.addAll(values);
            return this;
        }

        public @NotNull <K> Builder<V> withIndex(final String indexName, final KeyMapper<K, V> keyMapper) {
            this.store.index(indexName, keyMapper);
            return this;
        }

        public @NotNull <K> Builder<V> withIndex(final String indexName, final KeyMapper<K, V> keyMapper, final Reducer<K, V> reducer) {
            this.store.index(indexName, keyMapper, reducer);
            return this;
        }

        public @NotNull <K> Builder<V> withIndex(final String indexName, final IndexDefinition<K, V> indexDefinition) {
            this.store.index(indexName, indexDefinition);
            return this;
        }

        public final @NotNull MemoryStore<V> build() {
            return this.store;
        }
    }
}
