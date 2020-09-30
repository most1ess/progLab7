package server;

import command.CommandData;

import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Level;

public class Receiver extends RecursiveAction {
    private Processor processor;

    public Receiver(Processor processor) {
        this.processor = processor;
    }

    /**
     * Получение объекта.
     * @param processor - процессор сервера
     * @throws ClassNotFoundException - ошибка классов
     * @throws IOException - ошибка ввода вывода
     */
    public static void receive(Processor processor) throws ClassNotFoundException, IOException {
        ByteBuffer buffer = ByteBuffer.allocate(5000);
        byte[] bufArray = buffer.array();
        CommandData commandData;
        SocketAddress socketAddress;
        socketAddress = processor.getDatagramChannel().receive(buffer);
        processor.setThreadStatus(true);
        if(socketAddress == null) {
            return;
        }
        try (ObjectInputStream serialize = new ObjectInputStream(new ByteArrayInputStream(bufArray))) {
            commandData = (CommandData) serialize.readObject();
            buffer.clear();
        } catch (IOException e) {
            processor.getLogger().log(Level.SEVERE, "Ошибка!");
            return;
        }
        processor.getLogger().log(Level.INFO, "Получена команда " + commandData.getName() + ".");
        processor.getLogger().log(Level.INFO, "Обработка команды...");
        processor.handle(commandData, socketAddress);
    }

    @Override
    protected void compute() {
        try {
            receive(processor);
        } catch (ClassNotFoundException | IOException e) {
            processor.getLogger().log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
        }
    }
}
