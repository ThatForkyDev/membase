package net.tridentgames.membase.query.section;

import java.util.Collections;
import java.util.List;

public class Section {
    private final List<SectionPart> parts;
    private final SectionOperator operator;

    public Section(final SectionPart part, final SectionOperator operator) {
        this(Collections.singletonList(part), operator);
    }

    public Section(final List<SectionPart> parts, final SectionOperator operator) {
        this.parts = parts;
        this.operator = operator;
    }

    public List<SectionPart> getParts() {
        return this.parts;
    }

    public SectionOperator getOperator() {
        return this.operator;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (final SectionPart part : this.parts) {
            builder
                .append(part.getKey())
                .append(" should ")
                .append(part.getOperator().name())
                .append(" ")
                .append(part.getValue())
                .append(" ")
                .append(this.operator.name())
                .append(" ");
        }

        return builder.toString();
    }
}
