package server;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Sender {
    /**
     * Отправление результата исполнения команды пользователю.
     * @param processor процессор сервера.
     * @throws IOException ошибка ввода-вывода.
     */
    public static void send(Processor processor) throws IOException  {
        ByteBuffer buffer = ByteBuffer.wrap(processor.getResult().getBytes());
        processor.getDatagramChannel().send(buffer, processor.getSocketAddress());
    }
}
