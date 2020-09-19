package command;

import person.Person;
import server.Processor;

import java.util.TreeMap;
import java.util.regex.Pattern;

public class FilterStartsWithName extends Command {
    private TreeMap<String, Person> collection;
    private String name;

    public FilterStartsWithName(Processor processor) {
        collection = processor.getCollection().get();
        name = processor.getCommandData().getParam1();
    }

    @Override
    public synchronized String execute() {
        if(collection.isEmpty()) {
            return "Опа! А колллекция то пуста!\n И элементов таких, получается, что нет :)\n";
        } else {
            StringBuilder resBuilder = new StringBuilder();
            String regex = "^" + name + ".*";
            Pattern pattern = Pattern.compile(regex);

            if(collection.values().stream()
                    .anyMatch((p) -> pattern.matcher(p.getName()).lookingAt())) {
                collection.values().stream()
                        .filter((p) -> pattern.matcher(p.getName()).lookingAt())
                        .forEachOrdered((p) -> resBuilder.append(p.getId()).append(";\n"));
                resBuilder.append("Элементы с указанными выше id имеют имя, начинающееся с '").append(name).append("'.\n");
            } else resBuilder.append("Опа! А нет таких элементов!");
            return resBuilder.toString();
        }
    }
}
