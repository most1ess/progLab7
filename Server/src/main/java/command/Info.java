package command;

import person.Person;
import server.Processor;

import java.util.TreeMap;

public class Info extends Command {
    private TreeMap<String, Person> collection;
    private java.time.LocalDateTime creationDate;

    public Info(Processor processor) {
        collection = processor.getCollection().get();
        creationDate = processor.getCollection().getCreationDate();
    }

    @Override
    public synchronized String execute() {
        return "Вид коллекции: TreeMap" + '\n' +
                "Тип хранимых значений: Person" + '\n' +
                "Количество элементов: " + collection.size() + '\n' +
                "Дата создания: " + creationDate.toString().replaceAll("T", " ") + '\n';
    }
}
