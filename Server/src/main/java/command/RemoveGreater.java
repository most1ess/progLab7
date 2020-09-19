package command;

import person.Person;
import server.Database;
import server.Processor;

import java.util.TreeMap;

public class RemoveGreater extends Command {
    private TreeMap<String, Person> collection;
    private String key;
    private Database database;
    private String login;

    public RemoveGreater(Processor processor) {
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
            double xCoordinate;
            try {
                xCoordinate = Double.parseDouble(key);
            } catch(NumberFormatException e) {
                return "Введённый вами аргумент должен быть числом!\n";
            }
            if (database.removeGreater(xCoordinate))
                if(collection.values().removeIf(person -> person.getCoordinates().getX() < xCoordinate && person.getLogin().equals(login)))
                    return ("Все искомые объекты успешно удалены.\n");
                else return ("Опа! А объектов, удовлетворяющих условию, нет!\n");
            else return "При удалении объектов произошла ошибка.\n";
        }
    }
}
