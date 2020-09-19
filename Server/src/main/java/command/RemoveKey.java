package command;

import person.Person;
import server.Database;
import server.Processor;

import java.util.TreeMap;

public class RemoveKey extends Command {
    private TreeMap<String, Person> collection;
    private String key;
    private Database database;
    private String login;

    public RemoveKey(Processor processor) {
        collection = processor.getCollection().get();
        key = processor.getCommandData().getParam1();
        database = processor.getDatabase();
        login = processor.getCommandData().getLogin();
    }

    @Override
    public synchronized String execute() {
        if (collection.isEmpty())
            return "Невозможно удалить элемент с заданным ключом. Коллекция уже пуста.\n";
        else if (!collection.containsKey(key))
            return "Элемента с таким ключом нет в коллекции! Попробуйте ввести другой ключ.\n";
        else {
            if (database.remove(key)) {
                collection.keySet().removeIf(k -> k.equals(key) && collection.get(k).getLogin().equals(login));
                return ("Элемент с ключом " + key + " успешно удален!\n");
            } else {
                return "Во время удаления элемента произошла ошибка!\n";
            }
        }
    }
}
