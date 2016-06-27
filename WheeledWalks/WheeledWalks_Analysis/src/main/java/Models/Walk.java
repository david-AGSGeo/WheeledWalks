package Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



/**
 * Created by David on 12/05/2016.
 */

@JsonIgnoreProperties({ "LogFile" })
public class Walk {

    private String name;   //the name of the walk
    private Long dateSurveyed; //the date the recording was started
    private Float length; //the total length of the walk as recorded by the gps
    private Float rating;  //the user's rating of the walk (0-5)
    private int grade; //the objective difficulty of the walk (1-5) determined from the data
    private String description; // a brief description of the walk
    private String locality; //the suburb or national park in which the walk is located
    private String imageFilePath; //the absolute filepath of the thumnail image
    private String logFilePath; //the absolute filepath of the log file


    public Walk() {  //empty constructor for firebase

    }

    //Getters

    public String getName() {
        return name;
    }

    public long getDateSurveyed() {
        return dateSurveyed;
    }

    public Float getLength() {
        return length;
    }

    public float getRating() {
        return rating;
    }

    public int getGrade() {
        return grade;
    }

    public String getDescription() {
        return description;
    }

    public String getLocality() {
        return locality;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

}
