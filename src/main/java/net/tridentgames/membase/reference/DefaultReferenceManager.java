package net.tridentgames.membase.reference;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import net.tridentgames.membase.identity.IdentityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation of reference manager that maintains unique references
 *
 * @param <V> value type
 */
public class DefaultReferenceManager<V> implements ReferenceManager<V> {
    private final IdentityProvider identityProvider;
    private final ReferenceFactory<V> referenceFactory;
    private final Map<Object, Reference<V>> referenceMap;

    public DefaultReferenceManager(final IdentityProvider identityProvider, final ReferenceFactory<V> referenceFactory) {
        this.identityProvider = identityProvider;
        this.referenceFactory = referenceFactory;
        this.referenceMap = new LinkedHashMap<>();
    }

    private DefaultReferenceManager(final IdentityProvider identityProvider, final ReferenceFactory<V> referenceFactory, final Map<Object, Reference<V>> referenceMap) {
        this.identityProvider = identityProvider;
        this.referenceFactory = referenceFactory;
        this.referenceMap = new LinkedHashMap<>(referenceMap);
    }

    @Override
    public Collection<Reference<V>> getReferences() {
        return this.referenceMap.values();
    }

    @Override
    public Optional<Reference<V>> findReference(final Object item) {
        final Object identity = this.identityProvider.getIdentity(item);

        if (Objects.isNull(identity)) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.referenceMap.get(identity));
    }

    @Override
    public int size() {
        return this.referenceMap.size();
    }

    @Override
    public void clear() {
        this.referenceMap.clear();
    }

    @Override
    public @Nullable Reference<V> add(final V item) {
        final Object identity = this.identityProvider.getIdentity(item);

        if (Objects.isNull(identity)) {
            return null;
        }

        final Reference<V> stored = this.referenceMap.get(identity);

        if (Objects.nonNull(stored)) {
            return stored;
        }

        final Reference<V> reference = this.referenceFactory.createReference(item);
        this.referenceMap.put(identity, reference);
        return reference;
    }

    @Override
    public @NotNull ReferenceManager<V> copy() {
        return new DefaultReferenceManager<>(this.identityProvider, this.referenceFactory, this.referenceMap);
    }

    @Override
    public @Nullable Reference<V> remove(@NotNull Object item) {
        final Object identity = this.identityProvider.getIdentity(item);

        if (Objects.isNull(identity)) {
            return null;
        }

        return this.referenceMap.remove(identity);
    }
}
