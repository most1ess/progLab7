package command.util.script;

import person.Coordinates;
import person.Country;
import person.Location;
import person.Person;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.format.DateTimeParseException;

public class ScriptUtil {
    private BufferedReader bufferedReader;
    private Person person = new Person();

    /**
     * Генерация человека в соответствие с данными из скрипта.
     * @return получилось ли сгенерировать человека.
     * @throws IOException ошибка ввода вывода.
     */
    public boolean genPerson() throws IOException {
        String line;
        try {
            line = bufferedReader.readLine();
            if(line.equals("")) return false;
            person.setName(line);

            line = bufferedReader.readLine();
            Coordinates coordinates = new Coordinates();
            coordinates.setX(Double.parseDouble(line));

            line = bufferedReader.readLine();
            if (Double.parseDouble(line) > 355) return false;
            coordinates.setY(Double.parseDouble(line));
            person.setCoordinates(coordinates);
            line = bufferedReader.readLine();
            if (Integer.parseInt(line) <= 0) {
                return false;
            }
            person.setHeight(Integer.parseInt(line));
            line = bufferedReader.readLine();
            if (line.equals("")) {
                person.setBirthday(null);
            } else {
                person.setBirthday(line);
            }
            line = bufferedReader.readLine();
            if (Long.parseLong(line) <= 0) {
                return false;
            }
            person.setWeight(Long.parseLong(line));
            line = bufferedReader.readLine();
            person.setNationality(Country.valueOf(line));
            line = bufferedReader.readLine();
            Location location = new Location();
            if (line.equals("")) {
                location = null;
            } else {
                location.setX(Long.valueOf(line));
                line = bufferedReader.readLine();
                location.setY(Float.parseFloat(line));
                line = bufferedReader.readLine();
                location.setZ(Double.parseDouble(line));
            }
            person.setLocation(location);
            System.out.println("kek");

            person.setCreationDate(java.time.LocalDateTime.now().withNano(0).withSecond(0).toString().replace("T", " "));
        } catch(NullPointerException | DateTimeParseException | IllegalArgumentException e) {
            System.out.println("В заполнении данных о добавляемом объекте присутствуют ошибки.");
            return false;
        }
        return true;
    }

    public Person getPerson() {
        return person;
    }

    public ScriptUtil(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }
}
