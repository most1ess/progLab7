package server;

import java.net.SocketAddress;
import java.util.concurrent.*;
import java.util.logging.Level;

public class TaskCollapser implements Runnable {
    private Callable<String> task;
    private Processor processor;
    private SocketAddress socketAddress;

    public TaskCollapser(Callable<String> task, Processor processor, SocketAddress socketAddress) {
        this.task = task;
        this.processor = processor;
        this.socketAddress = socketAddress;
    }

    @Override
    public void run() {
        Future<String> future = processor.getHandlePool().submit(task);
        String result;
        try {
            result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            processor.getLogger().log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
            return;
        }
        processor.getLogger().log(Level.INFO, "Команда обработана.");
        processor.getLogger().log(Level.INFO, "Отправление данных получателю...");
        processor.getSendPool().execute(new Sender(processor, socketAddress, result));
    }
}
