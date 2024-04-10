package net.tridentgames.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.tridentgames.membase.Store;
import net.tridentgames.membase.policy.type.TimedExpiringPolicy;
import net.tridentgames.membase.query.section.Section;
import net.tridentgames.membase.type.expiring.ExpiringMemoryStore;
import net.tridentgames.membase.type.expiring.ExpiringStore;
import net.tridentgames.membase.type.memory.MemoryStore;
import net.tridentgames.membase.query.Query;
import net.tridentgames.test.modal.Person;
import net.tridentgames.test.modal.SimplePerson;
import org.assertj.core.util.Sets;
import org.junit.Test;

public class StoreTests {
    @Test
    public void simpleGet() {
        final Store<SimplePerson> store = new MemoryStore<>();
        store.index("firstName", SimplePerson::getFirstName);

        store.add(new SimplePerson("John", "Doe", 21));

        final SimplePerson result = store.getFirst(Query.simpleQuery().where("firstName", "John"));
        final SimplePerson result2 = store.getFirst(Query.simpleQuery().where("firstName", "Paul"));

        assertThat(result).isNotNull();
        assertThat(result2).isNull();
    }

    @Test
    public void simpleContains() {
        final ExpiringMemoryStore<SimplePerson> store = new ExpiringMemoryStore<>();
        store.index("drinks", SimplePerson::getDrinks);
        store.index("name", SimplePerson::getFirstName);

        final Set<String> drinks = new HashSet<>();
        drinks.add("Coffee");
        store.add(new SimplePerson("John", "Doe", 21, Sets.newHashSet(drinks)));

        final SimplePerson result = store.getFirst(Query.simpleQuery().where("drinks", "Coffee"));
        System.out.println("First: " + result);



        final MemoryStore<SimplePerson> temp = new MemoryStore<>();
        temp.index("drinks", SimplePerson::getDrinks);
        temp.add(new SimplePerson("John", "Doe", 21, Sets.newHashSet(drinks)));

        final SimplePerson result2 = temp.getFirst(Query.simpleQuery().where("drinks", "Coffee"));
        System.out.println("Second: " + result2);
    }

    @Test
    public void expiringStore() {
        final ExpiringStore<SimplePerson> store = new ExpiringMemoryStore<>();
        store.addPolicy(TimedExpiringPolicy.of(TimeUnit.SECONDS, 1, false));
        store.add(new SimplePerson("John", "Doe", 20));

        assertThat(store.size()).isEqualTo(1);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThat(store.size()).isEqualTo(0);
    }

    @Test
    public void testAdvancedGet() {
        final Store<SimplePerson> store = new MemoryStore<>();
        store.index("firstName", SimplePerson::getFirstName);
        store.index("lastName", SimplePerson::getLastName);
        store.index("age", SimplePerson::getAge);

        store.add(new SimplePerson("John", "Doe", 21));
        store.add(new SimplePerson("Jane", "Doe", 21));

        // lastName is Doe so it should return both
        {
            final List<SimplePerson> result = store.get(Query.advancedQuery().or(
                    simpleQuery -> simpleQuery.where("firstName", "Paul"),
                    simpleQuery -> simpleQuery.where("lastName", "Doe"),
                    simpleQuery -> simpleQuery.where("age", 25)
            ));

            assertThat(result).hasSize(2);
        }

        // All values match
        {
            final List<SimplePerson> result = store.get(Query.advancedQuery().and(
                simpleQuery -> simpleQuery.where("firstName", "John"),
                simpleQuery -> simpleQuery.where("lastName", "Doe"),
                simpleQuery -> simpleQuery.where("age", 21)
            ));

            assertThat(result).hasSize(1);
        }

        // firstName is Paul so it should return null.
        {
            final SimplePerson result = store.getFirst(Query.simpleQuery().where("firstName", "Paul"));

            assertThat(result).isNull();
        }
    }

    @Test
    public void showLogic() {
        final Section section = Query.simpleQuery()
            .where("playerName", "Notch").build();

        final List<Section> advancedSections = Query.advancedQuery()
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
