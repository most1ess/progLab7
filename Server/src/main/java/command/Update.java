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
    private Database database;
    private CommandData commandData;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Update(Processor processor, CommandData commandData) {
        synchronized (processor.getSynchronizer()) {
            collection = processor.getCollection().get();
        }
        key = commandData.getParam1();
        this.processor = processor;
        database = processor.getDatabase();
        person = commandData.getPerson();
        this.commandData = commandData;
    }

    @Override
    public String execute() {
        String result;
        synchronized (processor.getSynchronizer()) {
            if (!collection.containsKey(key)) {
                result = "Элемента с указанным ключом нет в коллекции.\n";
            } else if (!collection.get(key).getLogin().equals(commandData.getLogin())) {
                result = "Не надо лезть в чужие элементы.\n";
            } else if (person == null) {
                result = "Введите информацию о добавляемом элементе.\n";
            } else {
                if (person.getCreationDate() == null) {
                    result = "Элемент не был добавлен.\n";
                    return result;
                }
                int oldId = collection.get(key).getId();
                person.setId(oldId);
                if (database.remove(key, commandData) && database.put(key, person)) {
                    collection.remove(key);
                    collection.put(key, person);
                    processor.getCollection().set(collection);
                    result = "Элемент успешно изменён.\n";
                } else result = "Во время изменения элемента произошла ошибка.\n";
            }
        }
        return result;
    }
}
