package server;

import command.util.Checker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;
import java.util.logging.Level;

public class Connector {
    /**
     * Открытие канала обмена дейтаграммами.
     * @param processor процессор сервера.
     */
    public static void connect(Processor processor) {
        String port;
        int portInt = 0;
        boolean isPortGood = false;
        System.out.print("Для открытия канала обмена датаграммами введите порт:\n>");
        try (Scanner sc = new Scanner(System.in)) {
            while (!isPortGood) {
                port = sc.nextLine();
                if (Checker.isInteger(port)) {
                    portInt = Integer.parseInt(port);
                    if (portInt >= 0 && portInt <= 65535) isPortGood = true;
                    else System.out.print("Порт должен быть целым числом и находиться в пределах от 0 до 65535!\n" +
                            "Попробуйте ещё раз:\n>");
                } else System.out.print("Порт должен быть целым числом и находиться в пределах от 0 до 65535!\n" +
                        "Попробуйте ещё раз:\n>");
            }
        }
        SocketAddress socketAddress = new InetSocketAddress(portInt);
        try {
            DatagramChannel datagramChannel = DatagramChannel.open();
            datagramChannel.bind(socketAddress);
            processor.setDatagramChannel(datagramChannel);
        } catch (IOException e) {
            processor.getLogger().log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
            System.exit(1);
        }
        processor.getLogger().log(Level.INFO, "Открытие канала для передачи датаграмм.");
        System.out.println("Канал успешно подключен!\n");
        System.out.println("\n----------------------------------------\n\n");
    }
}
