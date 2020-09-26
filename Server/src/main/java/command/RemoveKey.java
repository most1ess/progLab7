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
    private String result;
    private Processor processor;

    public RemoveKey(Processor processor) {
        synchronized (Processor.synchronizer) {
            collection = processor.getCollection().get();
        }
        key = processor.getCommandData().getParam1();
        database = processor.getDatabase();
        login = processor.getCommandData().getLogin();
        this.processor = processor;
    }

    @Override
    public String execute() {
        synchronized (Processor.synchronizer) {
            if (collection.isEmpty())
                result = "Невозможно удалить элемент с заданным ключом. Коллекция уже пуста.\n";
            else if (!collection.containsKey(key))
                result = "Элемента с таким ключом нет в коллекции! Попробуйте ввести другой ключ.\n";
            else {
                if (database.remove(key)) {
                    collection.keySet().removeIf(k -> k.equals(key) && collection.get(k).getLogin().equals(login));
                    result = ("Элемент с ключом " + key + " успешно удален!\n");
                } else {
                    result = "Не надо лезть в чужие элементы.\n";
                }
            }
        }
        processor.setResult(result);
        return result;
    }
}
