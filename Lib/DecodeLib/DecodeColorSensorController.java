package org.firstinspires.ftc.teamcode.Lib.DecodeLib;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Color;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDColorSensorA;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDColorSensorB;

public class DecodeColorSensorController implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods For Implementing FTCRobotSubsystem
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    /**
     * Property that holds a log file
     */
    private DataLogging logFile;

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    /**
     * Property that holds whether data is being logged into the log file.
     */
    private boolean loggingOn = false;

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    private String subsystemName;

    private ITDColorSensorA intakeColorSensorRight;
    private ITDColorSensorB intakeColorSensorLeft;
    private Color artifactColorRight = Color.UNKNOWN;
    private Color artifactColorLeft = Color.UNKNOWN;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public DecodeColorSensorController(HardwareMap hardwareMap, Telemetry telemetry) {
        intakeColorSensorRight = new ITDColorSensorA(hardwareMap, telemetry, "colorSensorRight");
        intakeColorSensorLeft = new ITDColorSensorB(hardwareMap, telemetry, "colorSensorLeft");
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * This method gets fresh color data from the color sensor. Communication with the sensor takes
     * time. So only call this once in each update. Don't combine it with getFreshDistanceFromSensor().
     */
    private void getFreshColorFromRightSensor() {
        intakeColorSensorRight.sensor.updateDataColor();
        // using the just updated HSV values, determine the color seen by the artifact
        artifactColorRight = intakeColorSensorRight.sensor.getMostLikelyColor();
        ;
        // logComment2("Front Sample color = " + artifactColorRight.toString());
    }

    private void getFreshDistanceFromRightSensor() {
        intakeColorSensorRight.sensor.updateDataDistance();
    }

    private void getFreshDistanceAndColorFromRightSensor() {
        intakeColorSensorRight.sensor.updateDataDistanceAndColor();
    }
    private void getFreshColorFromLeftSensor() {
        intakeColorSensorLeft.sensor.updateDataColor();
        // using the just updated HSV values, determine the color seen by the artifact
        artifactColorLeft = intakeColorSensorLeft.sensor.getMostLikelyColor();
        ;
        // logComment2("Front Sample color = " + artifactColorRight.toString());
    }

    private void getFreshDistanceFromLeftSensor() {
        intakeColorSensorLeft.sensor.updateDataDistance();
    }

    private void getFreshDistanceAndColorFromLeftSensor() {
        intakeColorSensorLeft.sensor.updateDataDistanceAndColor();
    }

    private boolean isArtifactPresentInLeftSensor() {
        if (intakeColorSensorLeft.sensor.getDistance(DistanceUnit.CM) < 5) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isArtifactPresentInRightSensor() {
        if (intakeColorSensorRight.sensor.getDistance(DistanceUnit.CM) < 7) {
            return true;
        } else {
            return false;
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public boolean isArtifactPresent() {
        GiveFreshData();
        if (isArtifactPresentInRightSensor() || isArtifactPresentInLeftSensor()) {
            return true;
        } else {
            return false;
        }
    }

    public void colorSensorsOn() {
        intakeColorSensorRight.sensor.turnSensorOn();
        intakeColorSensorLeft.sensor.turnSensorOn();
    }

    public void ColorSensorsOff() {
        intakeColorSensorRight.sensor.turnSensorOff();
        intakeColorSensorLeft.sensor.turnSensorOff();
    }
    public void GiveFreshData() {
        getFreshDistanceFromLeftSensor();
        getFreshDistanceFromRightSensor();
    }

    //*********************************************************************************************
    //          METHODS needed to implement FTCRobotSubsystem
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    @Override
    public void update() {
    }

    @Override
    public String getName() {
        return subsystemName;
    }

    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
    }

    @Override
    public boolean init(Configuration config) {
        return true;
    }
}
