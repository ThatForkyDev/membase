package net.tridentgames.membase.identity;

import org.jetbrains.annotations.Nullable;

/**
 * Provides an identity for an object
 */
public interface IdentityProvider {
    /**
     * Get object identity
     *
     * @param obj object
     * @return identity
     */
    @Nullable Object getIdentity(Object obj);
}
