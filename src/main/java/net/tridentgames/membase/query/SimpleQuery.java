package net.tridentgames.membase.query;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.tridentgames.membase.query.enums.IndexOperator;
import net.tridentgames.membase.query.section.Section;
import net.tridentgames.membase.query.section.SectionOperator;
import net.tridentgames.membase.query.section.SectionPart;
import org.jetbrains.annotations.NotNull;

public final class SimpleQuery implements Query {
    private IndexOperator operator;
    private String indexName;
    private Object value;

    public @NotNull SimpleQuery contains(@NotNull String indexName, @NotNull String value) {
        this.operator = IndexOperator.CONTAINS;
        this.indexName = indexName;
        this.value = value;
        return this;
    }

    public @NotNull SimpleQuery where(@NotNull String indexName, @NotNull Object value) {
        this.operator = IndexOperator.EQUALS;
        this.indexName = indexName;
        this.value = value;
        return this;
    }

    public @NotNull SectionPart asPart() {
        return new SectionPart(this.indexName, this.value, this.operator);
    }

    @Override
    public boolean isMatch(@NotNull String indexName, @NotNull Object value) {
        return false;
    }

    @Override
    public List<Section> getSections() {
        return Collections.singletonList(this.build());
    }

    public @NotNull Section build() {
        return new Section(this.asPart(), SectionOperator.AND);
    }
}
