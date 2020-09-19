package client;

import command.util.Checker;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Scanner;

public class Connector {
    /**
     * Выполняет открытие канала для обмена дейтаграммами.
     * @param processor процессор клиента
     */
    public static void connect(Processor processor){
        String host;
        String port;
        int portInt = 0;
        boolean isPortGood = false;

        Scanner sc = new Scanner(System.in);
        System.out.print("Для подключения к серверу, введите имя его хоста:\n>");
        host = sc.nextLine();
        while(host.contains(" ") || host.equals("")) {
            System.out.print("Имя хоста не должно быть пустым или содержать пробелов!\n" +
                    "Попробуйте ещё раз:\n>");
            host = sc.nextLine();
        }
        System.out.print("Введите порт:\n>");
        while (!isPortGood) {
            port = sc.nextLine();
            if(Checker.isInteger(port)) {
                portInt = Integer.parseInt(port);
                if(portInt >= 0 && portInt <= 65535) isPortGood = true;
                else System.out.print("Порт должен быть целым числом и находиться в пределах от 0 до 65535!\n" +
                        "Попробуйте ещё раз:\n>");
            } else System.out.print("Порт должен быть целым числом и находиться в пределах от 0 до 65535!\n" +
                        "Попробуйте ещё раз:\n>");
        }
        try {
            processor.setDatagramSocket(new DatagramSocket());
        } catch(SocketException e) {
            System.out.println("!!!---ОШИБКА---!!!\n");
            System.out.println("На локальной машине нет доступных для открытия портов.");
            System.out.println("Дальнейшая работа программы не может быть продолжена.\n");
            System.exit(1);
        }
        SocketAddress socketAddress = new InetSocketAddress(host, portInt);
        processor.setSocketAddress(socketAddress);
        try {
            processor.getDatagramChannel().connect(socketAddress);
        } catch (IOException e) {
            System.out.println("!!!---ОШИБКА---!!!\n");
            System.out.println("Каналу обмена датаграммами не удалось подключить сокет.");
            System.out.println("Дальнейшая работа программы не может быть продолжена.\n");
            System.exit(1);
        }
        if(processor.getDatagramChannel().isConnected()) {
            System.out.println("Сокет для обмена датаграммами был успешно подключён.");
        }
        System.out.println("\n----------------------------------------\n");
    }
}
