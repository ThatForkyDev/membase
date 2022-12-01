package net.tridentgames.membase.query;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import net.tridentgames.membase.query.section.Section;
import net.tridentgames.membase.query.section.SectionOperator;
import net.tridentgames.membase.query.section.SectionPart;
import org.jetbrains.annotations.NotNull;

public final class AdvancedQuery implements Query {
    private final List<Section> sections = new ArrayList<>();

    @SafeVarargs
    public final @NotNull AdvancedQuery and(@NotNull UnaryOperator<SimpleQuery>... queries) {
        final List<SectionPart> parts = new ArrayList<>();

        for (final UnaryOperator<SimpleQuery> query : queries) {
            final SimpleQuery builder = query.apply(new SimpleQuery());
            parts.add(builder.asPart());
        }

        final Section section = new Section(parts, SectionOperator.AND);
        this.sections.add(section);
        return this;
    }

    @SafeVarargs
    public final @NotNull AdvancedQuery or(@NotNull UnaryOperator<SimpleQuery>... queries) {
        final List<SectionPart> parts = new ArrayList<>();

        for (final UnaryOperator<SimpleQuery> query : queries) {
            final SimpleQuery builder = query.apply(new SimpleQuery());
            parts.add(builder.asPart());
        }

        final Section section = new Section(parts, SectionOperator.OR);
        this.sections.add(section);
        return this;
    }

    @Override
    public boolean isMatch(@NotNull String indexName, @NotNull Object value) {
        return false;
    }

    @Override
    public @NotNull List<Section> getSections() {
        return this.sections;
    }

    public @NotNull List<Section> build() {
        return this.sections;
    }
}
