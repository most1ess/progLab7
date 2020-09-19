package command;

import person.Person;
import server.Database;
import server.Processor;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Clear extends Command {
    private TreeMap<String, Person> collection;
    private Database database;
    private String login;

    public Clear(Processor processor) {
        collection = processor.getCollection().get();
        database = processor.getDatabase();
        login = processor.getCommandData().getLogin();
    }

    @Override
    public synchronized String execute() {
        if (collection.isEmpty()) {
            return "Невозможно очистить коллекцию. Коллекция уже пуста.\n";
        } else {
            if (database.clear()) {
                collection.values().removeIf(person -> person.getLogin().equals(login));
                return "Коллекция успешно очищена.\n";
            } else {
                return "При очистке коллекции возникла ошибка.\n";
            }
        }
    }
}
