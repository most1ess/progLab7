package command;

import person.Person;

import java.io.Serializable;

public class CommandData implements Serializable {
    private static final long serialVersionUID = 1488L;
    private String name;
    private String param1;
    private String param2;
    private Person person;

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    private String hashedPassword;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    private String login;

    public CommandData(String name) {
        this.name = name;
    }

    public CommandData(String name, String param1) {
        this.name = name;
        this.param1 = param1;
    }

    public CommandData(String name, String param1, String param2) {
        this.name = name;
        this.param1 = param1;
        this.param2 = param2;
    }

    public CommandData(String name, String param1, Person person) {
        this.name = name;
        this.param1 = param1;
        this.person = person;
    }
}
