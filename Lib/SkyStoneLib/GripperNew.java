package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;

import java.util.concurrent.TimeUnit;

public class GripperNew implements FTCRobotSubsystem {

    private final static String SUBSYSTEM_NAME = "Gripper";

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
    private Servo8863New gripperServo;

    private double releasePosition = 0.43;
    private double gripPosition = 0.7;
    private double initPosition = releasePosition;

    private String initPositionName = "init";
    private String gripPositionName = "grip";
    private String releasePositionName = "release";

    private DataLogging logFile = null;
    private boolean loggingOn = false;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    //*********************************************************************************************
    //          Constructors
    //
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public GripperNew(HardwareMap hardwareMap, String servoName, Telemetry telemetry) {
        gripperServo = new Servo8863New(servoName, hardwareMap, telemetry);

        gripperServo.addPosition(initPositionName, initPosition, 1500, TimeUnit.MILLISECONDS);
        gripperServo.addPosition(gripPositionName, gripPosition, 1000, TimeUnit.MILLISECONDS);
        gripperServo.addPosition(releasePositionName, releasePosition, 1000, TimeUnit.MILLISECONDS);
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************


    private void log(String stringToLog) {
        if (logFile != null && loggingOn) {
            logFile.logData(stringToLog);

        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************


    @Override
    public String getName() {
        return SUBSYSTEM_NAME;
    }

    @Override
    public boolean init(Configuration config) {
        log("Gripper commanded to init");
        gripperServo.setPosition(initPositionName);
        return false;
    }

    @Override
    public boolean isInitComplete() {
        return gripperServo.isPositionReached(initPositionName);
    }

    public void releaseBlock() {
        log("Gripper commanded to release");
        gripperServo.setPosition(releasePositionName);
    }

    public boolean isReleaseComplete() {
        return gripperServo.isPositionReached(releasePositionName);
    }

    public void gripBlock() {
        log("Gripper commanded to grip");
        gripperServo.setPosition(gripPositionName);
    }

    public boolean isGripComplete() {
        return gripperServo.isPositionReached(gripPositionName);
    }

    @Override
    public void shutdown() {
        log("Gripper commanded to shutdown");
        releaseBlock();
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    @Override
    public void update() {
        // there is no state machine for this object
    }
}
