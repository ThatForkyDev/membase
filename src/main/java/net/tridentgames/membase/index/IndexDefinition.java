package net.tridentgames.membase.index;

import java.util.Collection;
import java.util.Collections;

import java.util.Objects;
import net.tridentgames.membase.index.comparison.ComparisonPolicy;
import net.tridentgames.membase.index.comparison.DefaultComparisonPolicy;
import net.tridentgames.membase.index.reducer.Reducer;
import org.jetbrains.annotations.NotNull;

/**
 * Definition of an index to be created
 *
 * @param <K> key type
 * @param <V> value type
 */
public final class IndexDefinition<K, V> {
    private final KeyMapper<Collection<K>, V> keyMapper;
    private ComparisonPolicy<K> comparisonPolicy;
    private Reducer<K, V> reducer;

    private IndexDefinition(final KeyMapper<Collection<K>, V> keyMapper) {
        this.keyMapper = keyMapper;
        this.comparisonPolicy = new DefaultComparisonPolicy<>();
        this.reducer = null;
    }

    /**
     * Specify a function that reduces multiple values that map to the same key.
     *
     * @param reducer reduction function
     * @return index build
     */
    public IndexDefinition<K, V> withReducer(final Reducer<K, V> reducer) {
        this.reducer = reducer;
        return this;
    }

    /**
     * Define the policy for mapping and comparing indexed keys. For example, we can specify a
     * {@link net.tridentgames.membase.index.comparison.string.CaseInsensitiveComparisonPolicy} when we want store and compare
     * indexes insensitive of case.
     *
     * @param comparisonPolicy policy to define how indexed keys are compared
     * @return index build
     */
    public IndexDefinition<K, V> withComparisonPolicy(final ComparisonPolicy<K> comparisonPolicy) {
        this.comparisonPolicy = comparisonPolicy;
        return this;
    }

    /**
     * Specify a function that maps a given value to a single indexed key. For example, we can index a Person object by
     * its firstName field. Example: IndexDefinition.withKeyMapping(Person::getFirstName) <br><br>
     *
     * To map a value to multiple keys, see {@link IndexDefinition#withKeyMappings(KeyMapper)}
     *
     * @param keyMapper a function that maps a given value to a single indexed key. Note: If the mapper returns a null, indexing will be skipped for the given value.
     * @param <K>       key type
     * @param <V>       value type
     * @return index build
     */
    public static @NotNull <K, V> IndexDefinition<K, V> withKeyMapping(final KeyMapper<K, V> keyMapper) {
        return withKeyMappings(value -> {
            final K key = keyMapper.map(value);
            return Objects.isNull(key) ? Collections.emptyList() : Collections.singletonList(key);
        });
    }

    /**
     * Specify a function that maps a given value to a one or more indexed keys. For example, we can index a Person object by
     * both its firstName and lastName fields. Example: IndexDefinition.withKeyMapping(person -&gt; Arrays.asList(person.getFirstName(), person.getLastName()))
     *
     * @param mapper a function that maps a given value to one or more indexed keys. Note: any null values returned in the collection will be ignored
     * @param <K>    key type
     * @param <V>    value type
     * @return index build
     */
    public static <K, V> IndexDefinition<K, V> withKeyMappings(final KeyMapper<Collection<K>, V> mapper) {
        return new IndexDefinition<>(mapper);
    }

    KeyMapper<Collection<K>, V> getKeyMapper() {
        return this.keyMapper;
    }

    ComparisonPolicy<K> getComparisonPolicy() {
        return this.comparisonPolicy;
    }

    Reducer<K, V> getReducer() {
        return this.reducer;
    }
}
