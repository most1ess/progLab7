package command;

import person.Person;
import server.Database;
import server.Processor;

import java.util.TreeMap;

public class RemoveGreaterKey extends Command {
    private TreeMap<String, Person> collection;
    private String key;
    private Database database;
    private String login;
    private String result;
    private Processor processor;

    public RemoveGreaterKey(Processor processor) {
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
            if (collection.isEmpty()) {
                result = "Опа! А коллекция то пуста!\n";
            } else {
                if (database.removeGreaterKey(key))
                    if (collection.keySet().removeIf(k -> k.compareTo(key) > 0 && collection.get(k).getLogin().equals(login)))
                        result = ("Все объекты, ключ которых превышает " + key + ", успешно удалены.\n");
                    else result = "Опа! А элементов таких и нет, оказывается!\n";
                else result = "Опа! А элементов таких и нет, оказывается!\n";
            }
        }
        processor.setResult(result);
        return result;
    }
}
