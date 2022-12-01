package net.tridentgames.membase.type.expiring.thread;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ExpirationThread {
    private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(1);

    public static ScheduledThreadPoolExecutor getExecutor() {
        return EXECUTOR;
    }
}
