package net.tridentgames.membase.index.reducer;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import net.tridentgames.membase.index.Element;

@SuppressWarnings("unchecked")
public abstract sealed class ComparingReducer<K, V> implements Reducer<K, V> permits MinReducer, MaxReducer {
    private final Function<V, ?> valueProvider;
    private final Comparator<Object> comparator;
    private final boolean nullGreater;

    protected <C extends Comparable<? super C>> ComparingReducer(final Function<V, C> valueProvider, final boolean nullGreater) {
        this.valueProvider = valueProvider;
        this.comparator = Comparator.comparing(obj -> ((C) obj));
        this.nullGreater = nullGreater;
    }

    protected <C> ComparingReducer(final Function<V, C> valueProvider, final Comparator<C> comparator, final boolean nullGreater) {
        this.valueProvider = valueProvider;
        this.comparator = (Comparator<Object>) comparator;
        this.nullGreater = nullGreater;
    }

    @Override
    public void reduce(final K key, final List<Element<V>> elements) {
        elements.stream().reduce(this::reduce);
    }

    private Element<V> reduce(final Element<V> element1, final Element<V> element2) {
        final Object comparable1 = this.valueProvider.apply(element1.get());
        final Object comparable2 = this.valueProvider.apply(element2.get());

        if (this.compare(comparable1, comparable2, this.comparator, this.nullGreater) > 0) {
            element2.remove();
            return element1;
        }

        element1.remove();
        return element2;
    }

    protected abstract int compare(Object value1, Object value2, Comparator<Object> comparator, boolean nullGreater);
}
