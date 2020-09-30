package command;

import server.Database;
import server.Processor;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

public class RegisterUser extends Command {
    private String login;
    private String password;
    private Database database;

    public RegisterUser(Processor processor, CommandData commandData) {
        login = commandData.getParam1();
        password = commandData.getParam2();
        database = processor.getDatabase();
    }

    @Override
    public String execute() {
        String result;
        try {
            PreparedStatement preparedStatement = database.getConnection().prepareStatement("INSERT INTO userdata (login, password) VALUES (?, ?);");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, Base64.getEncoder().encodeToString(database.getHash().digest(password.getBytes(StandardCharsets.UTF_8))));
            preparedStatement.execute();
            result = "Пользователь под логином " + login + " успешно зарегистрирован!\n";
        } catch (SQLException e) {
            e.printStackTrace();
            result = "Ошибка регистрации! Попробуйте использовать другой логин.\n";
        }
        return result;
    }
}
