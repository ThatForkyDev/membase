package net.tridentgames.membase.index.reducer;

import java.util.List;

import net.tridentgames.membase.index.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Reduces elements a key once the configured limit has been reached
 *
 * @param <K> key type
 * @param <V> value type
 */
public class LimitReducer<K, V> implements Reducer<K, V> {
    private final int limit;
    private final Retain retain;

    public LimitReducer(int limit, @NotNull Retain retain) {
        this.limit = limit;
        this.retain = retain;
    }

    @Override
    public void reduce(final K key, @NotNull List<Element<V>> elements) {
        if (elements.size() <= this.limit) {
            return;
        }

        if (this.retain == Retain.OLDEST) {
            this.reduceOldest(elements);
        } else if (this.retain == Retain.NEWEST) {
            this.reduceNewest(elements);
        }
    }

    private void reduceNewest(@NotNull List<Element<V>> elements) {
        for (int i = 0; i < (elements.size() - this.limit); i++) {
            elements.get(i).remove();
        }
    }

    private void reduceOldest(@NotNull List<Element<V>> elements) {
        for (int i = this.limit; i < elements.size(); i++) {
            elements.get(i).remove();
        }
    }

    public enum Retain {
        NEWEST,
        OLDEST
    }
}
