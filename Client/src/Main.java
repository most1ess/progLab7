import client.Processor;

import java.util.NoSuchElementException;

public class Main {
    public static void main(String[] args) {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n\n----------------------------------------");
                System.out.println("\nРабота программы была завершена.\n");
            }));
            Processor client = new Processor();
            client.execute();
        } catch (NoSuchElementException e) {
            System.out.println("\n\n----------------------------------------");
            System.out.println("\nРабота программы была прервана.\n");
        }
    }
}
