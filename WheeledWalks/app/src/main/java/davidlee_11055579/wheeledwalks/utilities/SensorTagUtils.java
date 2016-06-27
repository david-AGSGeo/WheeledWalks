package davidlee_11055579.wheeledwalks.utilities;

import java.nio.ByteBuffer;

/**
 * Created by David on 10/05/2016.
 */
public class SensorTagUtils {
    public static String ConvertMovementDataToString(byte[] rawData){
        String formattedData = "";
        for (int i = 0; i < rawData.length; i+=2){
            byte[] byteArray = {rawData[i+1],rawData[i]}; //TODO: check if this is right!
            ByteBuffer wrapped = ByteBuffer.wrap(byteArray);
            short result = wrapped.getShort();
            formattedData += String.valueOf(result) + ",";
        }


        return formattedData;
    }


}
