package command;

import person.Person;
import server.Processor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class GroupCountingByCreationDate extends Command {
    private TreeMap<String, Person> collection;

    public GroupCountingByCreationDate(Processor processor) {
        collection = processor.getCollection().get();
    }

    @Override
    public synchronized String execute() {
        if (collection.isEmpty()) {
            return "Опа! А коллекция то пуста!\n";
        } else {
            StringBuilder resBuilder = new StringBuilder();
            resBuilder.append("А вот и сортировочка по дате подъехала:\n");
            TreeMap<LocalDateTime, Long> creationDates = new TreeMap<>(collection.values().stream()
                    .collect(Collectors.groupingBy(Person::getCreationDate, Collectors.counting())));
            creationDates.entrySet().stream()
                    .distinct()
                    .sorted()
                    .forEach((e) -> resBuilder.append(e.getKey()).append(" - ").append(e.getValue()).append(" человек;\n"));
            return resBuilder.toString();
        }
    }
}
