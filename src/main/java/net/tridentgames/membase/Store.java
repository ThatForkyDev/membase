package net.tridentgames.membase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import net.tridentgames.membase.index.Index;
import net.tridentgames.membase.index.IndexDefinition;
import net.tridentgames.membase.index.IndexException;
import net.tridentgames.membase.index.KeyMapper;
import net.tridentgames.membase.index.reducer.Reducer;
import net.tridentgames.membase.listener.RemovalListener;
import net.tridentgames.membase.listener.enums.RemovalType;
import net.tridentgames.membase.query.Query;
import net.tridentgames.membase.type.concurrent.SynchronizedStore;
import net.tridentgames.membase.type.immutable.ImmutableStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Store of data
 *
 * @param <V> value type
 */
public interface Store<V> extends Collection<V> {
    /**
     * Create an index using the index build provided
     *
     * @param indexName       name of the index
     * @param indexDefinition build of the index
     * @param <K>             indexed key type
     * @return index
     * @throws IndexException thrown if the creation of the new index fails with exceptions.
     */
    <K> Index<V> index(String indexName, IndexDefinition<K, V> indexDefinition) throws IndexException;

    /**
     * Create an index using the index build provided
     *
     * @param indexDefinition build of the index
     * @param <K>             indexed key type
     * @return index
     * @throws IndexException thrown if the creation of the new index fails with exceptions.
     */
    default <K> Index<V> index(final IndexDefinition<K, V> indexDefinition) throws IndexException {
        return this.index(UUID.randomUUID().toString(), indexDefinition);
    }

    /**
     * Register a new index with this store, mapping a single value to a collection of indexed keys.
     * This is a convenient of creating an index, for more options see {@link #index(IndexDefinition)}
     *
     * @param indexName name of the index
     * @param keyMapper function to provide a value to one or more keys
     * @param <K>       key type
     * @return index
     * @throws IndexException thrown if the new index failed with exceptions.
     */
    default <K> Index<V> index(final String indexName, final KeyMapper<K, V> keyMapper) throws IndexException {
        return this.index(indexName, IndexDefinition.withKeyMapping(keyMapper));
    }

    /**
     * Register a new index with this store, mapping a single value to a collection of indexed keys.
     * This is a convenient of creating an index, for more options see {@link #index(IndexDefinition)}
     *
     * @param indexName name of the index
     * @param keyMapper function to provide a value to one or more keys
     * @param reducer   function to reduce indexed values
     * @param <K>       key type
     * @return index
     * @throws IndexException thrown if the new index failed with exceptions.
     */
    default <K> Index<V> index(final String indexName, final KeyMapper<K, V> keyMapper, final Reducer<K, V> reducer) throws IndexException {
        return this.index(indexName, IndexDefinition.withKeyMapping(keyMapper).withReducer(reducer));
    }

    /**
     * Register a new index with this store, mapping a single value to a collection of indexed keys.
     * This is a convenient of creating an index, for more options see {@link #index(IndexDefinition)}
     *
     * @param keyMapper function to provide a value to one or more keys
     * @param reducer   function to reduce indexed values
     * @param <K>       key type
     * @return index
     * @throws IndexException thrown if the new index failed with exceptions.
     */
    default <K> Index<V> index(final KeyMapper<K, V> keyMapper, final Reducer<K, V> reducer) throws IndexException {
        return this.index(IndexDefinition.withKeyMapping(keyMapper).withReducer(reducer));
    }

    /**
     * Register a new index with this store, mapping a single value to a collection of indexed keys.
     * This is a convenient of creating an index, for more options see {@link #index(IndexDefinition)}
     *
     * @param keyMapper function to provide a value to one or more keys
     * @param <K>       key type
     * @return index
     * @throws IndexException thrown if the new index failed with exceptions.
     */
    default <K> Index<V> index(final KeyMapper<K, V> keyMapper) throws IndexException {
        return this.index(IndexDefinition.withKeyMapping(keyMapper));
    }

    /**
     * Query indexes and look up all  matching value.
     *
     * @param query query to execute
     * @param limit limit results to the first x number of items
     * @return values associated with this key or an empty list
     */
    List<V> get(final Query query, @Nullable final Integer limit);

    List<V> remove(@NotNull Query query);

    /**
     * Query indexes and look up all  matching value.
     *
     * @param query query to execute
     * @return values associated with this key or an empty list
     */
    default List<V> get(final Query query) {
        return this.get(query, null);
    }

