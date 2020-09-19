package person;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private static final long serialVersionUID = 2L;
    private double x;
    private double y; //Максимальное значение поля: 355

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
