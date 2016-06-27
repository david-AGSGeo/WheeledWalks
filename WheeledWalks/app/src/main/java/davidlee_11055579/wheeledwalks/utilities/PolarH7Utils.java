package davidlee_11055579.wheeledwalks.utilities;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import davidlee_11055579.wheeledwalks.models.Constants;

/**
 * Created by David on 23/05/2016.
 */
public class PolarH7Utils {

    public static int getHeartRate(BluetoothGattCharacteristic characteristic) {
        int flag = characteristic.getProperties();
        int format;
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
            //Log.d(Constants.DEBUG_TAG, "Heart rate format UINT16.");
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
            //Log.d(Constants.DEBUG_TAG, "Heart rate format UINT8.");
        }
        final int heartRate = characteristic.getIntValue(format, 1);
        return heartRate;
    }

}
