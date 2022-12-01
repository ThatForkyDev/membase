package net.tridentgames.test.modal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Person {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private String ipAddress;

    public Person() {}

    @JsonCreator
    public Person(@JsonProperty("id") final Long id, @JsonProperty("firstName") final String firstName, @JsonProperty("lastName") final String lastName, @JsonProperty("email") final String email, @JsonProperty("gender") final Gender gender, @JsonProperty("ipAddress") final String ipAddress) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.ipAddress = ipAddress;
    }

    public Long getId()
    {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(final Gender gender) {
        this.gender = gender;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "Person{"
               + "id=" + this.id
               + ", firstName='" + this.firstName + '\''
               + ", lastName='" + this.lastName + '\''
               + ", email='" + this.email + '\''
               + ", gender=" + this.gender
               + ", ipAddress='" + this.ipAddress + '\''
               + '}';
    }
}