package net.tridentgames.membase.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.tridentgames.membase.reference.Reference;

public abstract class IndexManager<V> {
    private final Map<String, ReferenceIndex<?, V>> indexMap;

    public IndexManager(final Collection<ReferenceIndex<?, V>> indexes) {
        this.indexMap = new HashMap<>();
        indexes.forEach(index -> this.indexMap.put(index.getName(), index));
    }

    public <K> ReferenceIndex<K, V> createIndex(String indexName, IndexDefinition<K, V> indexDefinition, Collection<Reference<V>> references) {
        if (this.indexMap.containsKey(indexName)) {
            throw new IllegalArgumentException("An index already exists with this name");
        }

        final ReferenceIndex<K, V> newIndex = this.createIndex(indexName, indexDefinition);
        this.indexMap.put(indexName, newIndex);
        indexReferences(Collections.singleton(newIndex), references);
        return newIndex;
    }

    public ReferenceIndex<?, V> getIndex(final String indexName) {
        return this.indexMap.get(indexName);
    }

    public void reindex(final Collection<Reference<V>> references) {
        indexReferences(this.indexMap.values(), references);
    }

    public boolean removeIndex(final String indexName) {
        return this.indexMap.remove(indexName) != null;
    }

    public boolean removeIndex(final Index<V> index) {
        if (index.equals(this.indexMap.get(index.getName()))) {
            this.indexMap.remove(index.getName());
            return true;
        }

        return false;
    }

    public void removeReference(final Reference<V> reference) {
        this.indexMap.values().forEach(index -> index.removeIndex(reference));
    }

    public void clear() {
        this.indexMap.values().forEach(ReferenceIndex::clear);
    }

    public Collection<Index<V>> getIndexes() {
        return Collections.unmodifiableCollection(this.indexMap.values());
    }

    public IndexManager<V> copy() {
        final Set<ReferenceIndex<?, V>> copyOfIndexes = this.indexMap.values().stream().map(ReferenceIndex::copy).collect(Collectors.toSet());
        return this.createCopy(copyOfIndexes);
    }

    protected abstract IndexManager<V> createCopy(Set<ReferenceIndex<?, V>> copyOfIndexes);

    protected abstract <K> ReferenceIndex<K, V> createIndex(String indexName, IndexDefinition<K, V> indexDefinition);

    private static <T> void indexReferences(final Collection<ReferenceIndex<?, T>> indexes, final Collection<Reference<T>> references) {
        final List<IndexCreationException> exceptions = new ArrayList<>();

        for (final Reference<T> reference : references) {
            if (reference == null) {
                continue;
            }

            for (final ReferenceIndex<?, T> index : indexes) {
                try {
                    index.index(reference);
                } catch (final IndexCreationException e) {
                    exceptions.add(e);
                }
            }
        }

        if (!exceptions.isEmpty()) {
            final String message = (exceptions.size() == 1 ? "1 exception" : exceptions.size() + " exceptions") + " occurred during indexing";

            for (final IndexCreationException exception : exceptions) {
                exception.printStackTrace();
            }

            throw new IndexException(message, exceptions);
        }
    }
}
