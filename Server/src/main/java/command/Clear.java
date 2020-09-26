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
    private String result;
    private Processor processor;

    public Clear(Processor processor) {
        synchronized (Processor.synchronizer) {
            collection = processor.getCollection().get();
        }
        database = processor.getDatabase();
        login = processor.getCommandData().getLogin();
        this.processor = processor;
    }

    @Override
    public String execute() {
        synchronized (Processor.synchronizer) {
            if (collection.isEmpty()) {
                result = "Невозможно очистить коллекцию. Коллекция уже пуста.\n";
            } else {
                if (database.clear()) {
                    collection.values().removeIf(person -> person.getLogin().equals(login));
                    result = "Коллекция успешно очищена.\n";
                } else {
                    result = "При очистке коллекции возникла ошибка.\n";
                }
            }
        }
        processor.setResult(result);
        return result;
    }
}
