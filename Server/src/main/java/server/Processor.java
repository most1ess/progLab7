package server;

import collection.People;
import command.*;

import java.io.FileInputStream;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Processor {
    private final Object synchronizer = new Object();

    public Object getSynchronizer2() {
        return synchronizer2;
    }

    private final Object synchronizer2 = new Object();
    volatile private Logger logger;
    volatile private People collection = new People();
    volatile private DatagramChannel datagramChannel;
    volatile private boolean threadStatus = true;
    volatile private String result;
    volatile private ForkJoinPool receivePool = ForkJoinPool.commonPool();
    volatile private ExecutorService handlePool = Executors.newCachedThreadPool();
    volatile private Callable<String> task;
    volatile private ForkJoinPool sendPool = ForkJoinPool.commonPool();
    volatile private String fileName;
    volatile private Database database;

    /**
     * Создание лога работы сервера.
     */
    private void createLog() {
        try (FileInputStream ins = new FileInputStream("log.config")) {
            LogManager.getLogManager().readConfiguration(ins);
            logger = Logger.getLogger(Processor.class.getName());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка!");
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
        logger.log(Level.INFO, "\nНастройка подключения канала обмена датаграммами...");
        Connector.connect(this);
        if (!createDatabase()) System.exit(1);
        if (!database.load()) System.exit(1);
        logger.log(Level.INFO, "Ожидание данных от клиента...");

        while (true) {
            synchronized (synchronizer2) {
                while (!threadStatus) {
                    try {
                        synchronizer2.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                threadStatus = false;
            }
            launchThread();
        }
    }

    public void launchThread() {
        receivePool.execute(new Receiver(this));
    }

    /**
     * Обработка полученных от пользователя данных.
     */
    public void handle(CommandData commandData, SocketAddress socketAddress) {
        String name = commandData.getName();
        try {
            if (!name.equals("loginUser") && !name.equals("registerUser")) {
                if (!database.checkUser(commandData.getLogin(), commandData.getHashedPassword())) {
                    logger.log(Level.SEVERE, "Ошибка авторизации пользователя.\nКоманда не может быть исполнена.\n");
                    return;
                }
            }
            switch (name) {
                case "registerUser":
                    task = () -> (new RegisterUser(this, commandData)).execute();
                    break;
                case "loginUser":
                    task = () -> (new LoginUser(this, commandData)).execute();
                    break;
                case "help":
                    task = () -> (new Help()).execute();
                    break;
                case "info":
                    task = () -> (new Info(this)).execute();
                    break;
                case "show":
                    task = () -> (new Show(this).execute());
                    break;
                case "clear":
                    task = () -> (new Clear(this, commandData).execute());
                    break;
                case "remove_key":
                    task = () -> (new RemoveKey(this, commandData).execute());
                    break;
                case "remove_greater":
                    task = () -> (new RemoveGreater(this, commandData).execute());
                    break;
                case "replace_if_lower":
                    task = () -> (new ReplaceIfLower(this, commandData).execute());
                    break;
                case "remove_greater_key":
                    task = () -> (new RemoveGreaterKey(this, commandData).execute());
                    break;
                case "group_counting_by_creation_date":
                    task = () -> (new GroupCountingByCreationDate(this).execute());
                    break;
                case "count_greater_than_location":
                    task = () -> (new CountGreaterThanLocation(this, commandData).execute());
                    break;
                case "filter_starts_with_name":
                    task = () -> (new FilterStartsWithName(this, commandData).execute());
                    break;
                case "insert":
                    task = () -> (new Insert(this, commandData).execute());
                    break;
                case "update":
                    task = () -> (new Update(this, commandData).execute());
                    break;
                case "thread":
                    task = () -> (new ThreadStart().execute());
            }
            handlePool.execute(new TaskCollapser(task, this, socketAddress));
        } catch (ClassCastException e) {
            logger.log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
        }
    }

    /**
     * Подключение к БД.
     *
     * @return - было ли успешно произведено действие
     */
    private boolean createDatabase() {
        if (!unpackFileName()) return false;
        try {
            database = new Database(this);
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
            return false;
        }
        if (!database.loadProperties()) return false;
        return database.connect();
    }

    /**
     * Получение имени файла с данными для БД.
     *
     * @return - было ли успешно произведено действие
     */
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

    public Object getSynchronizer() {
        return synchronizer;
    }

    public void setThreadStatus(boolean threadStatus) {
        this.threadStatus = threadStatus;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ExecutorService getHandlePool() {
        return handlePool;
    }

    public ForkJoinPool getSendPool() {
        return sendPool;
    }

    public String getFileName() {
        return fileName;
    }

    public Database getDatabase() {
        return database;
    }
}
