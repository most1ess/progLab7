package command;

import client.Processor;
import person.Person;

import java.io.Serializable;
import java.net.SocketAddress;

public class CommandData implements Serializable {
    private static final long serialVersionUID = 1488L;
    private String name;
    private String param1;
    private String param2;
    private Person person;

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    private SocketAddress socketAddress;

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

    public CommandData(String name, Processor processor) {
        this.name = name;
        this.socketAddress = processor.getSocketAddress();
    }

    public CommandData(String name, String param1, Processor processor) {
        this.name = name;
        this.param1 = param1;
        this.socketAddress = processor.getSocketAddress();
    }

    public CommandData(String name, String param1, String param2, Processor processor) {
        this.name = name;
        this.param1 = param1;
        this.param2 = param2;
        this.socketAddress = processor.getSocketAddress();
    }

    public CommandData(String name, String param1, Person person, Processor processor) {
        this.name = name;
        this.param1 = param1;
        this.person = person;
        this.socketAddress = processor.getSocketAddress();
    }
}
