package net.tridentgames.membase.type.expiring.thread;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.jetbrains.annotations.NotNull;

public class ExpirationThread {
    private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(1);

    public static @NotNull ScheduledThreadPoolExecutor getExecutor() {
        return EXECUTOR;
    }
}
