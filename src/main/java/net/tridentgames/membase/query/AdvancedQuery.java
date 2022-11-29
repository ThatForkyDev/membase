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
    public final AdvancedQuery and(@NotNull final UnaryOperator<SimpleQuery>... queries) {
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
    public final AdvancedQuery or(@NotNull final UnaryOperator<SimpleQuery>... queries) {
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
    public boolean isMatch(String indexName, Object value) {
        return false;
    }

    public List<Section> build() {
        return this.sections;
    }
}
