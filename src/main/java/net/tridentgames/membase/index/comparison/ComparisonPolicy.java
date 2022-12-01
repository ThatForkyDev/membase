package net.tridentgames.membase.index.comparison;

import org.jetbrains.annotations.NotNull;

/**
 * Transforms a given key into a comparable key.
 *
 * @param <T> type
 */
public interface ComparisonPolicy<T> {
    /**
     * Returns true if comparison is supported for this class type
     *
     * @param clazz class to check for support
     * @return true if supported
     */
    boolean supports(@NotNull Class<?> clazz);

    /**
     * Transform the given item into the comparable type
     *
     * @param item item
     * @return comparable
     */
    @NotNull T createComparable(T item);
}
