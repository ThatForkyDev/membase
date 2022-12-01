package net.tridentgames.membase.type.immutable;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import net.tridentgames.membase.Store;
import net.tridentgames.membase.index.Index;
import net.tridentgames.membase.index.IndexDefinition;
import net.tridentgames.membase.index.IndexException;
import net.tridentgames.membase.index.KeyMapper;
import net.tridentgames.membase.index.reducer.Reducer;
import net.tridentgames.membase.listener.RemovalListener;
import net.tridentgames.membase.listener.enums.RemovalType;
import net.tridentgames.membase.query.Query;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of a store that cannot be modified
 *
 * @param <V> value type
 */
public class ImmutableStore<V> implements Store<V> {
    private final Store<V> store;

    public ImmutableStore(final Store<V> store) {
        this.store = store;
    }

    @Override
    public <K> Index<V> index(final String indexName, final IndexDefinition<K, V> indexDefinition) throws IndexException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K> Index<V> index(final IndexDefinition<K, V> indexDefinition) throws IndexException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K> Index<V> index(final String indexName, final KeyMapper<K, V> keyMapper) throws IndexException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K> Index<V> index(final KeyMapper<K, V> keyMapper) throws IndexException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K> Index<V> index(final String indexName, final KeyMapper<K, V> keyMapper, final Reducer<K, V> reducer) throws IndexException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K> Index<V> index(final KeyMapper<K, V> keyMapper, final Reducer<K, V> reducer) throws IndexException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<V> get(final Query query, @Nullable final Integer limit) {
        return this.store.get(query, limit);
    }

    @Override
    public List<V> get(final Query query) {
        return this.store.get(query);
    }

    @Override
    public V getFirst(final Query query) {
        return this.store.getFirst(query);
    }

    @Override
    public Optional<V> findFirst(final Query query) {
        return this.store.findFirst(query);
    }

    @Override
    public void removeAllIndexes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Index<V>> findIndex(final String indexName) {
        return this.store.findIndex(indexName);
    }

    @Override
    public boolean addAll(final V[] items) throws IndexException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Store<V> synchronizedStore() {
        return this.store.synchronizedStore().immutableStore();
    }

    @Override
    public Index<V> getIndex(final String indexName) {
        return this.store.getIndex(indexName);
    }

    @Override
    public Collection<Index<V>> getIndexes() {
        return this.store.getIndexes();
    }

    @Override
    public boolean removeIndex(final Index<V> index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeIndex(final String indexName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reindex() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reindex(final Collection<V> items) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reindex(final V item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Store<V> immutableStore() {
        return this;
    }

    @Override
    public Map<RemovalType, Set<RemovalListener<V>>> getRemovalListeners() {
        return Collections.emptyMap();
    }

    @Override
    public int size() {
        return this.store.size();
    }

    @Override
    public boolean isEmpty() {
        return this.store.isEmpty();
    }

    @Override
    public boolean contains(final Object obj) {
        return this.store.contains(obj);
    }

    @Override
    public Iterator<V> iterator() {
        return new ImmutableIterator<>(this.store.iterator());
    }

    @Override
    public Object[] toArray() {
        return this.store.toArray();
    }

    @Override
    public <T1> T1[] toArray(final T1[] array) {
        return this.store.toArray(array);
    }

    @Override
    public boolean add(final V item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(final Collection<?> collection) {
        return this.store.containsAll(collection);
    }

    @Override
    public boolean addAll(final Collection<? extends V> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeIf(final Predicate<? super V> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Store<V> copy() {
        return this.store.copy();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        final ImmutableStore<?> that = (ImmutableStore<?>) obj;
        return Objects.equals(this.store, that.store);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.store);
    }

    @Override
    public String toString() {
        return this.store.toString();
    }
}