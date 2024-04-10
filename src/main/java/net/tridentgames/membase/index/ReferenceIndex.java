package net.tridentgames.membase.index;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import net.tridentgames.membase.index.comparison.ComparisonPolicy;
import net.tridentgames.membase.index.reducer.Reducer;
import net.tridentgames.membase.reference.Reference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Maintains indexes against references to stored items
 *
 * @param <V> value type
 */
public class ReferenceIndex<K, V> implements Index<V> {
    private final String name;
    private final KeyMapper<Collection<K>, V> keyMapper;
    private final Reducer<K, V> reducer;
    private final ComparisonPolicy<K> comparisonPolicy;
    private final Map<K, References<K, V>> keyToReferencesMap;
    private final Map<Reference<V>, Set<K>> referenceToKeysMap;

    private ReferenceIndex(final String name, final KeyMapper<Collection<K>, V> keyMapper, final Reducer<K, V> reducer, final ComparisonPolicy<K> comparisonPolicy, final Map<K, References<K, V>> keyToReferencesMap, final Map<Reference<V>, Set<K>> referenceToKeysMap) {
        this.name = name;
        this.keyMapper = keyMapper;
        this.reducer = reducer;
        this.comparisonPolicy = comparisonPolicy;
        this.keyToReferencesMap = keyToReferencesMap;
        this.referenceToKeysMap = referenceToKeysMap;
    }

    public ReferenceIndex(final String indexName, final KeyMapper<Collection<K>, V> keyMapper, final Reducer<K, V> reducer, final ComparisonPolicy<K> comparisonPolicy) {
        this(indexName, keyMapper, reducer, comparisonPolicy, new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public @NotNull Optional<V> findFirst(final Object key) {
        final K comparableKey = this.getComparableKey(key);
        final References<K, V> references = this.keyToReferencesMap.get(comparableKey);

        if (Objects.isNull(references)) {
            return Optional.empty();
        }

        return references.findFirst();
    }

    public @NotNull Set<Reference<V>> getReferences(final Object key) {
        final K comparableKey = this.getComparableKey(key);
        final References<K, V> references = this.keyToReferencesMap.get(comparableKey);

        if (Objects.isNull(references)) {
            return Collections.emptySet();
        }

        return references.getAllReferences();
    }

    @Override
    public List<V> get(final Object key) {
        final K comparableKey = this.getComparableKey(key);
        final References<K, V> references = this.keyToReferencesMap.get(comparableKey);

        if (Objects.isNull(references)) {
            return Collections.emptyList();
        }

        return references.getAll();
    }

    public void index(final Reference<V> reference) throws IndexCreationException {
        final Set<K> keys = this.generateKeys(reference);

        this.removeIndex(reference);

        if (!keys.isEmpty()) {
            this.referenceToKeysMap.put(reference, Collections.unmodifiableSet(keys));
            keys.forEach(key -> this.keyToReferencesMap.computeIfAbsent(key, ignore -> new References<>(key, reference, this.reducer)).add(reference));
        }
    }

    public void removeIndex(final Reference<V> reference) {
        final Set<K> keys = this.referenceToKeysMap.get(reference);

        if (Objects.isNull(keys)) {
            return;
        }

        for (final K key : keys) {
            final References<K, V> references = this.keyToReferencesMap.get(key);

            if (reference != null) {
                if (references != null) {
                    references.remove(reference);

                    if (references.isEmpty()) {
                        this.keyToReferencesMap.remove(key);
                    }
                }
            }
        }

        this.referenceToKeysMap.remove(reference);
    }

    public void clear() {
        this.keyToReferencesMap.clear();
        this.referenceToKeysMap.clear();
    }

    public ReferenceIndex<K, V> copy() {
        final Map<K, References<K, V>> keyToReferencesMapCopy = this.keyToReferencesMap.entrySet()
            .stream()
            .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().copy()));

        final Map<Reference<V>, Set<K>> referenceToKeysMapCopy = new HashMap<>(this.referenceToKeysMap);

        return new ReferenceIndex<>(this.name, this.keyMapper, this.reducer, this.comparisonPolicy, keyToReferencesMapCopy, referenceToKeysMapCopy);
    }

    private Set<K> generateKeys(final Reference<V> reference) throws IndexCreationException {
        final V item;

        try {
            item = reference.get();
        } catch (final RuntimeException e) {
            e.printStackTrace();
            throw new IndexCreationException("Index: " + this.name + ". Unable to retrieve item to index", e);
        }

        try {
            final Set<K> set = new HashSet<>();

            for (final K key : this.keyMapper.map(item)) {
                final K value = this.getComparableKey(key);

                if (value == null) {
                    continue;
                }

                if (value instanceof Collection<?>) {
                    final Collection<? extends K> collection = (Collection<? extends K>) value;
                    set.addAll(collection);
                    continue;
                }

                set.add(value);
            }

            return set;
        } catch (final RuntimeException e) {
            e.printStackTrace();
            throw new IndexCreationException("Index: " + this.name + ". Error generating indexes for item: " + item, e);
        }
    }

    @SuppressWarnings("unchecked")
    private @Nullable K getComparableKey(final Object key) {
        if (Objects.isNull(key) || !this.comparisonPolicy.supports(key.getClass())) {
            return null;
        }

        if (key instanceof Collection<?>) {
            return (K) key;
        }

        return this.comparisonPolicy.createComparable((K) key);
    }

    @Override
    public String toString() {
        return "Index[name='" + this.name + "']";
    }
}
