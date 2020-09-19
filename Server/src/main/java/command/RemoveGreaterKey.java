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

    public RemoveGreaterKey(Processor processor) {
        collection = processor.getCollection().get();
        key = processor.getCommandData().getParam1();
        database = processor.getDatabase();
        login = processor.getCommandData().getLogin();
    }

    @Override
    public synchronized String execute() {
        if (collection.isEmpty()) {
            return "Опа! А коллекция то пуста!\n";
        } else {
            if(database.removeGreaterKey(key))
                if(collection.keySet().removeIf(k -> k.compareTo(key) > 0 && collection.get(k).getLogin().equals(login)))
                    return ("Все объекты, ключ которых превышает " + key + ", успешно удалены.\n");
                else return "Опа! А элементов таких и нет, оказывается!\n";
            else return "При удалении элементов произошла ошибка!\n";
        }
    }
}
