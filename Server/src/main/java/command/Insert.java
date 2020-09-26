package command;

import person.Person;
import server.Processor;

import java.util.TreeMap;

public class Insert extends Command {
    private TreeMap<String, Person> collection;
    private String key;
    private Person person;
    private Processor processor;
    private String result;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Insert(Processor processor) {
        synchronized (Processor.synchronizer) {
            collection = processor.getCollection().get();
        }
        key = processor.getCommandData().getParam1();
        this.processor = processor;
        this.person = processor.getCommandData().getPerson();
    }

    private boolean exists() {
        synchronized (Processor.synchronizer) {
            return collection.containsKey(key);
        }
    }

    @Override
    public String execute() {
        if(exists()) {
            result = "Невозможно добавить элемент с указанным ключом. Элемент с таким ключом уже присутствует в коллекции!\n";
        } else {
            if (person == null) {
                result = "Введите информацию о добавляемом элементе.\n";
            } else {
                if (person.getCreationDate() == null) {
                    result = "Элемент не был добвлен.\n";
                    processor.setResult(result);
                    return result;
                }
                if (processor.getDatabase().put(key, person)) {
                    synchronized (Processor.synchronizer) {
                        person.setId(processor.getDatabase().gainId(key));
                        collection.put(key, person);
                        processor.getCollection().set(collection);
                    }
                    result = "Элемент успешно добавлен.\n";
                } else result = "Ошибка добавления элемента.\n";
            }
        }
        processor.setResult(result);
        return result;
    }
}
