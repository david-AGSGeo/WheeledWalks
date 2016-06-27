package davidlee_11055579.wheeledwalks.models;

/**
 * Created by David on 26/04/2016.
 */
public class BtDeviceDbConstants {
    //Bluetooth Device Database Name
    public static final String DATABASE_NAME = "WW_BT_deviceDB.db";

    //Database Version
    public static final int DATABASE_VERSION = 1;

    //Walks Table Name
    public static final String TABLE_DEVICES = "devices_table";

    //Walks Table Columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DEVICE_ADDRESS = "device_address";
    public static final String COLUMN_DEVICE_ALIAS = "device_alias";
    public static final String COLUMN_DEVICE_SENSOR_ENABLE = "device_sensor_enable";



    //walks table array
    public static final String[] DEVICE_TABLE_COLUMNS = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_DEVICE_ADDRESS,
            COLUMN_DEVICE_ALIAS,
            COLUMN_DEVICE_SENSOR_ENABLE

    };

    // Database creation sql statement
    public static final String DATABASE_CREATE = "create table "
            + TABLE_DEVICES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT not null, "
            + COLUMN_DEVICE_ADDRESS + " TEXT not null, "
            + COLUMN_DEVICE_ALIAS + " TEXT not null, "
            + COLUMN_DEVICE_SENSOR_ENABLE + " INTEGER not null"
            + ");";
}
