package server;

import command.CommandData;
import person.Person;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;

public class Receiver extends RecursiveAction {
    private Processor processor;

    public Receiver(Processor processor) {
        this.processor = processor;
    }

    public static void receive(Processor processor) throws ClassNotFoundException, IOException {
        ByteBuffer buffer = ByteBuffer.allocate(5000);
        try {
            processor.setSocketAddress(processor.getDatagramChannel().receive(buffer));
        } catch(IOException e) {
            e.printStackTrace();
        }
        byte[] bufArray = buffer.array();
        CommandData commandData;
        try (ObjectInputStream serialize = new ObjectInputStream(new ByteArrayInputStream(bufArray))) {
            commandData = (CommandData) serialize.readObject();
        } catch(IOException e) {
            e.printStackTrace();
            return;
        }
        processor.setCommandData(commandData);
        buffer.clear();
        processor.getLogger().log(Level.INFO, "Получена команда от клиента.");
        System.out.println("Получена команда " + commandData.getName() + ".");
        System.out.println("Обработка команды...");
        processor.handle(commandData.getName());
    }

    @Override
    protected void compute() {
        try {
            receive(processor);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}
