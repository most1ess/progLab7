package command;

import person.Person;
import server.Database;
import server.Processor;

import java.util.TreeMap;

public class Clear extends Command {
    private TreeMap<String, Person> collection;
    private Database database;
    private String login;
    private Processor processor;
    private CommandData commandData;

    public Clear(Processor processor, CommandData commandData) {
        synchronized (processor.getSynchronizer()) {
            collection = processor.getCollection().get();
        }
        database = processor.getDatabase();
        login = commandData.getLogin();
        this.processor = processor;
        this.commandData = commandData;
    }

    @Override
    public String execute() {
        String result;
        synchronized (processor.getSynchronizer()) {
            if (collection.isEmpty()) {
                result = "Невозможно очистить коллекцию. Коллекция уже пуста.\n";
            } else {
                if (database.clear(commandData)) {
                    collection.values().removeIf(person -> person.getLogin().equals(login));
                    result = "Коллекция успешно очищена.\n";
                } else {
                    result = "При очистке коллекции возникла ошибка.\n";
                }
            }
        }
        return result;
    }
}
