package davidlee_11055579.wheeledwalks.controllers;

import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import davidlee_11055579.wheeledwalks.models.BtDeviceDbConstants;
import davidlee_11055579.wheeledwalks.models.Constants;

/**
 * Created by David on 26/04/2016.
 */
public class DevicesDatabaseHelper extends SQLiteOpenHelper {

    public DevicesDatabaseHelper(Context context) {
        super(context, BtDeviceDbConstants.DATABASE_NAME, null, BtDeviceDbConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BtDeviceDbConstants.DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DevicesDatabaseHelper.class.getName(), "Upgrading Database from version " +
                oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + BtDeviceDbConstants.TABLE_DEVICES);
        onCreate(db);
    }


    public void addDevice(BluetoothDevice device, String alias
                          ){
        ContentValues values = new ContentValues();
        values.put(BtDeviceDbConstants.COLUMN_NAME, device.getName());
        values.put(BtDeviceDbConstants.COLUMN_DEVICE_ADDRESS, device.getAddress());
        values.put(BtDeviceDbConstants.COLUMN_DEVICE_ALIAS, alias);


        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(BtDeviceDbConstants.TABLE_DEVICES, null, values);

        db.close();
        Log.w(Constants.DEBUG_TAG, "Bluetooth Device added to database: " + device.getName());
    }


    public void addOrUpdateDevice(BluetoothDevice device,
                                  String alias,
                                  Boolean sensorEnabled
                                 ) {
        ContentValues values = new ContentValues();
        values.put(BtDeviceDbConstants.COLUMN_NAME, device.getName());
        values.put(BtDeviceDbConstants.COLUMN_DEVICE_ADDRESS, device.getAddress());
        values.put(BtDeviceDbConstants.COLUMN_DEVICE_ALIAS, alias);
        values.put(BtDeviceDbConstants.COLUMN_DEVICE_SENSOR_ENABLE, (sensorEnabled) ? 1 : 0);

        SQLiteDatabase db = this.getWritableDatabase();


        db.delete(BtDeviceDbConstants.TABLE_DEVICES,
                BtDeviceDbConstants.COLUMN_DEVICE_ADDRESS + " = ?",
                new String[]{String.valueOf((device.getAddress()))});
        db.insert(BtDeviceDbConstants.TABLE_DEVICES, null, values);

        db.close();
        Log.w(Constants.DEBUG_TAG, "Bluetooth Device added to database: "
                + device.getName()
                + " with Alias: " + alias);
    }



    public String getAlias(String address) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(BtDeviceDbConstants.TABLE_DEVICES,
                BtDeviceDbConstants.DEVICE_TABLE_COLUMNS,
                "device_address = ?",
                new String[]{String.valueOf(address)},
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            String alias = cursor.getString(cursor.getColumnIndexOrThrow(BtDeviceDbConstants.COLUMN_DEVICE_ALIAS));
            cursor.close();
            db.close();
            return alias;
        } else {
            return "Unknown Device";
        }

    }



    public Boolean getSensorEnabled(String address) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(BtDeviceDbConstants.TABLE_DEVICES,
                BtDeviceDbConstants.DEVICE_TABLE_COLUMNS,
                "device_address = ?",
                new String[]{String.valueOf(address)},
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            int isEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(BtDeviceDbConstants.COLUMN_DEVICE_SENSOR_ENABLE));
            cursor.close();
            db.close();
            return (isEnabled != 0);
        } else {
            return false;
        }

    }



    /**
     * Returns a cursor with all devices in the database
     * @return - the cursor
     */
    public Cursor getAllDevices() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor results = db.rawQuery("SELECT * FROM " + BtDeviceDbConstants.TABLE_DEVICES, null);

        return results;
    }

    /**
     * Deletes a device from the database by address
     * @param address - the address of the device to delete
     */
    public void deleteDevice(String address) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(BtDeviceDbConstants.TABLE_DEVICES,
                BtDeviceDbConstants.COLUMN_DEVICE_ADDRESS + " = ?",
                new String[]{String.valueOf((address))});
        db.close();
    }
}
