package net.tridentgames.membase.query;

import java.util.List;
import net.tridentgames.membase.query.section.Section;
import org.jetbrains.annotations.NotNull;

public interface Query {
    static @NotNull SimpleQuery simpleQuery() {
        return new SimpleQuery();
    }

    static @NotNull AdvancedQuery advancedQuery() {
        return new AdvancedQuery();
    }

    boolean isMatch(@NotNull String indexName, @NotNull Object value);

    List<Section> getSections();
}
