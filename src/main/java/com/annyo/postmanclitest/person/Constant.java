package com.annyo.postmanclitest.person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constant {

    public static List<Person> PERSONS = new ArrayList<>(Arrays.asList(
            new Person(1, "John", "Doe", 25),
            new Person(2, "Alice", "Bowman", 30),
            new Person(3, "Bob", "Obo", 35)
    ));

    public static List<Person> getPersons() {
        return PERSONS;
    }

    public static Person addPerson(Person person) {
        person.setId(PERSONS.size() + 1);
        PERSONS.add(person);
        return person;
    }

    public static Person removePerson(int id) {
        Person person = PERSONS.get(id - 1);
        PERSONS.remove(person);
        return person;
    }
}
