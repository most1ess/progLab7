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
    private Processor processor;
    private CommandData commandData;

    public RemoveGreaterKey(Processor processor, CommandData commandData) {
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
            if (collection.isEmpty()) {
                result = "Опа! А коллекция то пуста!\n";
            } else {
                if (database.removeGreaterKey(key, commandData))
                    if (collection.keySet().removeIf(k -> k.compareTo(key) > 0 && collection.get(k).getLogin().equals(login)))
                        result = ("Все объекты, ключ которых превышает " + key + ", успешно удалены.\n");
                    else result = "Опа! А элементов таких и нет, оказывается!\n";
                else result = "Опа! А элементов таких и нет, оказывается!\n";
            }
        }
        return result;
    }
}
