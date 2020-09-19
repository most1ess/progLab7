package command.util;

import person.Coordinates;
import person.Country;
import person.Location;
import person.Person;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class UpdateUtil {
    private Person person = new Person();
    private Scanner parameterInput = new Scanner(System.in);
    private Coordinates coordinates = new Coordinates();
    private Location location = new Location();
    private boolean isValueWritten;

    /**
     * Генерация человека при команде Update.
     * @return сгенерированный человек.
     */
    public Person genPerson(String login) {
        name();
        xCoordinate();
        yCoordinate();
        height();
        birthday();
        weight();
        nationality();
        location();
        genCreationDate();
        person.setId(null);
        person.setLogin(login);
        return person;
    }

    /**
     * Ввод имени.
     */
    public void name() {
        isValueWritten = false;
        while(!isValueWritten) {
            try {
                System.out.println("Текущее имя: " + person.getName());
                System.out.print("Введите имя (не может быть пустым): ");
                person.setName(parameterInput.nextLine());
                isValueWritten = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Значение не может быть пустым.");
            }
        }
    }

    /**
     * Ввод координаты х.
     */
    public void xCoordinate() {
        isValueWritten = false;
        while(!isValueWritten) {
            try {
                System.out.println("Текущая координата Х: " + coordinates.getX());
                System.out.print("Введите координату Х (число): ");
                coordinates.setX(Double.parseDouble(parameterInput.nextLine()));
                isValueWritten = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Значение должно быть числом.");
            }
        }
    }

    /**
     * Ввод координаты у.
     */
    public void yCoordinate() {
        isValueWritten = false;
        while(!isValueWritten) {
            try {
                System.out.println("Текущая координата Y: " + coordinates.getY());
                System.out.print("Введите координату Y (число, не большее 355): ");
                String yCoordinate = parameterInput.nextLine();
                if (Double.parseDouble(yCoordinate) > 355) {
                    System.out.println("Введенное вами значение не попадает в заданные границы! Попробуйте снова.");
                } else {
                    coordinates.setY(Double.parseDouble(yCoordinate));
                    person.setCoordinates(coordinates);
                    isValueWritten = true;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Значение должно быть числом, не больши 355.");
            }
        }
    }

    /**
     * Ввод роста.
     */
    public void height() {
        isValueWritten = false;
        while(!isValueWritten) {
            try {
                System.out.println("Текущий рост: " + person.getHeight());
                System.out.print("Введите рост (целое положительное число): ");
                String height = parameterInput.nextLine();
                if (Integer.parseInt(height) <= 0) {
                    System.out.println("Введенное вами значение должно быть положительным целым числом! Попробуйте снова.");
                } else {
                    person.setHeight(Integer.parseInt(height));
                    isValueWritten = true;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Значение должно быть целым положительным числом.");
            }
        }
    }

    /**
     * Ввод дня рождения.
     */
    public void birthday() {
        isValueWritten = false;
        while(!isValueWritten) {
            try {
                System.out.println("Текущий День Рождения: " + person.getBirthday());
                System.out.print("Введите День Рождения (в формате YYYY-MM-DD HH:MM) (может быть пустым): ");
                String birthday = parameterInput.nextLine();
                if (birthday.equals("")) {
                    person.setBirthday(null);
                } else {
                    person.setBirthday(parameterInput.nextLine());
                }
                isValueWritten = true;
            } catch (DateTimeParseException | IllegalArgumentException e) {
                System.out.println("Значение должно быть записано в формате YYYY-MM-DD HH:MM.");
            }
        }
    }

    /**
     * Ввод веса.
     */
    public void weight() {
        isValueWritten = false;
        while(!isValueWritten) {
            try {
                System.out.println("Текущий вес: " + person.getWeight());
                System.out.print("Введите вес (положительное число): ");
                String weight = parameterInput.nextLine();
                if (Long.parseLong(weight) <= 0) {
                    System.out.println("Введенное вами значение должно быть положительным! Попробуйте снова.");
                } else {
                    person.setWeight(Long.parseLong(weight));
                    isValueWritten = true;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Значение должно быть положительным числом.");
            }
        }
    }

    /**
     * Ввод национальности.
     */
    public void nationality() {
        isValueWritten = false;
        while(!isValueWritten) {
            try {
                System.out.println("Текущая национальность: " + person.getNationality());
                System.out.print("Введите национальность (USA, FRANCE, ITALY): ");
                person.setNationality(Country.valueOf(parameterInput.nextLine()));
                isValueWritten = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Значение должно быть одним из (USA, FRANCE, ITALY).");
            }
        }
    }

    public void location() {
        isValueWritten = false;
        while(!isValueWritten) {
            if(location == null) {
                System.out.println("Текущее значение полей поля location: null");
            } else {
                System.out.println("Текущее значение полей поля location: " + location.getX() + ", " + location.getY() + ", " + location.getZ());
            }
            System.out.println("Введите местоположение.");
            System.out.print("Хотите ли вы, чтобы местоположение было нулевым? (y/n) ");
            Scanner scanner = new Scanner(System.in);
            String gnida = scanner.nextLine();
            switch(gnida) {
                case "y":
                    person.setLocation(null);
                    isValueWritten = true;
                    break;
                case "n":
                    xLocation();
                    yLocation();
                    zLocation();
                    isValueWritten = true;
                    break;
                default:
                    System.out.println("Введенное вами значение должно быть y или n.");
                    break;
            }
        }
    }

    /**
     * Ввод местоположения по х.
     */
    private void xLocation() {
        isValueWritten = false;
        while(!isValueWritten) {
            try {
                System.out.println("Текущее местоположение по Х: " + location.getX());
                System.out.print("Введите местоположение по Х (число): ");
                location.setX(Long.valueOf(parameterInput.nextLine()));
                isValueWritten = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Значение должно быть числом.");
            }
        }
    }

    /**
     * Ввод местоположения по у.
     */
    private void yLocation() {
        isValueWritten = false;
        while(!isValueWritten) {
            try {
                System.out.println("Текущее местоположение по Y: " + location.getY());
                System.out.print("Введите местоположение по Y (число): ");
                location.setY(Float.parseFloat(parameterInput.nextLine()));
                isValueWritten = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Значение должно быть числом.");
            }
        }
    }

    /**
     * Ввод местоположения по z.
     */
    private void zLocation() {
        isValueWritten = false;
        while(!isValueWritten) {
            try {
                System.out.println("Текущее местоположение по Z: " + location.getZ());
                System.out.print("Введите местоположение по Z (число): ");
                location.setZ(Double.parseDouble(parameterInput.nextLine()));
                person.setLocation(location);
                isValueWritten = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Значение должно быть числом.");
            }
        }
    }

    public void genCreationDate() {
        person.setCreationDate(LocalDateTime.now().withNano(0).withSecond(0).toString().replace("T", " "));
    }
}

