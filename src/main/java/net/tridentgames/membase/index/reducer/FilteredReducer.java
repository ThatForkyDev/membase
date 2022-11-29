package net.tridentgames.membase.index.reducer;

import java.util.List;
import java.util.function.Predicate;

import net.tridentgames.membase.index.Element;
import org.jetbrains.annotations.NotNull;

/**
 * If the predicate return true, the element will be removed from the index
 *
 * @param <K> key type
 * @param <V> value type
 */
public class FilteredReducer<K, V> implements Reducer<K, V> {
    private final Predicate<V> predicate;

    public FilteredReducer(final @NotNull Predicate<V> predicate) {
        this.predicate = predicate;
    }

    @Override
    public void reduce(final K key, final List<Element<V>> elements) {
        for (final Element<V> element : elements) {
            if (this.predicate.test(element.get())) {
                element.remove();
            }
        }
    }
}
