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

    public RegisterUser(Processor processor) {
        login = processor.getCommandData().getParam1();
        password = processor.getCommandData().getParam2();
        database = processor.getDatabase();
    }

    @Override
    public synchronized String execute() {
        try {
            PreparedStatement preparedStatement = database.getConnection().prepareStatement("INSERT INTO userdata (login, password) VALUES (?, ?);");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, Base64.getEncoder().encodeToString(database.getHash().digest(password.getBytes(StandardCharsets.UTF_8))));
            preparedStatement.execute();
            return "Пользователь под логином " + login + " успешно зарегистрирован!\n";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Ошибка регистрации! Попробуйте использовать другой логин.\n";
        }
    }
}
