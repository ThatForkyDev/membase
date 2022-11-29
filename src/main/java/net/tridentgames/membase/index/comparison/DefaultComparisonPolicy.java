package net.tridentgames.membase.index.comparison;

import org.jetbrains.annotations.NotNull;

/**
 * Apply default comparison logic
 *
 * @param <V> value type
 */
public class DefaultComparisonPolicy<V> implements ComparisonPolicy<V> {
    @Override
    public boolean supports(final Class<?> clazz) {
        return true;
    }

    @Override
    public @NotNull V createComparable(final V item) {
        return item;
    }
}
