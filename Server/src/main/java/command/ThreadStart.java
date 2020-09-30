package command;

public class ThreadStart extends Command {
    public ThreadStart() {

    }

    @Override
    public String execute() {
        try {
            Thread a = new Thread();
            a.start();
            Thread.sleep(100000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Тред поспал";
    }
}
