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
    private Processor processor;
    private CommandData commandData;

    public RemoveGreater(Processor processor, CommandData commandData) {
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
                double xCoordinate;
                try {
                    xCoordinate = Double.parseDouble(key);
                } catch (NumberFormatException e) {
                    result = "Введённый вами аргумент должен быть числом!\n";
                    return result;
                }
                if (database.removeGreater(xCoordinate, commandData))
                    if (collection.values().removeIf(person -> person.getCoordinates().getX() < xCoordinate && person.getLogin().equals(login)))
                        result = ("Все искомые объекты успешно удалены.\n");
                    else result = ("Опа! А объектов, удовлетворяющих условию, нет!\n");
                else result = "Опа! А объектов, удовлетворяющих условию, нет!\n";
            }
        }
        return result;
    }
}
