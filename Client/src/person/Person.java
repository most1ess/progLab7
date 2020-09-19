package person;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Person implements Comparable<Person>, Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int height; //Значение поля должно быть больше 0
    private java.time.LocalDateTime birthday; //Поле может быть null
    private long weight; //Значение поля должно быть больше 0
    private Country nationality; //Поле не может быть null
    private Location location; //Поле может быть null
    private String login;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCreationDate(String creationDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.creationDate = this.creationDate.parse(creationDate, formatter);
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setBirthday(String birthday) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        if(birthday == null) {
            this.birthday = null;
        } else {
            this.birthday = this.birthday.parse(birthday, formatter);
        }
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public int getHeight() {
        return height;
    }

    public LocalDateTime getBirthday() {
        return birthday;
    }

    public long getWeight() {
        return weight;
    }

    public Country getNationality() {
        return nationality;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public int compareTo(Person p) {
        if(this.getLocation() == null) {
            return Integer.MIN_VALUE;
        } else {
            return (this.getLocation().compareTo(p.getLocation()));
        }
    }
}
