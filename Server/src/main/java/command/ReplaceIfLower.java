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

    public ReplaceIfLower(Processor processor) {
        collection = processor.getCollection().get();
        key = processor.getCommandData().getParam1();
        value = processor.getCommandData().getParam2();
        database = processor.getDatabase();
        login = processor.getCommandData().getLogin();
    }

    @Override
    public synchronized String execute() {
        if(collection.isEmpty()) {
            return ("Опа! А коллекция то пуста!\n");
        } else {
            int valueInt;
            try {
                valueInt = Integer.parseInt(value);
            } catch(NumberFormatException e) {
                return "Введённый вами аргумент должен быть целым положительным числом!\n";
            }
            if(valueInt <= 0) {
                return "Введённый вами аргумент должен быть целым положительным числом!\n";
            }
            if (collection.get(key).getHeight() > valueInt) {
                if(database.replaceIfLower(key, valueInt)) {
                    if(collection.get(key).getLogin().equals(login)) {
                        collection.get(key).setHeight(Integer.parseInt(value));
                        return ("Рост успешно изменен.\n");
                    } else {
                        return ("Не надо лезть в чужие элементы :(.\n");
                    }
                } else return "Во время изменения роста произошла ошибка.\n";
            } else {
                return ("Опа! А введённый рост то не меньше текущего!\n");
            }
        }
    }
}
