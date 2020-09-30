package server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Level;

public class Sender extends RecursiveAction {
    private Processor processor;
    private SocketAddress socketAddress;
    private String result;

    public Sender(Processor processor, SocketAddress socketAddress, String result) {
        this.processor = processor;
        this.socketAddress = socketAddress;
        this.result = result;
    }

    /**
     * Отправление результата исполнения команды пользователю.
     * @param processor процессор сервера.
     * @throws IOException ошибка ввода-вывода.
     */
    public void send(Processor processor) throws IOException  {
        ByteBuffer buffer = ByteBuffer.wrap(result.getBytes());
        processor.getDatagramChannel().send(buffer, socketAddress);
        processor.getLogger().log(Level.INFO, "Данные отправлены получателю.");
    }

    @Override
    protected void compute() {
        try {
            send(processor);
        } catch (IOException e) {
            processor.getLogger().log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
        }
    }
}
