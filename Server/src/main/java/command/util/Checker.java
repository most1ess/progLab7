package command.util;

public class Checker {
    /**
     * Проверка на Integer.
     * @param string строка.
     * @return можно ли строку преобразовать в Integer.
     */
    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }
}
