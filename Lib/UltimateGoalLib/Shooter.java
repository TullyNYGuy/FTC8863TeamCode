package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class Shooter implements FTCRobotSubsystem {

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
    private DualMotorGearBox dualMotorGearBox;
    private DataLogging logFile;
    private boolean loggingOn = false;
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public Shooter(String leftMotorName, String rightMotorName, HardwareMap hardwareMap, Telemetry telemetry) {
        dualMotorGearBox = new DualMotorGearBox(leftMotorName, rightMotorName, hardwareMap, telemetry);
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public void setSpeed(int motorRPM) {
    dualMotorGearBox.setSpeed(motorRPM);
    }

    public double getSpeed() {
        return dualMotorGearBox.getSpeed();
    }

    public void stop(){
        dualMotorGearBox.stopGearbox();
    }

    @Override
    public String getName() {
        return "Shooter";
    }

    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public boolean init(Configuration config) {
        return true;
    }

    @Override
    public void update() {
        dualMotorGearBox.update();
    }

    @Override
    public void shutdown() {
        dualMotorGearBox.stopGearbox();
    }

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

    @Override
    public void timedUpdate(double timerValueMsec) {
    }
}
