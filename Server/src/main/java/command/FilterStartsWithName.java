package command;

import person.Person;
import server.Processor;

import java.util.TreeMap;
import java.util.regex.Pattern;

public class FilterStartsWithName extends Command {
    private TreeMap<String, Person> collection;
    private String name;
    private Processor processor;

    public FilterStartsWithName(Processor processor, CommandData commandData) {
        synchronized (processor.getSynchronizer()) {
            collection = processor.getCollection().get();
        }
        name = commandData.getParam1();
        this.processor = processor;
    }

    @Override
    public String execute() {
        String result;
        synchronized (processor.getSynchronizer()) {
            if (collection.isEmpty()) {
                result = "Опа! А колллекция то пуста!\n И элементов таких, получается, что нет :)\n";
            } else {
                StringBuilder resBuilder = new StringBuilder();
                String regex = "^" + name + ".*";
                Pattern pattern = Pattern.compile(regex);

                if (collection.values().stream()
                        .anyMatch((p) -> pattern.matcher(p.getName()).lookingAt())) {
                    collection.values().stream()
                            .filter((p) -> pattern.matcher(p.getName()).lookingAt())
                            .forEachOrdered((p) -> resBuilder.append(p.getId()).append(";\n"));
                    resBuilder.append("Элементы с указанными выше id имеют имя, начинающееся с '").append(name).append("'.\n");
                } else resBuilder.append("Опа! А нет таких элементов!");
                result = resBuilder.toString();
            }
        }
        return result;
    }
}
