package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class FFArm implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    ClawServo clawServo;
    WristServo wristServo;
    ShoulderServo shoulderServo;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public FFArm(HardwareMap hardwareMap, Telemetry telemetry) {
        clawServo = new ClawServo(hardwareMap, telemetry);
        wristServo = new WristServo(hardwareMap, telemetry);
        shoulderServo = new ShoulderServo(hardwareMap, telemetry);
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
    public void openClaw() {
        clawServo.open();
    }

    public void closeClaw() {
        clawServo.close();
    }

    public void pickup() {
        shoulderServo.down();
        wristServo.pickup();
        clawServo.openPlusDelay();
    }

    public void carry() {
    }

    public void storage() {
        shoulderServo.storage();
        wristServo.storage();
        clawServo.close();
    }

    public void dropoff() {
    }

    public void hold() {
    }

    public boolean isPositionReached() {
        boolean answer = false;
        if (shoulderServo.isPositionReached() && wristServo.isPositionReached() && clawServo.isPositionReached()) {
            answer = true;
        }
        return answer;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isInitComplete() {
        return false;
    }

    @Override
    public boolean init(Configuration config) {
        return false;
    }

    @Override
    public void update() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void setDataLog(DataLogging logFile) {

    }

    @Override
    public void enableDataLogging() {

    }

    @Override
    public void disableDataLogging() {

    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }
}


