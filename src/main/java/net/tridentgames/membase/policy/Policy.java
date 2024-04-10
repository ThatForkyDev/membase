package net.tridentgames.membase.policy;

import java.util.function.BiPredicate;
import net.tridentgames.membase.policy.Policy.ExpirationData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Policy<V, T extends ExpirationData> {
    @NotNull String key();
    boolean checkExpiration(V value, T data);
    void onAccess(V value, T data);

    default @Nullable T createExpirationData(final V value) {
        return null;
    }

    default boolean isNullable() {
        return true;
    }

    default BiPredicate<V, T> checkExpiration() {
        return this::checkExpiration;
    }

    interface ExpirationData {}
}