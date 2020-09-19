package server;

import person.Person;

import java.io.*;
import java.nio.ByteBuffer;

public class Receiver {
    /**
     * Получение и десериализация данных от пользователя.
     * @param processor процессор сервера.
     * @param <T> тип получаемых данных.
     * @return полученный объект.
     * @throws ClassNotFoundException ошибка ненахождения класса.
     */
    public static <T> T receive(Processor processor) throws ClassNotFoundException {
        ByteBuffer buffer = ByteBuffer.allocate(5000);
        try {
            processor.setSocketAddress(processor.getDatagramChannel().receive(buffer));
        } catch(IOException e) {
            e.printStackTrace();
        }
        byte[] bufArray = buffer.array();
        T obj;
        try (ObjectInputStream serialize = new ObjectInputStream(new ByteArrayInputStream(bufArray))) {
            obj = (T) serialize.readObject();
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        buffer.clear();
        return obj;
    }
}
