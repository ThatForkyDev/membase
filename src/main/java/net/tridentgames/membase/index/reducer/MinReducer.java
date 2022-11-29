package net.tridentgames.membase.index.reducer;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

/**
 * Reduces all elements for a key retaining the min value
 *
 * @param <K> key type
 * @param <V> value type
 */
public non-sealed class MinReducer<K, V> extends ComparingReducer<K, V> {
    public <C extends Comparable<? super C>> MinReducer(final Function<V, C> valueProvider, final boolean nullGreater) {
        super(valueProvider, nullGreater);
    }

    public <C> MinReducer(final Function<V, C> valueProvider, final Comparator<C> comparator, final boolean nullGreater) {
        super(valueProvider, comparator, nullGreater);
    }

    @Override
    protected int compare(final Object value1, final Object value2, final Comparator<Object> comparator, final boolean nullGreater) {
        if (value1 == value2) {
            return 0;
        }

        if (Objects.isNull(value1)) {
            return nullGreater ? -1 : 1;
        }

        if (Objects.isNull(value2)) {
            return nullGreater ? 1 : -1;
        }

        return comparator.compare(value2, value1);
    }
}