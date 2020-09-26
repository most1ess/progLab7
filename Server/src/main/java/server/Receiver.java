package server;

import person.Person;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class Receiver<T> extends RecursiveTask<T> {
    private Processor processor;

    public Receiver(Processor processor) {
        this.processor = processor;
    }

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

    @Override
    protected T compute() {
        try {
            return receive(processor);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
