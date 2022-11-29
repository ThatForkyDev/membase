package net.tridentgames.membase.query;

public class QueryClause {
    private final String indexName;
    private final Object key;
    private final Operator operator;

    public QueryClause(final String indexName, final Object key, final Operator operator) {
        this.indexName = indexName;
        this.key = key;
        this.operator = operator;
    }

    public String getIndexName() {
        return this.indexName;
    }

    public Object getKey() {
        return this.key;
    }

    public Operator getOperator() {
        return this.operator;
    }
}
