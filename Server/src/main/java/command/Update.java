package command;

import person.Person;
import server.Database;
import server.Processor;

import java.util.TreeMap;

public class Update extends Command {
    private TreeMap<String, Person> collection;
    private String key;
    private Person person;
    private Processor processor;
    private int oldId;
    private Database database;
    private String result;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Update(Processor processor) {
        synchronized (Processor.synchronizer) {
            collection = processor.getCollection().get();
        }
        key = processor.getCommandData().getParam1();
        this.processor = processor;
        database = processor.getDatabase();
        person = processor.getCommandData().getPerson();
    }

    private boolean exists() {
        synchronized (Processor.synchronizer) {
            return collection.containsKey(key);
        }
    }

    @Override
    public String execute() {
        if (!exists()) {
            result = "Элемента с указанным ключом нет в коллекции.\n";
        } else if (person == null) {
            result = "Введите информацию о добавляемом элементе.\n";
        } else {
            if (person.getCreationDate() == null) {
                result = "Элемент не был добавлен.\n";
                processor.setResult(result);
                return result;
            }
            oldId = collection.get(key).getId();
            person.setId(oldId);
            if (database.remove(key) && database.put(key, person)) {
                synchronized (Processor.synchronizer) {
                    collection.remove(key);
                    collection.put(key, person);
                    processor.getCollection().set(collection);
                }
                result = "Элемент успешно изменён.\n";
            } else result = "Во время изменения элемента произошла ошибка.\n";
        }
        processor.setResult(result);
        return result;
    }
}
