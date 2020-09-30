package command;

import person.Person;
import server.Processor;

import java.util.TreeMap;

public class Info extends Command {
    private TreeMap<String, Person> collection;
    private java.time.LocalDateTime creationDate;
    private Processor processor;

    public Info(Processor processor) {
        synchronized (processor.getSynchronizer()) {
            collection = processor.getCollection().get();
            creationDate = processor.getCollection().getCreationDate();
            this.processor = processor;
        }
    }

    @Override
    public String execute() {
        String result;
        synchronized (processor.getSynchronizer()) {
            result = "Вид коллекции: TreeMap" + '\n' +
                    "Тип хранимых значений: Person" + '\n' +
                    "Количество элементов: " + collection.size() + '\n' +
                    "Дата создания: " + creationDate.toString().replaceAll("T", " ") + '\n';
        }
        return result;
    }
}
