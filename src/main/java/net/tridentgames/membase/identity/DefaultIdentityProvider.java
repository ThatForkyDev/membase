package net.tridentgames.membase.identity;

/**
 * Provides identity object as self
 */
public class DefaultIdentityProvider implements IdentityProvider {
    @Override
    public Object getIdentity(final Object obj) {
        return obj;
    }
}
