import net.tridentgames.membase.query.Query;
import net.tridentgames.membase.query.section.Section;

public class StoreTests {
    public static void main(String[] args) {
        final Section section = Query.simpleQuery()
            .where("playerName", "Notch").build();

        final var advancedSections = Query.advancedQuery()
            .and(
                (query) -> query.contains("members", "Hijacking"),
                (query) -> query.where("uuid", "1234567890")
            )
            .or(
                (query) -> query.where("votes", "2"),
                (query) -> query.contains("sites", "mcsl.net")
            ).build();

        System.out.println("====== ADVANCED SECTIONS ======");
        for (final Section advancedSection : advancedSections) {
            System.out.println(advancedSection);
        }

        System.out.println("====== SIMPLE SECTION ======");
        System.out.println(section);
    }
}
