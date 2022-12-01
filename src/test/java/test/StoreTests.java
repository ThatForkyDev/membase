package test;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import net.tridentgames.membase.Store;
import net.tridentgames.membase.policy.type.TimedExpiringPolicy;
import net.tridentgames.membase.type.expiring.ExpiringMemoryStore;
import net.tridentgames.membase.type.expiring.ExpiringStore;
import net.tridentgames.membase.type.memory.MemoryStore;
import net.tridentgames.membase.query.Query;
import net.tridentgames.membase.query.section.Section;

public class StoreTests {
    public static void main(String[] args) {
        System.out.println("====== TEST LOGIC ======");
        testLogic();

        System.out.println("\n\n\n====== TEST SIMPLE GET ======");
        testSimpleGet();

        System.out.println("\n\n\n====== TEST ADVANCED SET ======");
        testAdvancedGet();

        System.out.println("\n\n\n====== TEST EXPIRING STORE ======");
        testExpiringStore();
    }

    public static void testAdvancedGet() {
        final Store<Person> store = new MemoryStore<>();
        store.index("firstName", Person::getFirstName);
        store.index("lastName", Person::getLastName);
        store.index("age", Person::getAge);

        store.add(new Person("John", "Doe", 21));
        store.add(new Person("Jane", "Doe", 21));

        final List<Person> result = store.get(Query.advancedQuery().or(
            simpleQuery -> simpleQuery.where("firstName", "Paul"),
            simpleQuery -> simpleQuery.where("lastName", "Doe"),
            simpleQuery -> simpleQuery.where("age", 25)
        ));

        for (final Person person : result) {
            System.out.println(person);
        }

        final Person result2 = store.getFirst(Query.simpleQuery().where("firstName", "Paul"));

        if (Objects.isNull(result)) {
            System.err.println("John is null");
        }

        System.out.println(result);

        if (Objects.isNull(result2)) {
            System.err.println("Paul is null");
        }

        System.out.println(result2);
    }

    public static void testSimpleGet() {
        final Store<Person> store = new MemoryStore<>();
        store.index("firstName", Person::getFirstName);

        store.add(new Person("John", "Doe", 21));

        final Person result = store.getFirst(Query.simpleQuery().where("firstName", "John"));
        final Person result2 = store.getFirst(Query.simpleQuery().where("firstName", "Paul"));

        if (Objects.isNull(result)) {
            System.err.println("John is null");
        }

        System.out.println(result);

        if (Objects.isNull(result2)) {
            System.err.println("Paul is null");
        }

        System.out.println(result2);
    }

    public static void testLogic() {
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

    private static void testExpiringStore() {
        final ExpiringStore<Person> store = new ExpiringMemoryStore<>();
        store.addPolicy(TimedExpiringPolicy.of(TimeUnit.SECONDS, 1, false));
        store.add(new Person("John", "Doe", 20));

        for (final Person person : store) {
            System.out.println(person);
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (final Person person : store) {
            System.out.println(person);
        }
    }

    public static class Person {
        private final String firstName;
        private final String lastName;
        private final int age;

        public Person(String firstName, String lastName, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        public String getFirstName() {
            return this.firstName;
        }

        public String getLastName() {
            return this.lastName;
        }

        public int getAge() {
            return this.age;
        }

        @Override
        public String toString() {
            return "Person{" +
                   "firstName='" + this.firstName + '\'' +
                   ", lastName='" + this.lastName + '\'' +
                   ", age=" + this.age +
                   '}';
        }
    }


}
