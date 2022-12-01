package net.tridentgames.membase.listener;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class RemovalListener<V> {
    private final Consumer<V> listener;

    public RemovalListener(@NotNull Consumer<V> listener) {
        this.listener = listener;
    }

    public @NotNull Consumer<V> getListener() {
        return this.listener;
    }
}