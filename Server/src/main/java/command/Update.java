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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Update(Processor processor) {
        collection = processor.getCollection().get();
        key = processor.getCommandData().getParam1();
        this.processor = processor;
        database = processor.getDatabase();
    }

    public synchronized boolean exists() {
        if(collection.containsKey(key)) {
            oldId = collection.get(key).getId();
            return true;
        } else return false;
    } 

    @Override
    public synchronized String execute() {
        if(person.getCreationDate() == null) {
            return "Элемент не был добавлен.\n";
        }
        person.setId(oldId);
        if(database.remove(key) && database.put(key, person)) {
            collection.remove(key);
            collection.put(key, person);
            processor.getCollection().set(collection);
            return "Элемент успешно изменён.\n";
        } else return "Во время изменения элемента произошла ошибка.\n";
    }
}
