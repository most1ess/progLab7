package server;

import collection.People;
import command.*;
import person.Person;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Processor {
    private Logger logger;
    private People collection = new People();
    private SocketAddress socketAddress;
    private DatagramChannel datagramChannel;
    private CommandData commandData;
    private String result;
    private boolean workingStatus = true;

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
        try(FileInputStream ins = new FileInputStream("log.config")) {
            LogManager.getLogManager().readConfiguration(ins);
            logger = Logger.getLogger(Processor.class.getName());
        } catch (Exception e){
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

        try {
            System.out.println("Создания лога работы сервера...");
            createLog();
            System.out.println("Лог работы сервера создан!\n");
            logger.log(Level.INFO, "Начало логирования исполнения программы.");
            System.out.println("\nНастройка подключения канала обмена датаграммами...");
            Connector.connect(this);
            if (!createDatabase()) System.exit(1);
            if (!database.load()) System.exit(1);
            while(workingStatus) {
                System.out.println("Ожидание данных от клиента...");
                commandData = Receiver.<CommandData>receive(this);
                logger.log(Level.INFO, "Получена команда от клиента.");
                System.out.println("Получена команда " + this.commandData.getName() + ".");
                System.out.println("Обработка команды...");
                handle(commandData.getName());
                logger.log(Level.INFO, "Команда обработана.");
                System.out.println("Команда обработана.");
                System.out.println("Отправление данных получателю...");
                Sender.send(this);
                logger.log(Level.INFO, "Данные отправлены получателю.");
                System.out.println("Данные успешно отправлены.\n");
            }
        } catch(IOException e) {
            logger.log(Level.SEVERE, "Ошибка ввода-вывода.");
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Несоответствие классов.");
            e.printStackTrace();
        }
    }

    /**
     * Обработка полученных от пользователя данных.
     * @param name имя команды.
     * @throws IOException ошибка ввода-вывода.
     * @throws ClassNotFoundException ошибка ненахождения класса.
     */
    public void handle(String name) throws IOException, ClassNotFoundException{
        try {
            if (!name.equals("loginUser") && !name.equals("registerUser")) {
                if(!database.checkUser(commandData.getLogin(), commandData.getHashedPassword())) {
                    System.out.println("Ошибка авторизации пользователя.\nКоманда не может быть исполнена.\n");
                    return;
                }
            }
            switch (name) {
                case "registerUser":
                    result = (new RegisterUser(this)).execute();
                    break;
                case "loginUser":
                    result = (new LoginUser(this)).execute();
                    break;
                case "help":
                    result = (new Help()).execute();
                    break;
                case "info":
                    result = (new Info(this)).execute();
                    break;
                case "show":
                    result = (new Show(this).execute());
                    break;
                case "clear":
                    result = (new Clear(this).execute());
                    break;
                case "remove_key":
                    result = (new RemoveKey(this).execute());
                    break;
                case "remove_greater":
                    result = (new RemoveGreater(this).execute());
                    break;
                case "replace_if_lower":
                    result = (new ReplaceIfLower(this).execute());
                    break;
                case "remove_greater_key":
                    result = (new RemoveGreaterKey(this).execute());
                    break;
                case "group_counting_by_creation_date":
                    result = (new GroupCountingByCreationDate(this).execute());
                    break;
                case "count_greater_than_location":
                    result = (new CountGreaterThanLocation(this).execute());
                    break;
                case "filter_starts_with_name":
                    result = (new FilterStartsWithName(this).execute());
                    break;
                case "insert":
                    Insert insert = new Insert(this);
                    if (!insert.exists()) {
                        result = "Введите информацию о добавляемом элементе.\n";
                        Sender.send(this);
                        insert.setPerson(Receiver.<Person>receive(this));
                        result = insert.execute();
                    } else {
                        result = "Невозможно добавить элемент с указанным ключом. Элемент с таким ключом уже присутствует в коллекции!\n";
                    }
                    break;
                case "update":
                    Update update = new Update(this);
                    if (update.exists()) {
                        result = "Введите информацию о добавляемом элементе.\n";
                        Sender.send(this);
                        update.setPerson(Receiver.<Person>receive(this));
                        result = update.execute();
                    } else {
                        result = "Элемента с указанным ключом нет в коллекции.\n";
                    }
                    break;
            }
        } catch(ClassCastException e) {
            logger.log(Level.SEVERE, "Ошибка классов.");
            e.printStackTrace();
        }
    }

    private boolean createDatabase() {
        if (!unpackFileName()) return false;
        try {
            database = new Database(this);
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        if (!database.loadProperties()) return false;
        return database.connect();
    }

    private boolean unpackFileName() {
        if(System.getenv("FILE_PATH") == null) {
            System.out.println("Переменная окружения отсутствует! Коллекция не может быть загружена.");
            logger.log(Level.WARNING, "Переменная окружения отсутствует. Коллекция не может быть загружена.");
            return false;
        }
        fileName = System.getenv("FILE_PATH");
        if(fileName.equals("")) {
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
