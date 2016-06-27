package com.example.David.WheeledWalks.backend;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Models.BtDevice;
import Models.Constants;
import Models.HeartRateData;
import Models.SensorTagData;
import Models.TrackSegment;
import Models.Walk;

/**
 * Created by David on 14/05/2016.
 */
public class WalkProcessingServlet extends HttpServlet{

    private static final Logger log = Logger.getLogger(WalkProcessingServlet.class.getName());

    private Firebase mRawWalksRef;
    private Firebase mWalkLogRef;
    private Firebase mWalkInfoRef;

    private Firebase mSingleWalkLogRef;
    private Firebase mSingleWalkProcessedRef;

    final List<Walk> mProcessedWalks = new ArrayList<>(); //used for checking if walks already exist

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //log.info("running backend processing Service");
        super.service(req, resp);


        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        mRawWalksRef = new Firebase("https://wheeledwalks.firebaseio.com/Raw_Walks/Walks");
        mWalkInfoRef = new Firebase("https://wheeledwalks.firebaseio.com/Walk_Info/Walks");
        mWalkLogRef = new Firebase("https://wheeledwalks.firebaseio.com/Walk_Logs/Walks");

        mProcessedWalks.clear();
        //re-checks for processed walks. This is in case a processed walk
        // has been deleted manually so that it can be re-processed.
        mWalkInfoRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {

                    Walk newWalk = snapshot.getValue(Walk.class);
                    mProcessedWalks.add(newWalk);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                log.info("The read failed: " + firebaseError.getMessage());
            }
        });

        mRawWalksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //log.info("running onDataChanged in WarmupServlett");

                GenericTypeIndicator<List<String>> listType =
                        new GenericTypeIndicator<List<String>>() {};

                //First populate log files
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Walk newWalk = postSnapshot.getValue(Walk.class);
                    if (!walkExists(newWalk.getName())) { //check if  walk already exists
                        List<String> logStrings = postSnapshot.child("LogFile")
                                .getValue(listType);
                        if (logStrings != null) {

                            mSingleWalkLogRef = mWalkLogRef.child(newWalk.getName());
                            mSingleWalkProcessedRef =
                                    new Firebase("https://wheeledwalks.firebaseio.com/Processed/Walks")
                                            .child(newWalk.getName());
                            mSingleWalkProcessedRef.child("Info").setValue(newWalk);
                            log.info("calling ParseLogStrings for walk "
                                    + newWalk.getName());
                            parseLogStrings(logStrings);
                        }
                    } else {
                        log.info("Walk " + newWalk.getName()
                                + " Already exists, do not process");
                    }
                }
                //Then populate walk info - so it doesn't show up in info until it is finished processing
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Walk newWalk = postSnapshot.getValue(Walk.class);
                    if (!walkExists(newWalk.getName())) { //check if  walk already exists
                        mWalkInfoRef.child(newWalk.getName()).setValue(newWalk);
                    } else {
                        log.info("Walk " + newWalk.getName()
                                + " Already exists as a processed walk!");
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                log.info("The read failed: " + firebaseError.getMessage());
            }
        });
        //log.info("finished WalkProcessingServlet service");
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //log.info("doGet on WalkProcessingService called");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    private void parseLogStrings(List<String> logStrings){

        //Set up arraylists to store all the data temporarily
        //GPS data
        List<String> gpsStrings = new ArrayList<>();

        //Internal Accelerometer data (if used)
        List<Float> accelX = new ArrayList<>();
        List<Float> accelY = new ArrayList<>();
        List<Float> accelZ = new ArrayList<>();
        List<Long> accelTimestamps = new ArrayList<>();
        //Gps track stored as segments
        List<TrackSegment> trackSegments = new ArrayList<>();
        //Bluetooth data
        List<BtDevice> btDevices = new ArrayList<>();
        List<SensorTagData> btSensorTagData = new ArrayList<>();
        List<HeartRateData> btHeartRateData = new ArrayList<>();

        TrackSegment trackSegment = null;
        BtDevice btDevice;

        //log.info("starting to iterate through loop");
        for (String logString : logStrings){
            if (logString != null) {
                String dataTag = ((logString.split(","))[0]).substring(1); //separate out the
                // data tag by splitting the line on comma, and dropping the first "#" char

                switch (dataTag) {
                    case Constants.LOG_ACCEL_TAG:
                        accelTimestamps.add(getTimestamp(logString));
                        accelX.add(Float.parseFloat(((logString.split(","))[2]).substring(2)));
                        accelY.add(Float.parseFloat(((logString.split(","))[3]).substring(2)));
                        accelZ.add(Float.parseFloat(((logString.split(","))[4]).substring(2)));
                        break;

                    case Constants.LOG_GPS_TAG:
                        gpsStrings.add(logString);
                        if (trackSegment != null){
                            trackSegment.setEndTimestamp(getTimestamp(logString));
                            trackSegment.setEndLattitude(getLattitude(logString));
                            trackSegment.setEndLongitude(getLongitude(logString));
                            trackSegments.add(trackSegment);
                        }
                        trackSegment = new TrackSegment();
                        trackSegment.setStartTimestamp(getTimestamp(logString));
                        trackSegment.setStartLattitude(getLattitude(logString));
                        trackSegment.setStartLongitude(getLongitude(logString));
                        break;

                    case Constants.LOG_MARKER_TAG:
                        //TODO: Implement this
                        break;

                    case Constants.LOG_HEARTRATE_TAG:
                        HeartRateData heartRateData = new HeartRateData();
                        heartRateData.setTimestamp(getTimestamp(logString));
                        heartRateData.setHeartRate(Integer.parseInt(logString.split(",")[2]));
                        heartRateData.setAddress(logString.split(",")[3]);
                        btHeartRateData.add(heartRateData);
                        break;

                    case Constants.LOG_SENSORTAG_TAG:
                        SensorTagData data = new SensorTagData();
                        data.setTimestamp(getTimestamp(logString));
                        data.setGyroX(Integer.parseInt(((logString.split(","))[2])));
                        data.setGyroY(Integer.parseInt(((logString.split(","))[3])));
                        data.setGyroZ(Integer.parseInt(((logString.split(","))[4])));
                        data.setAccelX(Integer.parseInt(((logString.split(","))[5])));
                        data.setAccelY(Integer.parseInt(((logString.split(","))[6])));
                        data.setAccelZ(Integer.parseInt(((logString.split(","))[7])));
                        data.setCompassX(Integer.parseInt(((logString.split(","))[8])));
                        data.setCompassY(Integer.parseInt(((logString.split(","))[9])));
                        data.setCompassZ(Integer.parseInt(((logString.split(","))[10])));
                        data.setAddress((((logString.split(","))[11])));
                        btSensorTagData.add(data);
                        break;

                    case Constants.LOG_DEVICE_TAG:
                        btDevice = new BtDevice();
                        btDevice.setName((logString.split(","))[2]);
                        btDevice.setAddress((logString.split(","))[3]);
                        btDevice.setAlias((logString.split(","))[4]);
                        btDevices.add(btDevice);
                        break;
                    default:

                        break;
                }
            }

        }
        //log.info("writing to firebase");

        //WRITE DATA TO FIREBASE
        Firebase gpsRef = mSingleWalkLogRef.child("GPS");
        Firebase accelRef = mSingleWalkLogRef.child("Accelerometer");

        gpsRef.setValue(gpsStrings);
        accelRef.child("Timestamps").setValue(accelTimestamps);
        accelRef.child("X").setValue(accelX);
        accelRef.child("Y").setValue(accelY);
        accelRef.child("Z").setValue(accelZ);
        mSingleWalkProcessedRef.child("Track").setValue(trackSegments);
        mSingleWalkProcessedRef.child("Info").child ("Devices").setValue(btDevices);

        addBtDataToFirebase(btSensorTagData, btHeartRateData, mSingleWalkLogRef);
        log.info("...finished");
    }

    private void addBtDataToFirebase(List<SensorTagData> sensorTagDataList, List<HeartRateData> HeartDataList, Firebase dataRef){
        Firebase deviceRef;

        for( SensorTagData data: sensorTagDataList){
            deviceRef = dataRef.child(data.getAddress());
            deviceRef.child("Timestamps").push().setValue(data.getTimestamp());
            deviceRef.child("Gyroscope").child("X").push().setValue(data.getGyroX());
            deviceRef.child("Gyroscope").child("Y").push().setValue(data.getGyroY());
            deviceRef.child("Gyroscope").child("Z").push().setValue(data.getGyroZ());
            deviceRef.child("Accelerometer").child("X").push().setValue(data.getAccelX());
            deviceRef.child("Accelerometer").child("Y").push().setValue(data.getAccelY());
            deviceRef.child("Accelerometer").child("Z").push().setValue(data.getAccelZ());
            deviceRef.child("Compass").child("X").push().setValue(data.getCompassX());
            deviceRef.child("Compass").child("Y").push().setValue(data.getCompassY());
            deviceRef.child("Compass").child("Z").push().setValue(data.getCompassZ());
        }
        for( HeartRateData data: HeartDataList){
            deviceRef = dataRef.child(data.getAddress());
            deviceRef.child("Timestamps").push().setValue(data.getTimestamp());
            deviceRef.child("HeartRate").push().setValue(data.getHeartRate());
        }
    }


    private Long getTimestamp(String logString){
       return Long.parseLong(((logString.split(","))[1]).substring(1));
    }

    private Float getLattitude(String logString){
        return Float.parseFloat(((logString.split(","))[2]).substring(12));
    }

    private Float getLongitude(String logString){
        String temp = ((logString.split(","))[3]).substring(0);
        return Float.parseFloat(((temp.split(" "))[0]));
    }

    private boolean walkExists(String name){
        for (Walk walk: mProcessedWalks){
            if (name.equals(walk.getName())){
                return true;
            }
        }
        return false;
    }
}
