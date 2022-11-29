package net.tridentgames.membase.query;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.tridentgames.membase.query.enums.IndexOperator;
import net.tridentgames.membase.query.section.Section;
import net.tridentgames.membase.query.section.SectionOperator;
import net.tridentgames.membase.query.section.SectionPart;

public final class SimpleQuery implements Query {
    private IndexOperator operator;
    private String indexName;
    private Object value;

    public SimpleQuery contains(final String indexName, final String value) {
        this.operator = IndexOperator.CONTAINS;
        this.indexName = indexName;
        this.value = value;
        return this;
    }

    public SimpleQuery where(final String indexName, final Object value) {
        this.operator = IndexOperator.EQUALS;
        this.indexName = indexName;
        this.value = value;
        return this;
    }

    public SectionPart asPart() {
        return new SectionPart(this.indexName, this.value, this.operator);
    }

    @Override
    public boolean isMatch(String indexName, Object value) {
        return false;
    }

    @Override
    public List<Section> getSections() {
        return Collections.singletonList(this.build());
    }

    public Section build() {
        return new Section(this.asPart(), SectionOperator.AND);
    }
}
