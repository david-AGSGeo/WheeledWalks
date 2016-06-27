package Models;

/**
 * Created by David on 19/05/2016.
 */
public class SensorTagData {
    private String address;
    private Long timestamp;
    private int accelX;
    private int accelY;
    private int accelZ;

    private int gyroX;
    private int gyroY;
    private int gyroZ;

    private int compassX;
    private int compassY;
    private int compassZ;



    public SensorTagData() {
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getAccelX() {
        return accelX;
    }

    public void setAccelX(int accelX) {
        this.accelX = accelX;
    }

    public int getAccelY() {
        return accelY;
    }

    public void setAccelY(int accelY) {
        this.accelY = accelY;
    }

    public int getAccelZ() {
        return accelZ;
    }

    public void setAccelZ(int accelZ) {
        this.accelZ = accelZ;
    }

    public int getGyroX() {
        return gyroX;
    }

    public void setGyroX(int gyroX) {
        this.gyroX = gyroX;
    }

    public int getGyroY() {
        return gyroY;
    }

    public void setGyroY(int gyroY) {
        this.gyroY = gyroY;
    }

    public int getGyroZ() {
        return gyroZ;
    }

    public void setGyroZ(int gyroZ) {
        this.gyroZ = gyroZ;
    }

    public int getCompassX() {
        return compassX;
    }

    public void setCompassX(int compassX) {
        this.compassX = compassX;
    }

    public int getCompassY() {
        return compassY;
    }

    public void setCompassY(int compassY) {
        this.compassY = compassY;
    }

    public int getCompassZ() {
        return compassZ;
    }

    public void setCompassZ(int compassZ) {
        this.compassZ = compassZ;
    }
}
