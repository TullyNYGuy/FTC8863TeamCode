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
    private final String ARM_NAME = "Arm";
    private DataLogging logFile;
    private boolean loggingOn = false;
    private Boolean initComplete = false;
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
        initComplete = true;
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
/* This is used when we are going to pick up the team shipping element. The claw is lined up with the
top of the team shipping element at a flat angle. The shoulder is positioned downwards and the wrist
is also positioned down. */

    public void pickup() {
        shoulderServo.down();
        wristServo.pickup();
        clawServo.openPlusDelay();
    }

    public void carry() {
    }
/* The arm is stored behind the robot supported on a small beam. This is used when we do not need
to use the arm. */

    public void storage() {
        shoulderServo.storage();
        wristServo.storage();
        clawServo.close();

    }
/* The shoulder is positioned in a upward direction over the team shipping hub so that the claw may
open to release the team shipping element. The wrist is positioned in a downward position, and the
claw is positioned so that it is level with the team shipping hub over it. */

    public void dropoff() {
        shoulderServo.down();
        wristServo.dropOff();
        clawServo.open();
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
        return ARM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        return initComplete;
    }

    @Override
    public boolean init(Configuration config) {
        return true;
    }

    @Override
    public void update() {
    isPositionReached();
    }

    @Override
    public void shutdown() {
    storage();
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


