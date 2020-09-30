package command;

import person.Person;
import server.Processor;

import java.util.TreeMap;

public class CountGreaterThanLocation extends Command {
    private TreeMap<String, Person> collection;
    private String location;
    private Processor processor;

    public CountGreaterThanLocation(Processor processor, CommandData commandData) {
        synchronized (processor.getSynchronizer()) {
            collection = processor.getCollection().get();
        }
        location = commandData.getParam1();
        this.processor = processor;
    }

    @Override
    public String execute() {
        String result;
        synchronized (processor.getSynchronizer()) {
            if (collection.isEmpty()) {
                result = "Опа! А коллекция то пуста!\nНу и элементов, получается, нет таких!";
            } else {
                long inputLocation;
                try {
                    inputLocation = Long.parseLong(location);
                } catch (NumberFormatException e) {
                    result = "Введённый вами аргумент должен быть числом!\n";
                    return result;
                }
                long amount = collection.values().stream()
                        .filter((p) -> p.getLocation() != null)
                        .filter((p) -> (p.getLocation().getX() + p.getLocation().getY() + p.getLocation().getZ()) > inputLocation)
                        .count();
                result = ("Количество элементов с суммой координат location, больших чем " + location + ": " + amount + ".\n");
            }
        }
        return result;
    }
}
