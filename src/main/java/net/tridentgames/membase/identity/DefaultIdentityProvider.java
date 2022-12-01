package net.tridentgames.membase.identity;

import org.jetbrains.annotations.Nullable;

/**
 * Provides identity object as self
 */
public class DefaultIdentityProvider implements IdentityProvider {
    @Override
    public @Nullable Object getIdentity(final Object obj) {
        return obj;
    }
}
