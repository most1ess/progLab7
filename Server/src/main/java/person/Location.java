package person;

import java.io.Serializable;

public class Location implements Serializable, Comparable<Location> {
    private static final long serialVersionUID = 3L;
    private Long x; //Поле не может быть null
    private float y;
    private double z;

    public Long getX() {
        return x;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public int compareTo(Location location) {
        double firstSum = this.x + this.y + this.z;
        if(location == null) {
            return (int) firstSum;
        } else {
            double secondSum = location.getX() + location.getY() + location.getZ();
            return (int) (firstSum - secondSum);
        }
    }
}
