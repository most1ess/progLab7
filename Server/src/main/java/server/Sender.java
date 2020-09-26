package server;

import java.io.IOException;
import java.io.PipedReader;
import java.nio.ByteBuffer;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Level;

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
        processor.getLogger().log(Level.INFO, "Данные отправлены получателю.");
        System.out.println("Данные успешно отправлены.\n");
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
