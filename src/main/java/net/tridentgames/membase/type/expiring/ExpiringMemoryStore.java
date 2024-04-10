package net.tridentgames.membase.type.expiring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.jetbrains.annotations.Nullable;

public class ExpiringMemoryStore<V> extends AbstractStore<V> implements ExpiringStore<V> {
    private final List<Policy<V, ? extends ExpirationData>> policies = new ArrayList<>();
    private final Map<RemovalType, Set<RemovalListener<V>>> removalListeners;
    private final Map<Policy<V, ?>, Map<V, ? extends ExpirationData>> policyData;
    private boolean debug = false;

    private ExpiringMemoryStore(@NotNull ReferenceManager<V> referenceManager, @NotNull IndexManager<V> indexManager, @NotNull Map<RemovalType, Set<RemovalListener<V>>> removalListeners, @NotNull Map<Policy<V, ?>, Map<V, ? extends ExpirationData>> policyData) {
        super(referenceManager, indexManager);

        this.removalListeners = removalListeners;
        this.policyData = policyData;
    }

    public ExpiringMemoryStore() {
        this(new DefaultReferenceManager<>(new DefaultIdentityProvider(), new MemoryReferenceFactory<>()), new ReferenceIndexManager<>(), new HashMap<>(), new HashMap<>());
    }

    public void policyCreate(final V value) {
        for (final Entry<Policy<V, ?>, Map<V, ? extends ExpirationData>> entry : this.policyData.entrySet()) {
            final ExpirationData data = entry.getKey().createExpirationData(value);

            if (data == null) {
                continue;
            }

            final Map map = entry.getValue();
            map.put(value, data);
        }
    }

    @Override
    public boolean add(final V item) {
        final boolean added = super.add(item);

        if (!added) {
            this.getRemovalListeners()
                .getOrDefault(RemovalType.REMOVED, Collections.emptySet())
                .forEach(listener -> listener.getListener().accept(item));
        } else {
            this.policyCreate(item);
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
    public List<V> get(@NotNull final Query query, @Nullable final Integer limit) {
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
            .limit(limit == null || limit == -1 ? Long.MAX_VALUE : limit)
            .peek(reference -> {
                for (final Policy policy : this.policies) {
                    final ExpirationData data = this.policyData.get(policy).get(reference);

                    if (data == null && !policy.isNullable()) {
                        final ExpirationData create = policy.createExpirationData(reference);
                        final Map map = this.policyData.get(policy);
                        map.put(reference, create);

                        policy.onAccess(reference, create);
                        continue;
                    }

                    policy.onAccess(reference, this.policyData.get(policy).get(reference));
                }
            }).collect(Collectors.toList());
    }

    @Override
    public void addPolicy(@NotNull Policy<V, ?> policy) {
        this.policies.add(policy);
        this.policyData.put(policy, new HashMap<>());

        if (policy instanceof TimedExpiringPolicy) {
            final TimedExpiringPolicy timedExpiringPolicy = (TimedExpiringPolicy) policy;

            final TimeUnit timeUnit = timedExpiringPolicy.getUnit();
            final long duration = timedExpiringPolicy.getDuration();

            ExpirationThread.getExecutor().scheduleAtFixedRate(() -> {
                this.invalidate();
            }, duration, duration, timeUnit);
        }
    }

    @Override
    public @NotNull List<Policy<V, ?>> getExpiryPolicies() {
        return this.policies;
    }

    @Override
    public boolean remove(@Nullable Object obj) {
        final boolean removed = super.remove(obj);
        if (removed) {
            this.getRemovalListeners()
                .getOrDefault(RemovalType.REMOVED, Collections.emptySet())
                .forEach(listener -> listener.getListener().accept((V) obj));

            for (final Entry<Policy<V, ?>, Map<V, ? extends ExpirationData>> policyData : this.policyData.entrySet()) {
                policyData.getValue().remove(obj);
            }
        }

        return removed;
    }

    @Override
    public Store<V> synchronizedStore() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidate() {
        final List<V> invalidations = new ArrayList<>();

        for (final V item : this) {
            final boolean invalidate = this.checkExpiration(item);

            if (invalidate) {
                invalidations.add(item);
            }
        }

        if (this.debug) {
            System.out.println("Invalidating " + invalidations.size() + " items...");
        }

        for (final V invalidation : invalidations) {
            this.invalidate(invalidation);
        }
    }

    public void invalidate(final V obj) {
        final boolean removed = super.remove(obj);

        if (removed) {
            if (this.debug) {
                System.out.println("Successfully removed " + obj);
            }

            this.getRemovalListeners()
                .getOrDefault(RemovalType.EXPIRED, Collections.emptySet())
                .forEach(listener -> listener.getListener().accept(obj));
        }
    }

    public boolean checkExpiration(final V value) {
        if (this.policyData.isEmpty()) {
            if (this.debug) {
                System.out.println("No policies exist...");
            }

            return false;
        }

        boolean invalidate = true;

        for (final Policy policy : this.policies) {
            final ExpirationData data = this.policyData.get(policy).get(value);

            if (this.debug) {
                System.out.println("Checking " + policy.key());
            }

            if (!policy.checkExpiration(value, data)) {
                invalidate = false;

                if (this.debug) {
                    System.out.println(policy.key() + " says no");
                }
                break;
            } else {
                if (this.debug) {
                    System.out.println(policy.key() + " says yes");
                }
            }
        }

        return invalidate;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}