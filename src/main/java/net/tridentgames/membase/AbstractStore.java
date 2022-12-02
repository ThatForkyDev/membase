package net.tridentgames.membase;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import net.tridentgames.membase.index.Index;
import net.tridentgames.membase.index.IndexDefinition;
import net.tridentgames.membase.index.IndexException;
import net.tridentgames.membase.index.IndexManager;
import net.tridentgames.membase.query.Query;
import net.tridentgames.membase.query.section.Section;
import net.tridentgames.membase.query.section.SectionOperator;
import net.tridentgames.membase.query.section.SectionPart;
import net.tridentgames.membase.reference.Reference;
import net.tridentgames.membase.reference.ReferenceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractStore<V> extends AbstractCollection<V> implements Store<V> {
    protected final ReferenceManager<V> referenceManager;
    protected final IndexManager<V> indexManager;

    protected AbstractStore(final ReferenceManager<V> referenceManager, final IndexManager<V> indexManager) {
        this.referenceManager = referenceManager;
        this.indexManager = indexManager;
    }

    @Override
    public List<V> get(final Query query, @Nullable final Integer limit) {
        final Set<Reference<V>> results = new LinkedHashSet<>();

        for (final Section section : query.getSections()) {
            boolean firstMatch = true;

            for (final SectionPart part : section.getParts()) {
                final Set<Reference<V>> references = Optional.ofNullable(this.indexManager.getIndex(part.getKey()))
                    .map(index -> index.getReferences(part.getValue()))
                    .orElse(Collections.emptySet());

                if (firstMatch || section.getOperator() == SectionOperator.OR) {
                    results.addAll(references);
                    firstMatch = false;
                } else {
                    results.retainAll(references);
                }
            }
        }

        return results.stream()
            .map(Reference::get)
            .limit(Objects.isNull(limit) ? Long.MAX_VALUE : limit)
            .collect(Collectors.toList());
    }

    @Override
    public List<V> remove(@NotNull Query query) {
        final List<V> results = this.get(query, null);

        for (final V result : results) {
            this.remove(result);
        }

        return results;
    }

    @Override
    public <K> Index<V> index(final String indexName, final IndexDefinition<K, V> indexDefinition) throws IndexException {
        return this.indexManager.createIndex(indexName, indexDefinition, this.referenceManager.getReferences());
    }

    @Override
    public Index<V> getIndex(final String indexName) {
        return this.indexManager.getIndex(indexName);
    }

    @Override
    public Collection<Index<V>> getIndexes() {
        return this.indexManager.getIndexes();
    }

    @Override
    public boolean removeIndex(final Index<V> index) {
        return this.indexManager.removeIndex(index);
    }

    @Override
    public boolean removeIndex(final String indexName) {
        return this.indexManager.removeIndex(indexName);
    }

    @Override
    public void reindex() {
        this.indexManager.reindex(this.referenceManager.getReferences());
    }

    @Override
    public void reindex(final V item) {
        this.reindex(Collections.singleton(item));
    }

    @Override
    public void reindex(final Collection<V> items) {
        final List<Reference<V>> references = items.stream()
            .map(this.referenceManager::findReference)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        this.indexManager.reindex(references);
    }

    @Override
    public int size() {
        return this.referenceManager.size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean contains(final Object obj) {
        return this.referenceManager.findReference(obj).isPresent();
    }

    @Override
    public Iterator<V> iterator() {
        return new StoreIterator(this.referenceManager.getReferences().iterator());
    }

    @Override
    public boolean addAll(final Collection<? extends V> collection) {
        final List<Reference<V>> references = new ArrayList<>();
        boolean changed = false;

        for (final V item : collection) {
            final Optional<Reference<V>> existingReference = this.referenceManager.findReference(item);

            if (existingReference.isPresent()) {
                references.add(existingReference.get());
                continue;
            }

            references.add(this.referenceManager.add(item));
            changed = true;
        }

        this.indexManager.reindex(references);
        return changed;
    }

    @Override
    public boolean add(final V item) {
        return this.addAll(Collections.singleton(item));
    }

    @Override
    public boolean remove(@Nullable Object obj) {
        final Reference<V> reference = this.referenceManager.remove(obj);

        if (reference != null) {
            this.indexManager.removeReference(reference);
            return true;
        }

        return false;
    }

    @Override
    public void clear() {
        this.referenceManager.clear();
        this.indexManager.clear();
    }

    @Override
    public @NotNull Store<V> copy() {
        return this.createCopy(this.referenceManager, this.indexManager);
    }

    protected abstract @NotNull Store<V> createCopy(final ReferenceManager<V> referenceManager, final IndexManager<V> indexManager);

    protected ReferenceManager<V> getReferenceManager() {
        return this.referenceManager;
    }

    private class StoreIterator implements Iterator<V> {
        private final Iterator<Reference<V>> iterator;
        private Reference<V> previous;

        StoreIterator(final Iterator<Reference<V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public V next() {
            this.previous = this.iterator.next();
            return this.previous.get();
        }

        @Override
        public void remove() {
            this.iterator.remove();
            AbstractStore.this.indexManager.removeReference(this.previous);
        }
    }
}
