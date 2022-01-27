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

    private enum State {
        // states for returning to hold position (storage with element)
        // the idea is to get the arm to slow down before it arrives at the hold position so it
        // does not overshoot as badly and bang into the robot
        IDLE,
        MOVING_TO_HOLD_PRE_POSITION,
        AT_HOLD_PRE_POSITION,
        MOVING_TO_HOLD_FINAL_POSITION,
        AT_HOLD_POSITION
    }

    private State state;

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
        state = State.IDLE;
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
        //shoulderMotor.hold();
        // start the movement of the shoulder motor to the hold preposition
        shoulderMotor.holdPrePosition();
        // setup to run the state machine associated with this command
        state = State.MOVING_TO_HOLD_PRE_POSITION;
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
        // the state machine being IDLE means that any state machine associated with a command has
        // finished running. Since some commands have state machines and other commands do not, we
        // need to check both isPositionReached (for the commands without state machines) and
        // state == IDLE (for the commands that have a state machine)
        if (shoulderMotor.isPositionReached() && state == State.IDLE && wristServo.isPositionReached() && clawServo.isPositionReached()) {
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
        switch (state) {
            case IDLE:
                // we just be hanging out in this state waiting for someone to tell us to do
                // somethin
                break;

            // These next states are to implement to the command storageWithElement()
            // The idea is to move the arm to a position that is higher than the shoulder motor
            // hold position, let it arrive there and once it does arrive there, then move it more
            // slowly into the final hold position. Hopefully this eliminates the overshoot that the
            // arm has when moving to the hold position. The better way to do this would be to
            // implement a PIDF with F accounting for the force of gravity. But there is only so
            // much time ...
            case MOVING_TO_HOLD_PRE_POSITION:
                // is the hold pre-position reached yet?
                if (shoulderMotor.isPositionReached()) {
                    // yes so now command the shoulder motor to go to the final hold position
                    shoulderMotor.holdFinalPosition();
                    state = State.MOVING_TO_HOLD_FINAL_POSITION;
                }
                // if not then just keep looping in this state until the pre position is reached
                break;
            case MOVING_TO_HOLD_FINAL_POSITION:
                if (shoulderMotor.isPositionReached()) {
                    // yes so the arm has arrived at the final hold position. The shoulder is now
                    // at IDLE waiting for a new command
                    state = State.IDLE;
                }
                // if not then just keep looping in this state until the hold position is reached
                break;

                // any states associated with other commands will get inserted into the state machine
        }
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


