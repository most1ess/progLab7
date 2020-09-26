package server;

import collection.People;
import command.*;
import person.Person;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Processor {
    public static final Object synchronizer = new Object();
    private Logger logger;
    private People collection = new People();
    private SocketAddress socketAddress;
    private DatagramChannel datagramChannel;

    public void setCommandData(CommandData commandData) {
        this.commandData = commandData;
    }

    private CommandData commandData;

    public void setResult(String result) {
        this.result = result;
    }

    private String result;
    private boolean workingStatus = true;
    private Receiver commandDataReceiver = new Receiver(this);
    private ForkJoinPool receivePool = ForkJoinPool.commonPool();
    private ExecutorService handlePool = Executors.newCachedThreadPool();
    private Callable<String> task;
    private Future<String> futureResult;

    public ForkJoinPool getSendPool() {
        return sendPool;
    }

    public void setSendPool(ForkJoinPool sendPool) {
        this.sendPool = sendPool;
    }

    private ForkJoinPool sendPool = ForkJoinPool.commonPool();

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    private Sender sender = new Sender(this);

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    private Database database;

    /**
     * Создание лога работы сервера.
     */
    private void createLog() {
        try (FileInputStream ins = new FileInputStream("log.config")) {
            LogManager.getLogManager().readConfiguration(ins);
            logger = Logger.getLogger(Processor.class.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Основной метод, организующий работу сервера.
     */
    public void execute() {
        System.out.println("\n>>>>>>>>>> ДОБРО ПОЖАЛОВАТЬ! <<<<<<<<<<\n\n");
        System.out.println("Лабораторная работа №6, Серверное приложение\n");
        System.out.println("Студент: Максим Беляков\n");
        System.out.println("Группа: Р3131\n\n");
        System.out.println("----------------------------------------\n");


        System.out.println("Создания лога работы сервера...");
        createLog();
        System.out.println("Лог работы сервера создан!\n");
        logger.log(Level.INFO, "Начало логирования исполнения программы.");
        System.out.println("\nНастройка подключения канала обмена датаграммами...");
        Connector.connect(this);
        if (!createDatabase()) System.exit(1);
        if (!database.load()) System.exit(1);

        while (workingStatus) {
            commandDataReceiver = new Receiver(this);
            sender = new Sender(this);
            System.out.println("Ожидание данных от клиента...");
            receivePool.invoke(commandDataReceiver);
        }

    }

    /**
     * Обработка полученных от пользователя данных.
     *
     * @param name имя команды.
     * @throws IOException            ошибка ввода-вывода.
     * @throws ClassNotFoundException ошибка ненахождения класса.
     */
    public void handle(String name) throws IOException, ClassNotFoundException {
        try {
            if (!name.equals("loginUser") && !name.equals("registerUser")) {
                if (!database.checkUser(commandData.getLogin(), commandData.getHashedPassword())) {
                    System.out.println("Ошибка авторизации пользователя.\nКоманда не может быть исполнена.\n");
                    return;
                }
            }
            switch (name) {
                case "registerUser":
                    task = () -> (new RegisterUser(this)).execute();
                    break;
                case "loginUser":
                    task = () -> (new LoginUser(this)).execute();
                    break;
                case "help":
                    task = () -> (new Help(this)).execute();
                    break;
                case "info":
                    task = () -> (new Info(this)).execute();
                    break;
                case "show":
                    task = () -> (new Show(this).execute());
                    break;
                case "clear":
                    task = () -> (new Clear(this).execute());
                    break;
                case "remove_key":
                    task = () -> (new RemoveKey(this).execute());
                    break;
                case "remove_greater":
                    task = () -> (new RemoveGreater(this).execute());
                    break;
                case "replace_if_lower":
                    task = () -> (new ReplaceIfLower(this).execute());
                    break;
                case "remove_greater_key":
                    task = () -> (new RemoveGreaterKey(this).execute());
                    break;
                case "group_counting_by_creation_date":
                    task = () -> (new GroupCountingByCreationDate(this).execute());
                    break;
                case "count_greater_than_location":
                    task = () -> (new CountGreaterThanLocation(this).execute());
                    break;
                case "filter_starts_with_name":
                    task = () -> (new FilterStartsWithName(this).execute());
                    break;
                case "insert":
                    task = () -> (new Insert(this).execute());
                    break;
                case "update":
                    task = () -> (new Update(this).execute());
                    break;
            }
            futureResult = handlePool.submit(task);
            result = futureResult.get();
            getLogger().log(Level.INFO, "Команда обработана.");
            System.out.println("Команда обработана.");
            System.out.println("Отправление данных получателю...");
            getSendPool().invoke(getSender());
        } catch (ClassCastException e) {
            logger.log(Level.SEVERE, "Ошибка классов.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private boolean createDatabase() {
        if (!unpackFileName()) return false;
        try {
            database = new Database(this);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        if (!database.loadProperties()) return false;
        return database.connect();
    }

    private boolean unpackFileName() {
        if (System.getenv("FILE_PATH") == null) {
            System.out.println("Переменная окружения отсутствует! Коллекция не может быть загружена.");
            logger.log(Level.WARNING, "Переменная окружения отсутствует. Коллекция не может быть загружена.");
            return false;
        }
        fileName = System.getenv("FILE_PATH");
        if (fileName.equals("")) {
            System.out.println("Переменная окружения пуста! Коллекция не может быть загружена.");
            logger.log(Level.WARNING, "Переменная окружения пуста! Коллекция не может быть загружена.");
            return false;
        }
        return true;
    }

    public People getCollection() {
        return collection;
    }

    public void setCollection(People collection) {
        this.collection = collection;
    }

    public CommandData getCommandData() {
        return commandData;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public DatagramChannel getDatagramChannel() {
        return datagramChannel;
    }

    public void setDatagramChannel(DatagramChannel datagramChannel) {
        this.datagramChannel = datagramChannel;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getResult() {
        return result;
    }

    public void setPort(int portInt) {
    }
}
