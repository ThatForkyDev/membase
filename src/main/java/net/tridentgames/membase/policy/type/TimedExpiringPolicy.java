package net.tridentgames.membase.policy.type;

import java.util.concurrent.TimeUnit;
import net.tridentgames.membase.policy.Policy;
import net.tridentgames.membase.policy.type.TimedExpiringPolicy.TimedExpirationData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimedExpiringPolicy<V> implements Policy<V, TimedExpirationData> {
    private final TimeUnit unit;
    private final long duration;
    private final boolean resetOnAccess;

    protected TimedExpiringPolicy(TimeUnit unit, long duration, boolean resetOnAccess) {
        this.unit = unit;
        this.duration = duration;
        this.resetOnAccess = resetOnAccess;
    }

    @Override
    public @NotNull String key() {
        return "timed";
    }

    public static <V> Policy<V, TimedExpirationData> of(TimeUnit unit, long duration, boolean resetOnAccess) {
        return new TimedExpiringPolicy<>(unit, duration, resetOnAccess);
    }

    @Override
    public boolean checkExpiration(V value, TimedExpirationData data) {
        return  System.currentTimeMillis() - data.getLastFetched() >= TimedExpiringPolicy.this.unit.toMillis(TimedExpiringPolicy.this.duration);
    }

    @Override
    public void onAccess(V value, TimedExpirationData data) {
        if (this.resetOnAccess) {
            data.setLastFetched(System.currentTimeMillis());
        }
    }

    @Override
    public @Nullable TimedExpirationData createExpirationData(final V value) {
        return new TimedExpirationData();
    }

    public long getDuration() {
        return this.duration;
    }

    public @NotNull TimeUnit getUnit() {
        return this.unit;
    }

    public static class TimedExpirationData implements ExpirationData {
        private long lastFetched;

        public TimedExpirationData() {
            this.lastFetched = System.currentTimeMillis();
        }

        public void setLastFetched(long lastFetched) {
            this.lastFetched = lastFetched;
        }

        public long getLastFetched() {
            return this.lastFetched;
        }
    }
}
