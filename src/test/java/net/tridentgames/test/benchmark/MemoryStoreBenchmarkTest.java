package net.tridentgames.test.benchmark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import net.tridentgames.test.modal.Person;
import net.tridentgames.membase.Store;
import net.tridentgames.membase.index.Index;
import net.tridentgames.membase.type.memory.MemoryStore;
import net.tridentgames.util.JsonTestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryStoreBenchmarkTest {
    private static final List<Person> TEST_DATA = Arrays.asList(JsonTestUtils.loadResource("/data.json", Person[].class));

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();
    private List<Person> shuffledTestData;

    @Before
    public void setUp() {
        this.shuffledTestData = new ArrayList<>(TEST_DATA);
        Collections.shuffle(this.shuffledTestData);
    }

    @BenchmarkOptions(benchmarkRounds = 500, warmupRounds = 5)
    @Test
    public void memoryStoreBenchmark() {
        final Store<Person> store = new MemoryStore<>(this.shuffledTestData);
        final Index<Person> index = store.index(Person::getFirstName);

        for (final Person person : TEST_DATA) {
            final List<Person> results = index.get(person.getFirstName());
            assertThat(results).contains(person);
        }
    }

    @BenchmarkOptions(benchmarkRounds = 500, warmupRounds = 5)
    @Test
    public void arrayListBenchmark() {
        for (final Person person : TEST_DATA) {
            final List<Person> results = shuffledTestData.stream()
                .filter(p -> p.getFirstName().equals(person.getFirstName()))
                .collect(Collectors.toList());

            assertThat(results).contains(person);
        }
    }
}