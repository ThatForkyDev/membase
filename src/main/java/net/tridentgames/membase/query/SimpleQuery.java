package net.tridentgames.membase.query;

import net.tridentgames.membase.query.enums.IndexOperator;
import net.tridentgames.membase.query.section.Section;
import net.tridentgames.membase.query.section.SectionOperator;
import net.tridentgames.membase.query.section.SectionPart;

public final class SimpleQuery implements Query {
    private IndexOperator operator;
    private String indexName;
    private String value;

    public SimpleQuery contains(final String indexName, final String value) {
        this.operator = IndexOperator.CONTAINS;
        this.indexName = indexName;
        this.value = value;
        return this;
    }

    public SimpleQuery where(final String indexName, final String value) {
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

    public Section build() {
        return new Section(this.asPart(), SectionOperator.AND);
    }
}
