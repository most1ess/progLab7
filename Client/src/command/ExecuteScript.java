package command;

import client.Processor;
import client.Receiver;
import client.Sender;
import command.util.script.ScriptUtil;
import person.Person;

import java.io.*;

public class ExecuteScript {
    private String fileName;
    private Processor processor;

    public ExecuteScript(String fileName, Processor processor) {
        this.fileName = fileName;
        this.processor = processor;
    }

    /**
     * Метод, исполняюший скрипт из файла.
     * @return успешность исполнения скрипта.
     */
    public boolean execute() {
        ScriptUtil scriptUtil;
        try {
            boolean isPersonGood;
            File scriptFile = new File(fileName);
            if(scriptFile.canRead()) {
                FileReader fileReader = new FileReader(fileName);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                scriptUtil = new ScriptUtil(bufferedReader);
                String line = bufferedReader.readLine();
                String[] splitLine;
                while (line != null) {
                    splitLine = line.trim().split(" ");
                    if (splitLine[0].equals("insert") || splitLine[0].equals("update")) {
                        CommandData commandData = new CommandData(splitLine[0], splitLine[1]);
                        commandData.setHashedPassword(processor.getHashedPassword());
                        commandData.setLogin(processor.getLogin());
                        Sender.send(processor, commandData);
                        if (Receiver.receive(processor)) {
                            Person person;
                            scriptUtil = new ScriptUtil(bufferedReader);
                            isPersonGood = scriptUtil.genPerson();
                            person = scriptUtil.getPerson();
                            Sender.send(processor, person);
                        } else return false;
                        Receiver.receive(processor);
                        if(!isPersonGood) return false;
                    } else if (!processor.handle(splitLine)) {
                        System.out.println("Файл со скриптом содержит несуществующие команды или зацикливается.\n");
                        return false;
                    }
                    line = bufferedReader.readLine();
                }
            } else {
                System.out.println("Файл не может быть прочтён.\n");
                return false;
            }
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
