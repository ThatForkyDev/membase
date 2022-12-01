package net.tridentgames.membase.type.expiring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.tridentgames.membase.AbstractStore;
import net.tridentgames.membase.Store;
import net.tridentgames.membase.identity.DefaultIdentityProvider;
import net.tridentgames.membase.index.IndexManager;
import net.tridentgames.membase.index.ReferenceIndexManager;
import net.tridentgames.membase.listener.RemovalListener;
import net.tridentgames.membase.listener.enums.RemovalType;
import net.tridentgames.membase.memory.MemoryReferenceFactory;
import net.tridentgames.membase.policy.type.TimedExpiringPolicy;
import net.tridentgames.membase.query.Query;
import net.tridentgames.membase.query.section.Section;
import net.tridentgames.membase.query.section.SectionOperator;
import net.tridentgames.membase.query.section.SectionPart;
import net.tridentgames.membase.reference.DefaultReferenceManager;
import net.tridentgames.membase.reference.Reference;
import net.tridentgames.membase.reference.ReferenceManager;
import net.tridentgames.membase.policy.Policy;
import net.tridentgames.membase.policy.Policy.ExpirationData;
import net.tridentgames.membase.type.expiring.thread.ExpirationThread;
import org.jetbrains.annotations.NotNull;

public class ExpiringMemoryStore<V> extends AbstractStore<V> implements ExpiringStore<V> {
    private final List<Policy<V, ? extends ExpirationData>> policies = new ArrayList<>();
    private final Map<RemovalType, Set<RemovalListener<V>>> removalListeners;
    private final Map<Policy<V, ?>, Map<V, ? extends ExpirationData>> policyData;

    private ExpiringMemoryStore(@NotNull final ReferenceManager<V> referenceManager, @NotNull final IndexManager<V> indexManager, @NotNull final Map<RemovalType, Set<RemovalListener<V>>> removalListeners, @NotNull Map<Policy<V, ?>, Map<V, ? extends ExpirationData>> policyData) {
        super(referenceManager, indexManager);

        this.removalListeners = removalListeners;
        this.policyData = policyData;
    }

    public ExpiringMemoryStore() {
        this(new DefaultReferenceManager<>(new DefaultIdentityProvider(), new MemoryReferenceFactory<>()), new ReferenceIndexManager<>(), new HashMap<>(), new HashMap<>());
    }

    @Override
    public boolean add(final V item) {
        final boolean added = super.add(item);

        if (!added) {
            this.getRemovalListeners()
                .getOrDefault(RemovalType.REMOVED, Collections.emptySet())
                .forEach(listener -> listener.getListener().accept(item));
        }

        return added;
    }

    @Override
    public Map<RemovalType, Set<RemovalListener<V>>> getRemovalListeners() {
        return this.removalListeners;
    }

    @Override
    protected Store<V> createCopy(final ReferenceManager<V> referenceManager, final IndexManager<V> indexManager) {
        return new ExpiringMemoryStore<>(referenceManager.copy(), indexManager.copy(), new HashMap<>(this.removalListeners), new HashMap<>(this.policyData));
    }

    @Override
    public List<V> get(@NotNull final Query query, @NotNull final Integer limit) {
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

                references.removeIf(reference -> {
                    final boolean invalidate = this.checkExpiration(reference.get());

                    if (invalidate) {
                        this.invalidate(reference.get());
                    }

                    return invalidate;
                });
            }
        }

        return results.stream()
            .map(Reference::get)
            .limit(limit == -1 ? Long.MAX_VALUE : limit)
            .peek(reference -> {
                for (final Policy policy : this.policies) {
                    policy.onAccess(reference, this.policyData.get(policy).get(reference));
                }
            }).collect(Collectors.toList());
    }

    @Override
    public void addPolicy(Policy<V, ?> policy) {
        this.policies.add(policy);

        if (policy instanceof TimedExpiringPolicy timedExpiringPolicy) {
            final TimeUnit timeUnit = timedExpiringPolicy.getUnit();
            final long duration = timedExpiringPolicy.getDuration();

            ExpirationThread.getExecutor().schedule(() -> {
                this.invalidate();
            }, duration, timeUnit);
        }
    }

    @Override
    public @NotNull List<Policy<V, ?>> getExpiryPolicies() {
        return this.policies;
    }

    @Override
    public boolean remove(final Object obj) {
        final boolean removed = super.remove(obj);
        if (removed) {
            this.getRemovalListeners()
                .getOrDefault(RemovalType.REMOVED, Collections.emptySet())
                .forEach(listener -> listener.getListener().accept((V) obj));
        }

        return removed;
    }

    @Override
    public Store<V> synchronizedStore() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidate() {
        for (final V item : this) {
            final boolean invalidate = this.checkExpiration(item);

            if (invalidate) {
                this.invalidate(item);
            }

            this.invalidate(item);
        }
    }

    public void invalidate(final V obj) {
        final boolean removed = super.remove(obj);

        if (removed) {
            this.getRemovalListeners()
                .getOrDefault(RemovalType.EXPIRED, Collections.emptySet())
                .forEach(listener -> listener.getListener().accept(obj));
        }
    }

    public boolean checkExpiration(final V value) {
        if (this.policyData.isEmpty()) {
            return false;
        }

        boolean invalidate = true;

        for (final Policy policy : this.policies) {
            final ExpirationData data = this.policyData.get(policy).get(value);
            if (data == null) {
                continue;
            }

            if (!policy.checkExpiration(value, data)) {
                invalidate = false;
                break;
            }
        }

        return invalidate;
    }
}