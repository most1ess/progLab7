package command;

import person.Person;
import server.Processor;

import java.util.TreeMap;

public class CountGreaterThanLocation extends Command {
    private TreeMap<String, Person> collection;
    private String location;
    private String result;
    private Processor processor;

    public CountGreaterThanLocation(Processor processor) {
        synchronized (Processor.synchronizer) {
            collection = processor.getCollection().get();
        }
        location = processor.getCommandData().getParam1();
        this.processor = processor;
    }

    @Override
    public String execute() {
        synchronized (Processor.synchronizer) {
            if (collection.isEmpty()) {
                result = "Опа! А коллекция то пуста!\nНу и элементов, получается, нет таких!";
            } else {
                long inputLocation;
                try {
                    inputLocation = Long.parseLong(location);
                } catch (NumberFormatException e) {
                    result = "Введённый вами аргумент должен быть числом!\n";
                    processor.setResult(result);
                    return result;
                }
                long amount = collection.values().stream()
                        .filter((p) -> p.getLocation() != null)
                        .filter((p) -> (p.getLocation().getX() + p.getLocation().getY() + p.getLocation().getZ()) > inputLocation)
                        .count();
                result = ("Количество элементов с суммой координат location, больших чем " + location + ": " + amount + ".\n");
            }
        }
        processor.setResult(result);
        return result;
    }
}
