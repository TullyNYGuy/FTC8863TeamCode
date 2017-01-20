package org.firstinspires.ftc.teamcode.Lib.VelocityVortex;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServo;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServoGB;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

public class FrontBeaconPusher {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum BeaconPusherState {
        BOTH_BACK,
        MOVING_TO_BOTH_BACK,
        BOTH_MIDDLE,
        MOVING_TO_BOTH_MIDDLE,
        LEFT_BACK_RIGHT_FORWARD,
        MOVING_TO_LEFT_BACK_RIGHT_FORWARD,
        LEFT_FORWARD_RIGHT_BACK,
        MOVING_TO_LEFT_FORWARD_RIGHT_BACK,
        BOTH_FORWARD,
        MOVING_TO_BOTH_FORWARD;
    }


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private BeaconPusherState beaconPusherState = BeaconPusherState.BOTH_BACK;

    private CRServo leftCRServo;
    private CRServo rightCRServo;

    private double frontLeftServoCenterValueForward = .5;
    private double frontLeftServoCenterValueReverse = .5;
    private double frontRightServoCenterValueForward = .5;
    private double frontRightServoCenterValueReverse = .5;
    private double deadband = .1;

    private boolean complete = false;

    private AdafruitColorSensor8863 rightColorSensor;

    private double pusherMidPoint = 3.2; //measured in cm using the robot

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

    public FrontBeaconPusher(HardwareMap hardwareMap, Telemetry telemetry) {
        leftCRServo = new CRServo(RobotConfigMappingForGenericTest.getFrontLeftBeaconServo(),
                hardwareMap, frontLeftServoCenterValueForward, frontLeftServoCenterValueReverse,
                deadband, Servo.Direction.FORWARD, telemetry);
        rightCRServo = new CRServo(RobotConfigMappingForGenericTest.getFrontRightBeaconServo(),
                hardwareMap, frontRightServoCenterValueForward, frontRightServoCenterValueReverse,
                deadband, Servo.Direction.REVERSE, telemetry);
        // add the creation of color sensor object
        // check the positions and make sure that the pushers are both back against the limit
        // switches
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

    public void moveBothPushersBack() {
        // change the state
        beaconPusherState = BeaconPusherState.MOVING_TO_BOTH_BACK;
        updateState();
    }

    public void moveLeftPusherForwardRightPusherForward() {
        leftCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.FORWARD);
        rightCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.FORWARD);
        // set the next state
        beaconPusherState = BeaconPusherState.BOTH_FORWARD;
        // indicate that it has not gotten to the state yet
        complete = false;
        updateState();
    }

    public void moveLeftPusherForwardRightPusherBack() {
        leftCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.FORWARD);
        rightCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.BACKWARD);
        // set the next state
        beaconPusherState = BeaconPusherState.LEFT_FORWARD_RIGHT_BACK;
        // indicate that it has not gotten to the state yet
        complete = false;
        updateState();
    }

    public void moveLeftPusherBackRightPusherForward() {
        leftCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.BACKWARD);
        rightCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.FORWARD);
        // set the next state
        beaconPusherState = BeaconPusherState.LEFT_BACK_RIGHT_FORWARD;
        // indicate that it has not gotten to the state yet
        complete = false;
        updateState();
    }

    public void moveLeftPusherMidwayRightPusherMidway() {

    }

    private BeaconPusherState updateState() {
        CRServo.CRServoState leftCRServoState;
        CRServo.CRServoState rightCRServoState;
        leftCRServoState = leftCRServo.update();
        rightCRServoState = rightCRServo.update();
        switch(beaconPusherState) {
            case BOTH_BACK:
                // do nothing - until someone issues a command
                break;
            case MOVING_TO_BOTH_BACK:
                // if both servos are at the back now then change state to BOTH_BACK
                if (leftCRServoState == CRServo.CRServoState.BACK_AT_POSITION &&
                        rightCRServoState == CRServo.CRServoState.BACK_AT_POSITION) {
                    beaconPusherState = BeaconPusherState.BOTH_BACK;
                } else {
                    // if the left servo is not at the back and is not moving to the back already
                    // then start it moving
                    if (leftCRServoState != CRServo.CRServoState.MOVING_BACK_TO_POSITION &&
                            leftCRServoState != CRServo.CRServoState.BACK_AT_SWITCH) {
                        leftCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.BACKWARD);
                    }
                    // if the right servo is not at the back and is not moving to the back already
                    // then start it moving
                    if (rightCRServoState != CRServo.CRServoState.MOVING_BACK_TO_POSITION &&
                            rightCRServoState != CRServo.CRServoState.BACK_AT_POSITION) {
                        rightCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.BACKWARD);
                    }
                    // the only other possibility is that the servos are moving. IN that case just
                    // do nothing until they finish moving. Could put a timer here to check to see
                    // if they have been moving too long which would mean something is wrong.
                }
                break;
            case BOTH_MIDDLE:
                // do nothing - until someone issues a command
                break;
            case MOVING_TO_BOTH_MIDDLE:
                break;
            case LEFT_BACK_RIGHT_FORWARD:
                // do nothing - until someone issues a command
                break;
            case MOVING_TO_LEFT_BACK_RIGHT_FORWARD:
                break;
            case LEFT_FORWARD_RIGHT_BACK:
                // do nothing - until someone issues a command
                break;
            case MOVING_TO_LEFT_FORWARD_RIGHT_BACK:
                break;
            case BOTH_FORWARD:
                // do nothing - until someone issues a command
                break;
            case MOVING_TO_BOTH_FORWARD:
                break;
            default:
                break;
        }
        return beaconPusherState;
    }

}
