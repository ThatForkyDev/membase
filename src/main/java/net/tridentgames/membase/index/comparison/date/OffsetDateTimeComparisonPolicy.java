package net.tridentgames.membase.index.comparison.date;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import net.tridentgames.membase.index.comparison.ComparisonPolicy;
import org.jetbrains.annotations.NotNull;

/**
 * Comparison policy for comparing two {@link OffsetDateTime} values normalized to an UTC time offset
 */
public class OffsetDateTimeComparisonPolicy implements ComparisonPolicy<OffsetDateTime> {
    @Override
    public boolean supports(final Class<?> clazz) {
        return clazz == OffsetDateTime.class;
    }

    @Override
    public @NotNull OffsetDateTime createComparable(final OffsetDateTime offsetDateTime) {
        return offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC);
    }
}
