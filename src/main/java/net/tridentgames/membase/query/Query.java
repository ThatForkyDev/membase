package net.tridentgames.membase.query;

import java.util.List;
import net.tridentgames.membase.query.section.Section;
import org.jetbrains.annotations.NotNull;

public sealed interface Query permits SimpleQuery, AdvancedQuery {
    static @NotNull SimpleQuery simpleQuery() {
        return new SimpleQuery();
    }

    static @NotNull AdvancedQuery advancedQuery() {
        return new AdvancedQuery();
    }

    boolean isMatch(final String indexName, Object value);

    List<Section> getSections();
}
