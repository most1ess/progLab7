package command;

import person.Person;
import server.Processor;

import java.util.TreeMap;

public class Insert extends Command {
    private TreeMap<String, Person> collection;
    private String key;
    private Person person;
    private Processor processor;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Insert(Processor processor, CommandData commandData) {
        synchronized (processor.getSynchronizer()) {
            collection = processor.getCollection().get();
        }
        key = commandData.getParam1();
        this.processor = processor;
        this.person = commandData.getPerson();
    }

    private boolean exists() {
        synchronized (processor.getSynchronizer()) {
            return collection.containsKey(key);
        }
    }

    @Override
    public String execute() {
        String result;
        if(exists()) {
            result = "Невозможно добавить элемент с указанным ключом. Элемент с таким ключом уже присутствует в коллекции!\n";
        } else {
            if (person == null) {
                result = "Введите информацию о добавляемом элементе.\n";
            } else {
                if (person.getCreationDate() == null) {
                    result = "Элемент не был добвлен.\n";
                    return result;
                }
                if (processor.getDatabase().put(key, person)) {
                    synchronized (processor.getSynchronizer()) {
                        person.setId(processor.getDatabase().gainId(key));
                        collection.put(key, person);
                        processor.getCollection().set(collection);
                    }
                    result = "Элемент успешно добавлен.\n";
                } else result = "Ошибка добавления элемента.\n";
            }
        }
        return result;
    }
}
