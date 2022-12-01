package net.tridentgames.membase.index;

import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Index applied to a {@link net.tridentgames.membase.Store}
 *
 * @param <V> value type
 */
public interface Index<V> {
    /**
     * Get first indexed item matching key. This is the same as {@link Index#findFirst(Object)}, but returns an null instead of an optional if no result found.
     *
     * @param key indexed key to lookup
     * @return optional
     */
    default @Nullable V getFirst(final Object key) {
        return this.findFirst(key).orElse(null);
    }

    /**
     * Find first indexed item matching key. This is the same as {@link Index#getFirst(Object)}, but returns an optional instead of a null if no result found.
     *
     * @param key indexed key to lookup
     * @return optional
     */
    @NotNull Optional<V> findFirst(final Object key);

    /**
     * Find all indexed items matching key
     *
     * @param key indexed key to lookup
     * @return matching items
     */
    List<V> get(final Object key);

    /**
     * Get name of index
     *
     * @return index names
     */
    String getName();
}
