package net.tridentgames.test.modal;

public class SimplePerson {
    private final String firstName;
    private final String lastName;
    private final int age;

    public SimplePerson(String firstName, String lastName, int age) {
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