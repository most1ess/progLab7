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
    private Processor processor;
    private CommandData commandData;

    public RemoveKey(Processor processor, CommandData commandData) {
        synchronized (processor.getSynchronizer()) {
            collection = processor.getCollection().get();
        }
        key = commandData.getParam1();
        database = processor.getDatabase();
        login = commandData.getLogin();
        this.processor = processor;
        this.commandData = commandData;
    }

    @Override
    public String execute() {
        String result;
        synchronized (processor.getSynchronizer()) {
            if (collection.isEmpty())
                result = "Невозможно удалить элемент с заданным ключом. Коллекция уже пуста.\n";
            else if (!collection.containsKey(key))
                result = "Элемента с таким ключом нет в коллекции! Попробуйте ввести другой ключ.\n";
            else if (!collection.get(key).getLogin().equals(commandData.getLogin())) {
                result = "Не надо лезть в чужие элементы.\n";
            } else if (database.remove(key, commandData)) {
                collection.keySet().removeIf(k -> k.equals(key) && collection.get(k).getLogin().equals(login));
                result = ("Элемент с ключом " + key + " успешно удален!\n");
            } else {
                result = "Не надо лезть в чужие элементы.\n";
            }
        }
        return result;
    }
}

