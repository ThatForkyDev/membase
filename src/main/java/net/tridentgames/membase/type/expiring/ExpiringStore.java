package net.tridentgames.membase.type.expiring;

import net.tridentgames.membase.policy.PolicyStore;

public interface ExpiringStore<V> extends PolicyStore<V> {
    void invalidate();
}
