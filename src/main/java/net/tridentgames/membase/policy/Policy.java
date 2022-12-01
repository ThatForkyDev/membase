package net.tridentgames.membase.policy;

import java.util.function.BiPredicate;
import net.tridentgames.membase.policy.Policy.ExpirationData;

public interface Policy<V, T extends ExpirationData> {
    String key();
    boolean checkExpiration(V value, T data);
    void onAccess(V value, T data);

    default T createExpirationData(final V value) {
        return null;
    }

    default BiPredicate<V, T> checkExpiration() {
        return this::checkExpiration;
    }

    interface ExpirationData {}
}