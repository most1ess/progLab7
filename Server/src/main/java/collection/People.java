package collection;

import person.Country;
import person.Person;

import java.time.LocalDateTime;
import java.util.TreeMap;

public class People {
    volatile private TreeMap<String, Person> collection = new TreeMap<>();
    volatile private java.time.LocalDateTime creationDate;

    public People() {
        setCreationDate();
    }

    public static Country convertToCountry(String countryString) {
        switch(countryString) {
            case "USA":
                return Country.USA;
            case "ITALY":
                return Country.ITALY;
            case "FRANCE":
                return Country.FRANCE;
            default:
                return null;
        }
    }

    public void set(TreeMap<String, Person> collection) {
        this.collection = collection;
    }

    public void setCreationDate() {
        creationDate = LocalDateTime.now().withNano(0).withSecond(0);
    }

    public TreeMap<String, Person> get() {
        return collection;
    }

    public java.time.LocalDateTime getCreationDate() {
        return creationDate;
    }

}