    /**
     * Query indexes and look up the first matching value.
     *
     * @param query query to execute
     * @return first value matching the query
     */
    default V getFirst(final Query query) {
        final List<V> results = this.get(query, 1);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Query indexes and look up the first matching value.
     *
     * @param query query to execute
     * @return first value matching the query
     */
    default Optional<V> findFirst(final Query query) {
        return Optional.ofNullable(this.getFirst(query));
    }

    /**
     * Find index with name. This is the same as {@link Store#findIndex(String)}, but returns a null instead of an optional if an index cannot be found.
     *
     * @param indexName name of index to lookup
     * @return index
     */
    @Nullable Index<V> getIndex(String indexName);

    /**
     * Get all indexes
     *
     * @return indexes
     */
    Collection<Index<V>> getIndexes();

    /**
     * Remove all indexes associated with this store
     */
    default void removeAllIndexes() {
        new ArrayList<>(this.getIndexes()).forEach(this::removeIndex);
    }

    /**
     * Find index with name. This is the same as {@link Store#getIndex(String)}, but returns an optional instead of a null if an index cannot be found.
     *
     * @param indexName name of index to lookup
     * @return index optional
     */
    default Optional<Index<V>> findIndex(final String indexName) {
        return Optional.ofNullable(this.getIndex(indexName));
    }

    /**
     * Remove an index from store
     *
     * @param indexName index to remove
     * @return true if removed successfully
     */
    boolean removeIndex(String indexName);

    /**
     * Remove an index from store
     *
     * @param index index to remove
     * @return true if removed successfully
     */
    boolean removeIndex(Index<V> index);

    /**
     * Clear the existing indexes and reindex the entire store. This can be a slow operation
     * depending on the number of items in the store and total number of indexes.
     *
     * @throws IndexException thrown if one or more indexes failed with exceptions.
     */
    void reindex() throws IndexException;

    /**
     * Reindex a collection of items. This method will need to be called anytime a change
     * is made to items stored within the store that causes its indexes to become out
     * of date.
     *
     * @param items items to reindex
     * @throws IndexException thrown if one or more indexes failed with exceptions.
     */
    void reindex(Collection<V> items) throws IndexException;

    /**
     * Reindex a particular item. This method will need to be called anytime a change
     * is made to an item stored within the store that causes its indexes to become out
     * of date.
     *
     * @param item item to reindex
     * @throws IndexException thrown if one or more indexes failed with exceptions.
     */
    void reindex(final V item) throws IndexException;

    /**
     * Adds an item to the store and indexes it. If the item already exists in the store,
     * it will be reindexed.
     *
     * @param item item to on
     * @return true if item did not previously exist in the store.
     * @throws IndexException thrown if one or more indexes failed with exceptions.
     */
    @Override
    boolean add(V item) throws IndexException;

    /**
     * Adds all item to the store and indexes them. If an item already exists in the store,
     * it will be reindexed.
     *
     * @param items items to on
     * @return true if one or more items did not previously exist in the store.
     * @throws IndexException thrown if one or more indexes failed with exceptions.
     */
    @Override
    boolean addAll(Collection<? extends V> items) throws IndexException;

    /**
     * Adds all item to the store and indexes them. If an item already exists in the store,
     * it will be re-indexed.
     *
     * @param items items to on
     * @return true if one or more items did not previously exist in the store.
     * @throws IndexException thrown if one or more indexes failed with exceptions.
     */
    default boolean addAll(final V[] items) throws IndexException {
        return this.addAll(Arrays.asList(items));
    }

    /**
     * Create a copy of this store. This can be an expensive operation depending
     * on the number of items and indexes present. The copied store will be fully
     * independent from this store. Any changes made to the copy will not reflect back
     * onto this store.
     *
     * @return copy
     */
    @NotNull Store<V> copy();

    /**
     * Returns an unmodifiable view of this store. This method allows
     * modules to provide users with "read-only" access to internal
     * collections. Query operations on the returned store "read through"
     * the backed store, and attempts to modify the returned
     * store, whether direct or via its iterator, result in an
     * <tt>UnsupportedOperationException</tt>.<p>
     *
     * @return an unmodifiable view of this store.
     */
    default Store<V> immutableStore() {
        return new ImmutableStore<>(this);
    }

    /**
     * Returns an unmodifiable view of the specified collection. This method allows
     * modules to provide users with "read-only" access to internal
     * collections. Query operations on the returned collection "read through"
     * the specified collection, and attempts to modify the returned
     * collection, whether direct or via its iterator, result in an
     * <tt>UnsupportedOperationException</tt>.<p>
     *
     * @param collection the collection for which an unmodifiable view is to be returned.
     * @return an unmodifiable view of the specified collection.
     */
    Map<RemovalType, Set<RemovalListener<V>>> getRemovalListeners();

    /**
     * Returns a synchronized (thread-safe) map Store backed by this store.
     * In order to guarantee serial access, it is critical that
     * <strong>all</strong> access to the backing store is accomplished
     * through the returned store.<p> Any references held to {@link Index}es
     * should be discarded. Call {@link Store#getIndex(String)} on the synchronized
     * store to obtain synchronized indexes.
     *
     * It is imperative that the user manually synchronize on the returned
     * Store when iterating over it:
     * <pre>
     *  Store store = oldStore.synchronizedStore();
     *      ...
     *  synchronized (store) {
     *      Iterator i = store.iterator(); // Must be in synchronized block
     *      while (i.hasNext())
     *          foo(i.next());
     *  }
     * </pre>
     * Failure to follow this advice may result in non-deterministic behavior.
     *
     * @return synchronized store
     */
    default Store<V> synchronizedStore() {
        return new SynchronizedStore<>(this);
    }
}
