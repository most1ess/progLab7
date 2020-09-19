package command;

import person.Person;
import server.Processor;

import java.util.*;

public class Show extends Command {
    private TreeMap<String, Person> collection;

    public Show(Processor processor) {
        collection = processor.getCollection().get();
    }

    @Override
    public String execute() {
        StringBuilder resBuilder = new StringBuilder();
        if(collection.isEmpty()) {
            return "Невозможно отобразить элементы коллекции. Коллекция пуста!\n";
        } else {
            TreeMap<Person, String> sortedRes = new TreeMap<>();
            collection.entrySet()
                    .forEach((e) -> sortedRes.put(e.getValue(), getString(e)));
            sortedRes.forEach((key, value) -> resBuilder.append(value));
        }
        return resBuilder.toString();
    }

    private synchronized String getString(Map.Entry<String, Person> entry) {
        StringBuilder resBuilder = new StringBuilder();
        resBuilder.append("КЛЮЧ ЭЛЕМЕНТА: ").append(entry.getKey()).append('\n');
        resBuilder.append("ID: ").append(entry.getValue().getId()).append('\n');
        resBuilder.append("Имя: ").append(entry.getValue().getName()).append('\n');
        resBuilder.append("Координата X: ").append(entry.getValue().getCoordinates().getX()).append('\n');
        resBuilder.append("Координата Y: ").append(entry.getValue().getCoordinates().getY()).append('\n');
        resBuilder.append("Дата создания: ").append(entry.getValue().getCreationDate().toString().replaceAll("T", " ")).append('\n');
        resBuilder.append("Рост: ").append(entry.getValue().getHeight()).append('\n');
        resBuilder.append("День Рождения: ");
        try {
            resBuilder.append(entry.getValue().getBirthday().toString().replaceAll("T", " "));
        } catch (NullPointerException e) {
            resBuilder.append("null");
        }
        resBuilder.append('\n');
        resBuilder.append("Вес: ").append(entry.getValue().getWeight()).append('\n');
        resBuilder.append("Национальность: ").append(entry.getValue().getNationality()).append('\n');
        if (entry.getValue().getLocation() == null) {
            resBuilder.append("Местоположение: null").append('\n');
        } else {
            resBuilder.append("Местоположение по X: ").append(entry.getValue().getLocation().getX()).append('\n');
            resBuilder.append("Местоположение по Y: ").append(entry.getValue().getLocation().getY()).append('\n');
            resBuilder.append("Местоположение по Z: ").append(entry.getValue().getLocation().getZ()).append('\n');
        }
        resBuilder.append("Владелец: ").append(entry.getValue().getLogin()).append('\n');
        return resBuilder.toString();
    }
}
