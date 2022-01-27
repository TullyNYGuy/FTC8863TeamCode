package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

import java.util.Timer;

public class FFArm implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum ArmCommand {
        DOWN,
        UP,

    }

    private enum ClawState {
        OPEN,
        CLOSE,
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    ClawServo clawServo;
    WristServo wristServo;
    ShoulderMotor shoulderMotor;
    private final String ARM_NAME = "Arm";
    private DataLogging logFile;
    private boolean loggingOn = false;
    private Boolean initComplete = false;
    private ArmCommand armCommand;
    private ClawState clawState;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public FFArm(HardwareMap hardwareMap, Telemetry telemetry) {
        clawServo = new ClawServo(hardwareMap, telemetry);
        wristServo = new WristServo(hardwareMap, telemetry);
        shoulderMotor = new ShoulderMotor(hardwareMap, telemetry);

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
        clawState = ClawState.OPEN;
    }

    public void closeClaw() {
        clawServo.close();
        clawState = ClawState.CLOSE;
    }

    public void toggleClaw() {
        if (clawState == ClawState.OPEN) {
            closeClaw();
        } else if (clawState == ClawState.CLOSE) {
            openClaw();
        }
    }
/* This is used when we are going to pick up the team shipping element. The claw is lined up with the
top of the team shipping element at a flat angle. The shoulder is positioned downwards and the wrist
is also positioned down. */


    public void closeAndUp(){
        switch(armCommand) {
            case DOWN:

            case UP:
        }
    }
    public void pickup() {
        shoulderMotor.down();
        wristServo.pickup();
        clawServo.open();
        clawState = ClawState.OPEN;

    }
/* The shoulder is in the up position holding the team shipping element while the wrist is in the
carry position. It is used when we need to drive to the team shipping hub to cap it on top of it. */

   /* public void carry() {
        shoulderMotor.up();
        wristServo.hold();
        clawServo.close();
        clawState = ClawState.CLOSE;
    }*/


    public void lineUp() {
        shoulderMotor.up();
        wristServo.lineUp();
        clawServo.close();
        clawState = ClawState.CLOSE;
    }
/* The arm is stored behind the robot supported on a small beam. This is used when we do not need
to use the arm. */

    public void storage() {
        shoulderMotor.storage();
        wristServo.storage();
        clawServo.close();
        clawState = ClawState.CLOSE;
    }


    public void storageWithElement() {
        shoulderMotor.hold();
        wristServo.hold();
        clawServo.close();
        clawState = ClawState.CLOSE;
    }
/* The shoulder is positioned in a upward direction over the team shipping hub so that the claw may
open to release the team shipping element. The wrist is positioned in a downward position, and the
claw is positioned so that it is level with the team shipping hub over it. */

    public void dropoff() {
        shoulderMotor.up();
        wristServo.dropOff();
        clawServo.open();
        clawState = ClawState.OPEN;
    }

    /**
     * This is used to hold the team shipping element between the auto and teleop portions of the
     * game. It is the same as storage except that the wrist is point up to the sky
     */
    public void hold() {
        shoulderMotor.storage();
        wristServo.hold();
        clawServo.close();
        clawState = ClawState.CLOSE;
    }


    //make a command backlog thing?
    public boolean isPositionReached() {
        boolean answer = false;
        if (shoulderMotor.isPositionReached() && wristServo.isPositionReached() && clawServo.isPositionReached()) {
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
        return isPositionReached();
    }

    @Override
    public boolean init(Configuration config) {
        storage();
        initComplete = isPositionReached();
        return initComplete;
    }

    @Override
    public void update() {
        shoulderMotor.update();
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


