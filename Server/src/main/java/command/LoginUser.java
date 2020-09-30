package command;

import server.Database;
import server.Processor;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class LoginUser extends Command {
    private String login;
    private String password;
    private Database database;

    public LoginUser(Processor processor, CommandData commandData) {
        login = commandData.getParam1();
        password = commandData.getParam2();
        database = processor.getDatabase();
    }

    @Override
    public String execute() {
        String result;
        try {
            PreparedStatement preparedStatement = database.getConnection().prepareStatement("SELECT * FROM userdata WHERE (login = ?);");
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if(!Base64.getEncoder().encodeToString(database.getHash().digest(password.getBytes(StandardCharsets.UTF_8)))
                    .equals(resultSet.getString("password"))) {
                System.out.println(Base64.getEncoder().encodeToString(database.getHash().digest(password.getBytes(StandardCharsets.UTF_8))));
                System.out.println(resultSet.getString("password"));
                result = "Ошибка авторизации. Неверный пароль.\n";
                return result;
            }
            result = "Пользователь под логином " + login + " успешно авторизован!\n";
        } catch (SQLException e) {
            e.printStackTrace();
            result = "Ошибка авторизации. Неверный логин или пароль.\n";
        }
        return result;
    }
}
