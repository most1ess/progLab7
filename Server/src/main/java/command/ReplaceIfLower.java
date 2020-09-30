package command;

import person.Person;
import server.Database;
import server.Processor;

import java.util.TreeMap;

public class ReplaceIfLower extends Command {
    private TreeMap<String, Person> collection;
    private String key;
    private String value;
    private Database database;
    private String login;
    private Processor processor;
    private CommandData commandData;

    public ReplaceIfLower(Processor processor, CommandData commandData) {
        synchronized (processor.getSynchronizer()) {
            collection = processor.getCollection().get();
        }
        key = commandData.getParam1();
        value = commandData.getParam2();
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
                result = ("Опа! А коллекция то пуста!\n");
            } else {
                int valueInt;
                try {
                    valueInt = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    result = "Введённый вами аргумент должен быть целым положительным числом!\n";
                    return result;
                }
                if (valueInt <= 0) {
                    result = "Введённый вами аргумент должен быть целым положительным числом!\n";
                    return result;
                }
                if (collection.get(key).getHeight() > valueInt) {
                    if (database.replaceIfLower(key, valueInt, commandData)) {
                        if (collection.get(key).getLogin().equals(login)) {
                            collection.get(key).setHeight(Integer.parseInt(value));
                            result = ("Рост успешно изменен.\n");
                        } else {
                            result = ("Не надо лезть в чужие элементы.\n");
                        }
                    } else result = "Не надо лезть в чужие элементы.\n";
                } else {
                    result = ("Опа! А введённый рост то не меньше текущего!\n");
                }
            }
        }
        return result;
    }
}
