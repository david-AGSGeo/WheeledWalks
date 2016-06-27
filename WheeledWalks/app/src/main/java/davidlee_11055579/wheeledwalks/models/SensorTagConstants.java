package davidlee_11055579.wheeledwalks.models;

import java.util.UUID;

/**
 * Created by David on 20/04/2016.
 */
public class SensorTagConstants {
    private static final String DEVICE_NAME = "SensorTag";

    /* Movement Service */
    public static final UUID MOVEMENT_SERVICE = UUID.fromString("f000aa80-0451-4000-b000-000000000000");
    public static final UUID MOVEMENT_DATA_CHAR = UUID.fromString("f000aa81-0451-4000-b000-000000000000");
    public static final UUID MOVEMENT_CONFIG_CHAR = UUID.fromString("f000aa82-0451-4000-b000-000000000000");
    public static final UUID MOVEMENT_PERIOD_CHAR = UUID.fromString("f000aa83-0451-4000-b000-000000000000");


    /* Humidity Service */
    public static final UUID HUMIDITY_SERVICE = UUID.fromString("f000aa20-0451-4000-b000-000000000000");
    public static final UUID HUMIDITY_DATA_CHAR = UUID.fromString("f000aa21-0451-4000-b000-000000000000");
    public static final UUID HUMIDITY_CONFIG_CHAR = UUID.fromString("f000aa22-0451-4000-b000-000000000000");

    /* Barometric Pressure Service */
    public static final UUID PRESSURE_SERVICE = UUID.fromString("f000aa40-0451-4000-b000-000000000000");
    public static final UUID PRESSURE_DATA_CHAR = UUID.fromString("f000aa41-0451-4000-b000-000000000000");
    public static final UUID PRESSURE_CONFIG_CHAR = UUID.fromString("f000aa42-0451-4000-b000-000000000000");
    public static final UUID PRESSURE_CAL_CHAR = UUID.fromString("f000aa43-0451-4000-b000-000000000000");

    /* Accelerometer Service */
    public static final UUID ACCELEROMETER_SERVICE = UUID.fromString("f000aa10-0451-4000-b000-000000000000");
    public static final UUID ACCELEROMETER_DATA_CHAR = UUID.fromString("f000aa11-0451-4000-b000-000000000000");
    public static final UUID ACCELEROMETER_CONFIG_CHAR = UUID.fromString("f000aa12-0451-4000-b000-000000000000");
    public static final UUID ACCELEROMETER_PERIOD_CHAR = UUID.fromString("f000aa13-0451-4000-b000-000000000000");

    /* Gyroscope Service */
    public static final UUID GYROSCOPE_SERVICE = UUID.fromString("f000aa50-0451-4000-b000-000000000000");
    public static final UUID GYROSCOPE_DATA_CHAR = UUID.fromString("f000aa51-0451-4000-b000-000000000000");
    public static final UUID GYROSCOPE_CONFIG_CHAR = UUID.fromString("f000aa52-0451-4000-b000-000000000000");
    public static final UUID GYROSCOPE_PERIOD_CHAR = UUID.fromString("f000aa53-0451-4000-b000-000000000000");

    /* Client Configuration Descriptor */
    public static final UUID CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
}
