package net.tridentgames.test.modal;

import java.util.Set;
import org.assertj.core.util.Sets;

public class SimplePerson {
    private final String firstName;
    private final String lastName;
    private final int age;
    private final Set<String> drinks;

    public SimplePerson(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.drinks = Sets.newHashSet();
    }

    public SimplePerson(String firstName, String lastName, int age, Set<String> drinks) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.drinks = drinks;
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

    public Set<String> getDrinks() {
        return this.drinks;
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