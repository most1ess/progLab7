package client;

import command.CommandData;

import java.util.Scanner;

public class User {
    private String login = null;
    private String password = null;

    /**
     * Авторизация пользователя.
     * @param processor - процессор клиента
     */
    public void sign(Processor processor) {
        String signStatus;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.print("Если у вас есть аккаунт в базе данных, напишите команду 'log' для авторизации." +
                    "\nЕсли у вас нет аккаунта, зарегистрируйтесь с помощью" +
                    "команды 'reg'.\n>");
            signStatus = sc.nextLine();
            while (!(signStatus.equals("reg") || signStatus.equals("log"))) {
                System.out.print("Пока вы можете использовать только команды 'log' и 'reg'.\nПовторите ввод.\n>");
                signStatus = sc.nextLine();
            }

            if (signStatus.equals("reg")) Sender.send(processor, register(processor));
            else Sender.send(processor, login(processor));
        } while (!Receiver.receive(processor));
    }

    /**
     * Регистрация.
     * @param processor - процессор клиента
     * @return - объект с данными о команде
     */
    private CommandData register(Processor processor) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Для регистрации введите имя пользователя:\n>");
        login = sc.nextLine();
        while(login == null || login.contains(" ")) {
            System.out.print("Имя пользователя не может быть пустым.\nПовторите ввод.\n>");
            login = sc.nextLine();
        }

        return new CommandData("registerUser", login, enter(), processor);
    }

    /**
     * Логинирование
     * @param processor - процессор клиента
     * @return - объект с данными о команде
     */
    private CommandData login(Processor processor) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Для авторизации введите имя пользователя:\n>");
        login = sc.nextLine();
        while(login == null || login.contains(" ")) {
            System.out.print("Имя пользователя не может быть пустым.\nПовторите ввод.\n>");
            login = sc.nextLine();
        }

        return new CommandData("loginUser", login, enter(), processor);
    }

    /**
     * Ввод пароля
     * @return - введенный пароль
     */
    private String enter() {
        Scanner sc = new Scanner(System.in);
        String confirmPassword;

        do {
            if(password != null) System.out.print("Пароли не совпадают! Попробуйте еще раз.\n");
            System.out.print("Введите пароль:\n>");
            password = sc.nextLine();
            while (password == null || password.contains(" ")) {
                System.out.print("Пароль не может быть пустым.\nПовторите ввод.\n>");
                password = sc.nextLine();
            }
            System.out.print("Повторите пароль:\n>");
            confirmPassword = sc.nextLine();
        } while(!password.equals(confirmPassword));

        return password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
