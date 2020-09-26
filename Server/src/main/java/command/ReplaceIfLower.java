package command;

import person.Person;
import server.Database;
import server.Processor;

import javax.xml.crypto.Data;
import java.util.TreeMap;

public class ReplaceIfLower extends Command {
    private TreeMap<String, Person> collection;
    private String key;
    private String value;
    private Database database;
    private String login;
    private String result;
    private Processor processor;

    public ReplaceIfLower(Processor processor) {
        synchronized (Processor.synchronizer) {
            collection = processor.getCollection().get();
        }
        key = processor.getCommandData().getParam1();
        value = processor.getCommandData().getParam2();
        database = processor.getDatabase();
        login = processor.getCommandData().getLogin();
        this.processor = processor;
    }

    @Override
    public String execute() {
        synchronized (Processor.synchronizer) {
            if (collection.isEmpty()) {
                result = ("Опа! А коллекция то пуста!\n");
            } else {
                int valueInt;
                try {
                    valueInt = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    result = "Введённый вами аргумент должен быть целым положительным числом!\n";
                    processor.setResult(result);
                    return result;
                }
                if (valueInt <= 0) {
                    result = "Введённый вами аргумент должен быть целым положительным числом!\n";
                    processor.setResult(result);
                    return result;
                }
                if (collection.get(key).getHeight() > valueInt) {
                    if (database.replaceIfLower(key, valueInt)) {
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
        processor.setResult(result);
        return result;
    }
}
