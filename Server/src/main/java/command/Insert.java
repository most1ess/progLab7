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

    public Insert(Processor processor) {
        collection = processor.getCollection().get();
        key = processor.getCommandData().getParam1();
        this.processor = processor;
    }

    public boolean exists() {
        return collection.containsKey(key);
    }

    @Override
    public synchronized String execute() {
        if(person.getCreationDate() == null) {
            return "Элемент не был добвлен.\n";
        }
        if (processor.getDatabase().put(key, person)) {
            collection.put(key, person);
            processor.getCollection().set(collection);
            return "Элемент успешно добавлен.\n";
        } else return "Ошибка добавления элемента.\n";
    }
}
