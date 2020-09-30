package server;

import collection.People;
import command.CommandData;
import person.Coordinates;
import person.Location;
import person.Person;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;

public class Database {
    private Connection connection;
    private PreparedStatement preparedStatement;
    private MessageDigest hash;
    private String propertiesFile;
    private Processor processor;
    private String url;
    private String systemLogin;
    private String systemPassword;

    public Database(Processor processor) throws NoSuchAlgorithmException {
        hash = MessageDigest.getInstance("SHA-512");
        this.processor = processor;
        propertiesFile = processor.getFileName();
    }

    /**
     * Загрузка данных для входа в БД.
     * @return - было ли успешно произведено действие
     */
    public boolean loadProperties() {
        try (FileInputStream propertiesInputStream = new FileInputStream(propertiesFile)) {
            Properties properties = new Properties();
            properties.load(propertiesInputStream);
            url = properties.getProperty("location");
            systemLogin = properties.getProperty("login");
            systemPassword = properties.getProperty("password");
            return true;
        } catch(IOException e) {
            processor.getLogger().log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Подключение к БД.
     * @return - было ли успешно произведено действие
     */
    public boolean connect() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, systemLogin, systemPassword);
            return true;
        } catch(ClassNotFoundException | SQLException e) {
            processor.getLogger().log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Загрузка коллекции из БД.
     * @return - было ли успешно произведено действие
     */
    public boolean load() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM people;");
            while(resultSet.next()) {
                Person person = new Person();
                Coordinates coordinates = new Coordinates();
                Location location = new Location();
                String key = resultSet.getString("key");

                person.setId(resultSet.getInt("id"));
                person.setName(resultSet.getString("name"));
                coordinates.setX(resultSet.getDouble("coordinatex"));
                coordinates.setY(resultSet.getDouble("coordinatey"));
                person.setCoordinates(coordinates);
                person.setCreationDate(resultSet.getString("creationdate").replaceAll("T", " "));
                person.setHeight(resultSet.getInt("height"));
                if(resultSet.getString("birthday").equals("null")) person.setBirthday(null);
                else person.setBirthday(resultSet.getString("birthday").replaceAll("T", " "));
                person.setWeight(resultSet.getLong("weight"));
                person.setNationality(People.convertToCountry(resultSet.getString("nationality")));
                if(resultSet.getBoolean("locationexistance")) {
                    location.setX(resultSet.getLong("locationx"));
                    location.setY(resultSet.getFloat("locationy"));
                    location.setZ(resultSet.getDouble("locationz"));
                    person.setLocation(location);
                } else person.setLocation(null);
                person.setLogin(resultSet.getString("login"));

                processor.getCollection().get().put(key, person);
            }
            return true;
        } catch(SQLException e) {
            processor.getLogger().log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Добавление элемента в БД.
     * @param key - ключ элемента
     * @param person - элемент
     * @return - было ли успешно произведено действие
     */
    public boolean put(String key, Person person) {
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO people (key, id, name, " +
                    "coordinatex, coordinatey, creationdate, height, birthday, weight, nationality, " +
                    "locationexistance, locationx, locationy, locationz, login) VALUES (?, nextval('id'), ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?);");
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, person.getName());
            preparedStatement.setDouble(3, person.getCoordinates().getX());
            preparedStatement.setDouble(4, person.getCoordinates().getY());
            preparedStatement.setString(5, person.getCreationDate().toString());
            preparedStatement.setInt(6, person.getHeight());
            if(person.getBirthday() == null) preparedStatement.setString(7, "null");
            else preparedStatement.setString(7, person.getBirthday().toString());
            preparedStatement.setLong(8, person.getWeight());
            preparedStatement.setString(9, person.getNationality().toString());
            if(person.getLocation() == null) {
                preparedStatement.setBoolean(10, false);
                preparedStatement.setLong(11, 0);
                preparedStatement.setFloat(12, 0);
                preparedStatement.setDouble(13, 0);
            } else {
                preparedStatement.setBoolean(10, true);
                preparedStatement.setLong(11, person.getLocation().getX());
                preparedStatement.setFloat(12, person.getLocation().getY());
                preparedStatement.setDouble(13, person.getLocation().getZ());
            }
            preparedStatement.setString(14, person.getLogin());
            preparedStatement.execute();
            return true;
        } catch(SQLException e) {
            processor.getLogger().log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Очистка коллекции.
     * @param commandData - объект с данными о команде
     * @return - было ли успешно произведено действие
     */
    public boolean clear(CommandData commandData) {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM people WHERE id > 0 AND login = ?;");
            preparedStatement.setString(1, commandData.getLogin());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            processor.getLogger().log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Исполнение команды RemoveGreater.
     * @param coordinateX - координата Х
     * @param commandData - объект с данными о команде
     * @return - было ли успешно произведено действие
     */
    public boolean removeGreater(double coordinateX, CommandData commandData) {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM people WHERE coordinatex < ? AND login = ?;");
            preparedStatement.setDouble(1, coordinateX);
            preparedStatement.setString(2, commandData.getLogin());
            return preparedStatement.execute();
        } catch(SQLException e) {
            processor.getLogger().log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Исполнение команды removeGreaterKey
     * @param key - ключ элемента
     * @param commandData - объект с данными о команде
     * @return - было ли успешно произведено действие
     */
    public boolean removeGreaterKey(String key, CommandData commandData) {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM people WHERE key > ? AND login = ?;");
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, commandData.getLogin());
            return preparedStatement.execute();
        } catch(SQLException e) {
            processor.getLogger().log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Удаление элемента из БД.
     * @param key - ключ элемента
     * @param commandData - объект с данными о команде
     * @return - было ли успешно произведено действие
     */
    public boolean remove(String key, CommandData commandData) {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM people WHERE key = ? AND login = ?;");
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, commandData.getLogin());
            preparedStatement.execute();
            return true;
        } catch(SQLException e) {
            processor.getLogger().log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Исполнение команды replaceIfLower.
     * @param key - ключ элемента
     * @param height - введенный рост
     * @param commandData - объект с данными о команде
     * @return - было ли успешно произведено действие
     */
    public boolean replaceIfLower(String key, int height, CommandData commandData) {
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM people WHERE key = ? AND login = ?;");
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, commandData.getLogin());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if(resultSet.getInt("height") > height) {
                preparedStatement = connection.prepareStatement("UPDATE people SET height = ? WHERE key = ? AND login = ?;");
                preparedStatement.setInt(1, height);
                preparedStatement.setString(2, key);
                preparedStatement.setString(3, commandData.getLogin());
                preparedStatement.execute();
            }
            return true;
        } catch(SQLException e) {
            processor.getLogger().log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Проверка легитимности пользователя.
     * @param login - логин пользователя
     * @param hashedPassword - пароль пользователя
     * @return - было ли успешно произведено действие
     */
    public boolean checkUser(String login, String hashedPassword) {
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM userdata WHERE login = ?;");
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if(resultSet.getString("password").equals(hashedPassword)) return true;
            else {
                System.out.println("Попытка взлома! Пароли не совпадают!\n");
                return false;
            }
        } catch(SQLException e) {
            processor.getLogger().log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Получение ID элемента из последовательности в БД.
     * @param key - ключ элемента
     * @return - полученный ID
     */
    public int gainId(String key) {
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM people WHERE key = ?;");
            preparedStatement.setString(1, key);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("id");
        } catch (SQLException e) {
            processor.getLogger().log(Level.SEVERE, "Ошибка!");
            e.printStackTrace();
            return 0;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public MessageDigest getHash() {
        return hash;
    }
}
