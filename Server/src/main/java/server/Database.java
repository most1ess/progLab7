package server;

import collection.People;
import person.Coordinates;
import person.Country;
import person.Location;
import person.Person;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.ZoneId;
import java.util.Properties;

public class Database {
    private Connection connection;
    private PreparedStatement preparedStatement;
    private MessageDigest hash;
    private String propertiesFile;
    private Processor processor;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public void setPreparedStatement(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    public MessageDigest getHash() {
        return hash;
    }

    public void setHash(MessageDigest hash) {
        this.hash = hash;
    }

    public String getPropertiesFile() {
        return propertiesFile;
    }

    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSystemLogin() {
        return systemLogin;
    }

    public void setSystemLogin(String systemLogin) {
        this.systemLogin = systemLogin;
    }

    public String getSystemPassword() {
        return systemPassword;
    }

    public void setSystemPassword(String systemPassword) {
        this.systemPassword = systemPassword;
    }

    private String url;
    private String systemLogin;
    private String systemPassword;

    public Database(Processor processor) throws NoSuchAlgorithmException {
        hash = MessageDigest.getInstance("SHA-512");
        this.processor = processor;
        propertiesFile = processor.getFileName();
    }

    public boolean loadProperties() {
        try (FileInputStream propertiesInputStream = new FileInputStream(propertiesFile)) {
            Properties properties = new Properties();
            properties.load(propertiesInputStream);
            url = properties.getProperty("location");
            systemLogin = properties.getProperty("login");
            systemPassword = properties.getProperty("password");
            return true;
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean connect() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, systemLogin, systemPassword);
            return true;
        } catch(ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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
            e.printStackTrace();
            return false;
        }
    }

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
            e.printStackTrace();
            return false;
        }
    }

    public boolean clear() {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM people WHERE id > 0 AND login = ?;");
            preparedStatement.setString(1, processor.getCommandData().getLogin());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeGreater(double coordinateX) {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM people WHERE coordinatex < ? AND login = ?;");
            preparedStatement.setDouble(1, coordinateX);
            preparedStatement.setString(2, processor.getCommandData().getLogin());
            preparedStatement.execute();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeGreaterKey(String key) {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM people WHERE key > ? AND login = ?;");
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, processor.getCommandData().getLogin());
            preparedStatement.execute();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean remove(String key) {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM people WHERE key = ? AND login = ?;");
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, processor.getCommandData().getLogin());
            preparedStatement.execute();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean replaceIfLower(String key, int height) {
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM people WHERE key = ? AND login = ?;");
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, processor.getCommandData().getLogin());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if(resultSet.getInt("height") > height) {
                preparedStatement = connection.prepareStatement("UPDATE people SET height = ? WHERE key = ? AND login = ?;");
                preparedStatement.setInt(1, height);
                preparedStatement.setString(2, key);
                preparedStatement.setString(3, processor.getCommandData().getLogin());
                preparedStatement.execute();
            }
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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
            e.printStackTrace();
            return false;
        }
    }
}
