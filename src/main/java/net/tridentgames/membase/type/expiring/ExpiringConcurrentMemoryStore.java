package net.tridentgames.membase.type.expiring;

import java.util.List;
import net.tridentgames.membase.policy.Policy;
import net.tridentgames.membase.type.concurrent.SynchronizedStore;
import org.jetbrains.annotations.NotNull;

public class ExpiringConcurrentMemoryStore<V> extends SynchronizedStore<V> implements ExpiringStore<V> {
    private ExpiringConcurrentMemoryStore() {
        super(new ExpiringMemoryStore<>());
    }

    @Override
    public void addPolicy(@NotNull Policy<V, ?> policy) {
        final ExpiringMemoryStore<V> expiringMemoryStore = (ExpiringMemoryStore) this.store;

        synchronized (this.mutex) {
            expiringMemoryStore.addPolicy(policy);
        }
    }

    @Override
    public @NotNull List<Policy<V, ?>> getExpiryPolicies() {
        final ExpiringMemoryStore<V> expiringMemoryStore = (ExpiringMemoryStore) this.store;
        List<Policy<V, ?>> result;

        synchronized (this.mutex) {
            result = expiringMemoryStore.getExpiryPolicies();
        }

        return result;
    }

    @Override
    public void invalidate() {
        final ExpiringMemoryStore<V> expiringMemoryStore = (ExpiringMemoryStore) this.store;

        synchronized (this.mutex) {
            expiringMemoryStore.invalidate();
        }
    }
}