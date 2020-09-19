package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

public class Receiver {
    /**
     * Метод для получения дейтаграмм.
     * @param processor процессор клиента.
     * @return булево значение, используемое во многих других классах.
     */
    public static boolean receive(Processor processor){
        try {
            ByteBuffer answerInBytes = ByteBuffer.allocate(5000);
            DatagramPacket datagramPacket = new DatagramPacket(answerInBytes.array(), answerInBytes.array().length);
            processor.getDatagramSocket().setSoTimeout(3000);
            System.out.println("Ожидание ответа от сервера...");
            try {
                processor.getDatagramSocket().receive(datagramPacket);
            } catch(SocketTimeoutException e) {
                System.out.println("Сервер не ответил на запрос.\n");
                return false;
            }
            System.out.println("Данные получены.\n");
            if((new String(answerInBytes.array())).contains("Введите информацию о добавляемом элементе.\n") ||
                    (new String(answerInBytes.array())).contains("Пользователь под логином")) {
                if(!processor.isScriptMode()) {
                    System.out.println(new String(datagramPacket.getData()));
                }
                return true;
            }
            System.out.println(new String(datagramPacket.getData()));
        } catch(IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
