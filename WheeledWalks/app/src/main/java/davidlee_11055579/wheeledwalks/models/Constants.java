/*
 * Copyright (C) 2015 David Lee WheeledWalks Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package davidlee_11055579.wheeledwalks.models;

import davidlee_11055579.wheeledwalks.R;

/**
 * Created by David on 22/09/2015.
 */
public class Constants {


    public static final int WALK_THUMBNAIL_REQUIRED_SIZE = 180;
    public static final String EMPTY_STRING = "";
    public static final char DEGREES_SYMBOL = '\u00B0'; //symbol for degrees
    public static final int ASYNCTASK_LINE_SLEEP_DELAY = 5;
    public static final int BLUETOOTH_SCAN_TIMEOUT_MILLIS = 3000;

    //LOG tags
    public static final String DEBUG_TAG = "DebugTag";
    public static final String BT_TAG = "BluetoothTag";

    //intent request codes
    public static final int LOAD_IMAGE_RESULTS = 42;
    public static final int REQUEST_SPEECH_RESULTS = 43;
    public static final int STEP_MARKER_REQUEST_CODE = 44;
    public static final int OBSTACLE_MARKER_REQUEST_CODE = 45;
    public static final int CAMERA_REQUEST_CODE = 46;
    public static final int BT_ENABLE_REQUEST_CODE = 51;

    //Intent filter Tags
    public static final String GPS_DATA_INTENT = "GpsDataIntent";
    public static final String ACCEL_DATA_INTENT = "AccelDataIntent";
    public static final String INTERFACE_INTENT = "InterfaceIntent";
    public static final String SENSOR_CONFIG_INTENT = "SensorConfigIntent";
    public static final String SENSOR_CONNECT_INTENT = "SensorConnectIntent";
    public static final String BT_SENSOR_DATA_INTENT = "BtSensorDataIntent";
    public static final String BT_SENSOR_CONNECTED_INTENT = "BtSensorConnectedIntent";

    public static final String RAW_DATA_REPLY = "RawDataReply";
    public static final String CONTROL_DATA_REPLY = "ControlDataReply";
    public static final String FINALISE_WALK_REPLY = "FinaliseReply";
    public static final String EDIT_DIALOG_FRAGMENT_TAG = "edit_dialog_fragment";
    public static final String SEARCH_DIALOG_FRAGMENT_TAG = "search_dialog_fragment";

    //Intent extra tags
    public static final String WALK_TAG = "walkTag";
    public static final String DEVICE_TAG = "deviceTag";
    public static final String SERVICE_IS_DESTROYED = "isDestroyed";
    public static final String GPS_LOCATION = "gpsLocation";
    public static final String GPS_LATTITUDE = "GpsLattitude";
    public static final String GPS_LONGITUDE = "GpsLongitude";
    public static final String ACCEL_DATA = "accelData";
    public static final String ACCEL_X = "AccelX";
    public static final String ACCEL_Y = "AccelY";
    public static final String ACCEL_Z = "AccelZ";
    public static final String WALK_DELETED = "walkDeleted";
    public static final String WALK_CHANGED = "walkChanged";
    public static final String FINALISE_WALK = "finaliseWalk";
    public static final String EXPORT_WALK = "exportWalk";
    public static final String LOG_MARKER = "logMarker";
    public static final String DELETE_ALL = "deleteAll";
    public static final String TOTAL_DISTANCE_TRAVELLED = "totalDistanceTravelled";
    public static final String SEARCH_STRING = "search_string";
    public static final String STEP_HEIGHT = "step_height";
    public static final String OBSTACLE_TYPE = "obstacle_type";
    public static final String OBSTACLE_IMAGE = "obstacle_image";
    public static final String LOG_MARKER_IMAGE_PATH = "logMarkerImageFilePath";
    public static final String DISCARD_RECORDING = "discardRecording";
    public static final String START_RECORDING = "startRecording";
    public static final String PAUSE_RECORDING = "pauseRecording";
    public static final String BT_DEVICE_TAG = "BtDevice";
    public static final String BT_SENSOR_ADDRESS = "BtDeviceAddress";
    public static final String BT_SENSORTAG_DATA = "BtSensortagDATA";
    public static final String BT_HEARTRATE_DATA = "BtHeartRateDATA";
    public static final String BT_SENSOR_CONNECTED = "BtDeviceConnected";
    public static final String BT_CONNECTIONS_COMPLETE = "BtConnectionsComplete";
    public static final String BT_TIMESTAMP = "BtTimestamp";



    //log file constantsB
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

    //Image File constants
    public static final String IMAGE_FILE_DIRECTORY = "WheeledWalks/Images";
    public static final String IMAGE_FILE_EXTENSION = ".png";
    public static final String DEFAULT_IMAGE_PATH = "default_image";


    //navigation bar titles and icons
    public static final String NAV_DRAWER_HEADER_TITLE = "Wheeled Walks";
    public static final String NAV_DRAWER_HEADER_SUBTEXT = "Getting outdoors on one or more wheels";
    public static final String NAV_DRAWER_TITLES[] = {
            "Add New Walk",
            "Settings",
            "Connect Sensor Tags",
            "Connect Heart Rate Monitor",
            "Sensor Calibration",
            "Delete All The Things!"};
    public static final int NAV_DRAWER_ICONS[] = {
            R.drawable.ic_add_walk,
            R.drawable.ic_settings,
            R.drawable.ic_connectbt,
            R.drawable.ic_heartrate,
            R.drawable.ic_calibrate,
            R.drawable.ic_delete};


    //generic display constants
    public static final String EDIT_WALK_DIALOG_TITLE = "Edit Walk";
    public static final String SEARCH_WALKS_DIALOG_TITLE = "Search Walks";
    public static final String BLUETOOTH_SETUP_DIALOG_TITLE = "Bluetooth Setup";

    public static final String ACCEL_DISPLAY_FORMAT = "%.4f%n";
    public static final String TAB_HEADER_MAP = "MAP";
    public static final String TAB_HEADER_RAW_DATA = "RAW DATA";
    public static final String TAB_HEADER_CONTROL = "CTRL";
    public static final String GRADE_STRINGS[] = {
            "Very Easy",
            "Easy",
            "Moderate",
            "Difficult",
            "Extreme"};
    public static final int GRADE_COLOURS[] = {
            R.color.grade_very_easy,
            R.color.grade_easy,
            R.color.grade_medium,
            R.color.grade_difficult,
            R.color.grade_extreme};
    public static final int MAX_BT_DEVICES = 7;


    //Unsorted :)

}
