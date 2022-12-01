package net.tridentgames.membase.query.section;

import net.tridentgames.membase.query.enums.IndexOperator;
import org.jetbrains.annotations.NotNull;

public class SectionPart {
    private final String key;
    private final Object value;
    private final IndexOperator operator;

    public SectionPart(@NotNull String key, @NotNull Object value, @NotNull IndexOperator operator) {
        this.key = key;
        this.value = value;
        this.operator = operator;
    }

    public @NotNull String getKey() {
        return this.key;
    }

    public @NotNull Object getValue() {
        return this.value;
    }

    public @NotNull IndexOperator getOperator() {
        return this.operator;
    }
}
