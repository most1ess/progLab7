package command.util;


import client.Processor;
import person.Coordinates;
import person.Country;
import person.Location;
import person.Person;

import java.time.LocalDateTime;
import java.util.Scanner;

public class InsertUtil {
    private boolean isValueWritten;
    private Person person = new Person();
    private Scanner parameterInput = new Scanner(System.in);
    private Coordinates coordinates = new Coordinates();
    private Location location = new Location();

    /**
     * Генерация человека при команде Insert.
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
        System.out.println();
        return person;
    }

    /**
     * Ввод имени.
     */
    public void name() {
        isValueWritten = false;
        while (!isValueWritten) {
            System.out.print("Введите имя (непустая строка): \n>");
            person.setName(parameterInput.nextLine());
            if (!person.getName().equals("")) {
                isValueWritten = true;
            } else {
                System.out.print("Имя не может быть пустым! ");
            }
        }
    }

    /**
     * Ввод координаты х.
     */
    public void xCoordinate() {
        isValueWritten = false;
        while (!isValueWritten) {
            try {
                System.out.print("Введите координату Х (любое число): \n>");
                coordinates.setX(Double.parseDouble(parameterInput.nextLine()));
                isValueWritten = true;
            } catch (IllegalArgumentException e) {
                System.out.print("Координата Х должна быть числом! ");
            }
        }
    }

    /**
     * Ввод координаты у.
     */
    public void yCoordinate() {
        isValueWritten = false;
        while (!isValueWritten) {
            try {
                System.out.print("Введите координату Y (любое число, не большее 355): \n>");
                String yCoordinate = parameterInput.nextLine();
                if (Double.parseDouble(yCoordinate) > 355) {
                    System.out.print("Введенная вами координата Y не попадает в заданные границы! ");
                } else {
                    coordinates.setY(Double.parseDouble(yCoordinate));
                    isValueWritten = true;
                }
            } catch (IllegalArgumentException e) {
                System.out.print("Координата Y должна быть числом, не большим, чем 355! ");
            }
        }
        person.setCoordinates(coordinates);
    }

    /**
     * Ввод роста.
     */
    public void height() {
        isValueWritten = false;
        while (!isValueWritten) {
            try {
                System.out.print("Введите рост (целое положительное число): \n>");
                String height = parameterInput.nextLine();
                if (Integer.parseInt(height) <= 0) {
                    System.out.print("Рост должен быть положительным целым числом! ");
                } else {
                    person.setHeight(Integer.parseInt(height));
                    isValueWritten = true;
                }
            } catch (IllegalArgumentException e) {
                System.out.print("Рост должен быть целым положительным числом! ");
            }
        }
    }

    /**
     * Ввод дня рождения.
     */
    public void birthday() {
        isValueWritten = false;
        while (!isValueWritten) {
            try {
                System.out.print("Введите День Рождения в формате YYYY-MM-DD HH:MM (пустая строка или дата в указанном формате): \n>");
                String birthday = parameterInput.nextLine();
                if (birthday.equals("")) {
                    person.setBirthday(null);
                } else {
                    person.setBirthday(birthday);
                }
                isValueWritten = true;
            } catch (java.time.format.DateTimeParseException e) {
                System.out.print("День Рождения должен быть записан в формате YYYY-MM-DD HH:MM! ");
            }
        }
    }

    /**
     * Ввод веса.
     */
    public void weight() {
        isValueWritten = false;
        while (!isValueWritten) {
            try {
                System.out.print("Введите вес (целое положительное число): \n>");
                String weight = parameterInput.nextLine();
                if (Long.parseLong(weight) <= 0) {
                    System.out.print("Вес должен быть положительным! ");
                } else {
                    person.setWeight(Long.parseLong(weight));
                    isValueWritten = true;
                }
            } catch (IllegalArgumentException e) {
                System.out.print("Вес должен быть целым положительным числом! ");
            }
        }
    }

    /**
     * Ввод национальности.
     */
    public void nationality() {
        isValueWritten = false;
        while (!isValueWritten) {
            try {
                System.out.print("Введите национальность (USA, FRANCE, ITALY): \n>");
                person.setNationality(Country.valueOf(parameterInput.nextLine()));
                isValueWritten = true;
            } catch (IllegalArgumentException e) {
                System.out.print("Национальность должна принимать одно из значений: (USA, FRANCE, ITALY)! ");
            }
        }
    }

    /**
     * Ввод местоположения.
     */
    public void location() {
        isValueWritten = false;
        while (!isValueWritten) {
            System.out.print("Хотите ли вы, чтобы местоположение имело нулевое значение? (y/n) \n>");
            Scanner scanner = new Scanner(System.in);
            String gnida = scanner.nextLine();
            switch (gnida) {
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
                    System.out.print("Ответ должен быть представлен в виде y(да) или n(нет)! ");
                    break;
            }
        }
    }

    /**
     * Ввод местоположения по х.
     */
    private void xLocation() {
        isValueWritten = false;
        while (!isValueWritten) {
            try {
                System.out.print("Введите местоположение по Х (любое число): \n>");
                location.setX(Long.valueOf(parameterInput.nextLine()));
                isValueWritten = true;
            } catch (IllegalArgumentException e) {
                System.out.print("Местоположение по Х должно быть числом! ");
            }
        }
    }

    /**
     * Ввод местоположения по у.
     */
    private void yLocation() {
        isValueWritten = false;
        while (!isValueWritten) {
            try {
                System.out.print("Введите местоположение по Y (любое число): \n>");
                location.setY(Float.parseFloat(parameterInput.nextLine()));
                isValueWritten = true;
            } catch (IllegalArgumentException e) {
                System.out.print("Местоположение по Y должно быть числом! ");
            }
        }
    }

    /**
     * Ввод местоположения по z.
     */
    private void zLocation() {
        isValueWritten = false;
        while (!isValueWritten) {
            try {
                System.out.print("Введите местоположение по Z (любое число): \n>");
                location.setZ(Double.parseDouble(parameterInput.nextLine()));
                isValueWritten = true;
            } catch (IllegalArgumentException e) {
                System.out.print("Местоположение по Z должно быть числом! ");
            }
        }
        person.setLocation(location);
    }

    public void genCreationDate() {
        person.setCreationDate(LocalDateTime.now().withNano(0).withSecond(0).toString().replace("T", " "));
    }
}

