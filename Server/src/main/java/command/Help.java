package command;

public class Help extends Command {
    @Override
    public synchronized String execute() {
        return "Список доступных команд:" + "\n\n" +
        "help - вывод справки по доступным командам" + "\n\n" +
        "info - вывод информации о коллекции" + "\n\n" +
        "show - вывод всех элементов коллекции" + "\n\n" +
        "insert <key> - добавление нового элемента с заданным ключом" + "\n\n" +
        "remove_key <key> - удаление элемента по его ключу" + "\n\n" +
        "clear - очистка коллекции" + "\n\n" +
        "execute_script <file_name> - исполнение скрипта из указанного файла" + "\n\n" +
        "exit - завершение программы (без записи в файл)" + "\n\n" +
        "remove_greater <value> - удаление из коллекции всех элементов, в которых координата Х меньше введенной" + "\n\n" +
        "replace_if_lower <key> <value> - замена значения роста по ключу, если новое меньше старого" + "\n\n" +
        "remove_greater_key <key> - удаление всех элементов коллекции, в которых ключ превышает введённый" + "\n\n" +
        "group_counting_by_creation_date - группировка данных по значению поля creation_date и вывод сгруппированных данных" + "\n\n" +
        "count_greater_than_location <location> - вывод количества элементов, значение поля location которых больше заданного" + "\n\n" +
        "filter_starts_with_name <name> - вывод всех элементов, значение поля name которых начинается с заданной подстроки" + "\n";
    }
}
