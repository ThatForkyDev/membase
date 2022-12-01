package net.tridentgames.membase.index.comparison.string;

import java.util.Locale;

import net.tridentgames.membase.index.comparison.ComparisonPolicy;
import org.jetbrains.annotations.NotNull;

/**
 * Comparison policy for comparing two string elements regardless of case.
 */
public class CaseInsensitiveComparisonPolicy implements ComparisonPolicy<String> {
    @Override
    public boolean supports(@NotNull Class<?> clazz) {
        return clazz == String.class;
    }

    @Override
    public @NotNull String createComparable(@NotNull String item) {
        return item.toLowerCase(Locale.getDefault());
    }
}
