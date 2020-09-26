package server;

import java.io.IOException;
import java.io.PipedReader;
import java.nio.ByteBuffer;
import java.util.concurrent.RecursiveAction;

public class Sender extends RecursiveAction {
    private Processor processor;

    public Sender(Processor processor) {
        this.processor = processor;
    }

    /**
     * Отправление результата исполнения команды пользователю.
     * @param processor процессор сервера.
     * @throws IOException ошибка ввода-вывода.
     */
    public static void send(Processor processor) throws IOException  {
        ByteBuffer buffer = ByteBuffer.wrap(processor.getResult().getBytes());
        processor.getDatagramChannel().send(buffer, processor.getSocketAddress());
    }

    @Override
    protected void compute() {
        try {
            send(processor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
