package command;

import person.Person;
import server.Processor;

import java.util.TreeMap;

public class CountGreaterThanLocation extends Command {
    private TreeMap<String, Person> collection;
    private String location;

    public CountGreaterThanLocation(Processor processor) {
        collection = processor.getCollection().get();
        location = processor.getCommandData().getParam1();
    }

    @Override
    public synchronized String execute() {
        if (collection.isEmpty()) {
            return "Опа! А коллекция то пуста!\nНу и элементов, получается, нет таких!";
        } else {
            long inputLocation;
            try {
                inputLocation = Long.parseLong(location);
            } catch(NumberFormatException e) {
                return "Введённый вами аргумент должен быть числом!\n";
            }
            long amount = collection.values().stream()
                    .filter((p) -> p.getLocation()!=null)
                    .filter((p) -> (p.getLocation().getX() + p.getLocation().getY() + p.getLocation().getZ()) > inputLocation)
                    .count();
            return ("Количество элементов с суммой координат location, больших чем " + location + ": " + amount + ".\n");
        }
    }
}
