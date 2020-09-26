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
    private String result;
    private Processor processor;

    public RegisterUser(Processor processor) {
        login = processor.getCommandData().getParam1();
        password = processor.getCommandData().getParam2();
        database = processor.getDatabase();
        this.processor = processor;
    }

    @Override
    public String execute() {
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
        processor.setResult(result);
        return result;
    }
}
