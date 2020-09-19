package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;

public class Sender {
    /**
     * Метод отправки дейтаграмм.
     * @param processor процессор клиента.
     * @param obj передаваемый объект.
     * @param <T> тип передаваемого объекта.
     */
    public static <T> void send(Processor processor, T obj) {
        try {
            processor.setBuf(serialize(obj));
            DatagramPacket datagramPacket = new DatagramPacket(processor.getBuf(), processor.getBuf().length, processor.getSocketAddress());
            processor.getDatagramSocket().send(datagramPacket);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод сериализации объекта.
     * @param obj сериализуемый объект.
     * @param <T> тип сериализуемого объекта.
     * @return сериализованный объект.
     */
    private static <T> byte[] serialize(T obj) {
        try (ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
             ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayStream)) {
            outputStream.writeObject(obj);
            return byteArrayStream.toByteArray();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

