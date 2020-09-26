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
    private String result;
    private Processor processor;

    public RemoveGreater(Processor processor) {
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
                double xCoordinate;
                try {
                    xCoordinate = Double.parseDouble(key);
                } catch (NumberFormatException e) {
                    result = "Введённый вами аргумент должен быть числом!\n";
                    processor.setResult(result);
                    return result;
                }
                if (database.removeGreater(xCoordinate))
                    if (collection.values().removeIf(person -> person.getCoordinates().getX() < xCoordinate && person.getLogin().equals(login)))
                        result = ("Все искомые объекты успешно удалены.\n");
                    else result = ("Опа! А объектов, удовлетворяющих условию, нет!\n");
                else result = "Опа! А объектов, удовлетворяющих условию, нет!\n";
            }
        }
        processor.setResult(result);
        return result;
    }
}
