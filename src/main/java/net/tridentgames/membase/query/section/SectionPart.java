package net.tridentgames.membase.query.section;

import net.tridentgames.membase.query.enums.IndexOperator;

public class SectionPart {
    private final String key;
    private final Object value;
    private final IndexOperator operator;

    public SectionPart(final String key, final Object value, final IndexOperator operator) {
        this.key = key;
        this.value = value;
        this.operator = operator;
    }

    public String getKey() {
        return this.key;
    }

    public Object getValue() {
        return this.value;
    }

    public IndexOperator getOperator() {
        return this.operator;
    }
}
