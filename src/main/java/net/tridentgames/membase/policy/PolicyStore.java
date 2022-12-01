package net.tridentgames.membase.policy;

import java.util.List;
import net.tridentgames.membase.Store;
import org.jetbrains.annotations.NotNull;

public interface PolicyStore<V> extends Store<V> {
    void addPolicy(@NotNull Policy<V, ?> policy);
    @NotNull List<Policy<V, ?>> getExpiryPolicies();
}
