package Models;

/**
 * Created by David on 16/05/2016.
 */
public class Constants {
    public static final String ROOT_DIRECTORY = "WheeledWalks";
    public static final String LOG_FILE_DIRECTORY = "WheeledWalks/LogFiles";
    public static final String LOG_FILE_HEADER_FORMAT = "#WALK_NAME: %s \n#WALK_LOCALITY: %s\n#SURVEY_DATE: %d\n";
    public static final String LOG_FILE_FOOTER_FORMAT = "#Rating: %.1f stars\n#grade: %d\n#Length: %.1f meters\n#Description: %s\n#Thumbnail: %s\n";
    public static final String LOG_FILE_EXTENSION = ".txt";
    public static final String LOG_DATA_FORMAT = "#%s,@%d,%s\n";
    public static final String LOG_ACCEL_TAG = "ACCEL_TAG";
    public static final String LOG_GPS_TAG = "GPS_TAG";
    public static final String LOG_MARKER_TAG = "MARKER_TAG";
    public static final String LOG_HEARTRATE_TAG = "HEARTRATE_TAG";
    public static final String LOG_SENSORTAG_TAG = "SENSORTAG_TAG";
    public static final String LOG_DEVICE_TAG = "CONNECTED_DEVICE";
}
