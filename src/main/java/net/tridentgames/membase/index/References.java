package net.tridentgames.membase.index;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import net.tridentgames.membase.index.reducer.Reducer;
import net.tridentgames.membase.reference.Reference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class References<K, V> {
    private final K key;
    private final Reducer<K, V> reducer;
    private final Set<Reference<V>> references;
    private Set<Reference<V>> reducedReferences;

    private References(@NotNull K key, @NotNull Set<Reference<V>> references, @NotNull Collection<Reference<V>> reducedReferences, @Nullable Reducer<K, V> reducer) {
        this.key = key;
        this.references = new LinkedHashSet<>(references);
        this.reducedReferences = new LinkedHashSet<>(reducedReferences);
        this.reducer = reducer;
    }

    public References(@NotNull K key, @NotNull Reference<V> reference, @Nullable Reducer<K, V> reducer) {
        this(key, Collections.singleton(reference), Collections.emptySet(), reducer);
        this.reducedReferences.add(reference);
        this.reducedReferences = this.reduce(this.reducedReferences);
    }

    public void add(@NotNull Reference<V> reference) {
        this.references.add(reference);
        this.reducedReferences.add(reference);
        this.reducedReferences = this.reduce(this.reducedReferences);
    }

    public void remove(@NotNull Reference<V> reference) {
        this.references.remove(reference);

        if (this.reducedReferences.contains(reference)) {
            this.reducedReferences = this.reduce(this.references); // on remove, re-reduce all references associated with this key
        }
    }

    public @NotNull @Unmodifiable Set<Reference<V>> getAllReferences() {
        return Collections.unmodifiableSet(this.reducedReferences);
    }

    public @NotNull List<V> getAll() {
        return this.reducedReferences.stream().map(Reference::get).collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return this.references.isEmpty();
    }

    public Optional<V> findFirst() {
        return this.reducedReferences.stream().map(Reference::get).findFirst();
    }

    public References<K, V> copy() {
        return new References<>(this.key, this.references, this.reducedReferences, this.reducer);
    }

    private @NotNull Set<Reference<V>> reduce(final Set<Reference<V>> references) {
        if (Objects.isNull(this.reducer)) {
            return references;
        }

        final List<Element<V>> elements = references.stream().map(Element::new).collect(Collectors.toList());
        this.reducer.reduce(this.key, elements);

        return elements.stream()
            .filter(element -> !element.isRemoved())
            .map(Element::getReference)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
