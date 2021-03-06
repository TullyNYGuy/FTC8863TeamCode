package org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServo;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.MuxPlusColorSensors;
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
        MOVING_TO_BOTH_FORWARD,
        UNKNOWN,
        MOVING_TO_BOTH_BACK_THEN_TO_BOTH_MIDDLE;
    }

    public enum BeaconColor {
        BLUE_RED,
        RED_BLUE,
        BLUE_BLUE,
        RED_RED,
        UNKNOWN_RED,
        UNKNOWN_BLUE,
        RED_UNKNOWN,
        BLUE_UNKNOWN,
        UNKNOWN_UNKNOWN,
    }


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private BeaconPusherState beaconPusherState = BeaconPusherState.BOTH_BACK;
    private BeaconPusherState lastBeaconPusherState = BeaconPusherState.BOTH_BACK;

    public CRServo leftCRServo;
    private CRServo rightCRServo;

    private MuxPlusColorSensors muxPlusColorSensors;

    private double frontLeftServoNoMovePositionForward = .51;
    private double frontLeftServoNoMovePositionReverse = .48;
    private double frontRightServoNoMovePositionForward = .51;
    private double frontRightServoNoMovePositionReverse = .48;
    private double deadband = .1;

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

    public FrontBeaconPusher(HardwareMap hardwareMap, Telemetry telemetry, MuxPlusColorSensors muxPlusColorSensors) {
        leftCRServo = new CRServo(RobotConfigMappingForGenericTest.getFrontLeftBeaconServoName(),
                hardwareMap, frontLeftServoNoMovePositionForward, frontLeftServoNoMovePositionReverse,
                deadband, Servo.Direction.REVERSE,
                RobotConfigMappingForGenericTest.getLeftFrontLimitSwitchName(), Switch.SwitchType.NORMALLY_OPEN,
                RobotConfigMappingForGenericTest.getLeftBackLimitSwitchName(), Switch.SwitchType.NORMALLY_OPEN,
                telemetry);
        rightCRServo = new CRServo(RobotConfigMappingForGenericTest.getFrontRightBeaconServoName(),
                hardwareMap, frontRightServoNoMovePositionForward, frontRightServoNoMovePositionReverse,
                deadband, Servo.Direction.FORWARD,
                RobotConfigMappingForGenericTest.getRightFrontLimitSwitchName(), Switch.SwitchType.NORMALLY_OPEN,
                RobotConfigMappingForGenericTest.getRightBackLimitSwitchName(), Switch.SwitchType.NORMALLY_OPEN,
                telemetry);
        initialize();
        this.muxPlusColorSensors = muxPlusColorSensors;
    }

    private void initialize() {
        // set the rate of speed for each servo and direction
        leftCRServo.setForwardCMPerSecond(2.96);
        leftCRServo.setBackwardCMPerSecond(3.21);
        rightCRServo.setForwardCMPerSecond(1.90);
        rightCRServo.setBackwardCMPerSecond(2.07);

        // determine the current state and if it is not known, put it into the default state
        // Can't do this because there is no update called in a loop to determine when the limit
        // switches are hit. NEED TO FIND A SOLUTION!
        beaconPusherState = findBeaconPusherState();
        if (beaconPusherState == BeaconPusherState.UNKNOWN) {
            //moveBothPushersBack();
            //updateState();
        }
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

    // --------------------------------------------------
    // COLOR SENSOR METHODS
    //---------------------------------------------------

    public BeaconColor getBeaconColor() {
        BeaconColor result = BeaconColor.UNKNOWN_UNKNOWN;
        switch (muxPlusColorSensors.getSimpleColorFrontBeaconPusherRightColorSensor()) {
            case BLUE:
                switch (muxPlusColorSensors.getSimpleColorFrontBeaconPusherLeftColorSensor()) {
                    case BLUE:
                        result = BeaconColor.BLUE_BLUE;
                        break;
                    case RED:
                        result = BeaconColor.BLUE_RED;
                        break;
                    case GREEN:
                    case UNKNOWN:
                        result = BeaconColor.BLUE_UNKNOWN;
                        break;
                }
                break;
            case RED:
                switch (muxPlusColorSensors.getSimpleColorFrontBeaconPusherLeftColorSensor()) {
                    case BLUE:
                        result = BeaconColor.RED_BLUE;
                        break;
                    case RED:
                        result = BeaconColor.RED_RED;
                        break;
                    case GREEN:
                    case UNKNOWN:
                        result = BeaconColor.RED_UNKNOWN;
                        break;
                }
                break;
            case GREEN:
            case UNKNOWN:
                switch (muxPlusColorSensors.getSimpleColorFrontBeaconPusherLeftColorSensor()) {
                    case BLUE:
                        result = BeaconColor.UNKNOWN_BLUE;
                        break;
                    case RED:
                        result = BeaconColor.UNKNOWN_RED;
                        break;
                    case GREEN:
                    case UNKNOWN:
                        result = BeaconColor.UNKNOWN_UNKNOWN;
                        break;
                }
                break;
        }
        return result;
    }

    public String rgbValuesScaledAsString(MuxPlusColorSensors.BeaconSide side) {
        if (side == MuxPlusColorSensors.BeaconSide.RIGHT) {
            return muxPlusColorSensors.frontBeaconPusherRightColorSensorRGBValuesScaledAsString();
        } else {
            return muxPlusColorSensors.frontBeaconPusherRightColorSensorRGBValuesScaledAsString();
        }
    }

    public void displayBeaconColor(Telemetry telemetry) {
        telemetry.addData("Beacon Color (right / left = ", getBeaconColor().toString());
    }

    public boolean isRightColorSensorBlue() {
        return muxPlusColorSensors.frontBeaconPusherRightColorSensorIsBlue();
    }

    public boolean isRightColorSensorRed() {
        return muxPlusColorSensors.frontBeaconPusherRightColorSensorIsRed();
    }

    public void setCoreDimLEDToMatchColorSensor(MuxPlusColorSensors.BeaconSide side) {
        muxPlusColorSensors.setCoreDimLedToMatchColorSensor(side);
    }

    //-------------------------------------------------------------------------------
    // PUSHER METHODS
    //-------------------------------------------------------------------------------

    /**
     * This method will try to determine what the current state of the pushers is when we don't
     * know what it is. This will be called from initialize and hopefully never again.
     * @return current state of the pushers
     */
    // NEED TO CHANGE BACK TO PRIVATE
    public BeaconPusherState findBeaconPusherState() {
        CRServo.CRServoState leftCRServoState = leftCRServo.findCRServoState();
        CRServo.CRServoState rightCRServoState = rightCRServo.findCRServoState();
        // check the limit switches first
        if (leftCRServoState == CRServo.CRServoState.FORWARD_AT_SWITCH &&
                rightCRServoState == CRServo.CRServoState.FORWARD_AT_SWITCH) {
            return BeaconPusherState.BOTH_FORWARD;
        }
        if (leftCRServoState == CRServo.CRServoState.BACK_AT_SWITCH &&
                rightCRServoState == CRServo.CRServoState.BACK_AT_SWITCH) {
            return BeaconPusherState.BOTH_BACK;
        }
        if (leftCRServoState == CRServo.CRServoState.FORWARD_AT_SWITCH &&
                rightCRServoState == CRServo.CRServoState.BACK_AT_SWITCH) {
            return BeaconPusherState.LEFT_FORWARD_RIGHT_BACK;
        }
        if (leftCRServoState == CRServo.CRServoState.BACK_AT_SWITCH &&
                rightCRServoState == CRServo.CRServoState.FORWARD_AT_SWITCH) {
            return BeaconPusherState.LEFT_BACK_RIGHT_FORWARD;
        }
        // if both servos are at a position between the limit switches, assume that they are at
        // the middle
        if ((leftCRServoState == CRServo.CRServoState.FORWARD_AT_POSITION ||
                leftCRServoState == CRServo.CRServoState.BACK_AT_POSITION) &&
                (rightCRServoState == CRServo.CRServoState.FORWARD_AT_POSITION ||
                rightCRServoState == CRServo.CRServoState.BACK_AT_POSITION)) {
            return BeaconPusherState.BOTH_MIDDLE;
        }
        // if the states are any of these, then they are not really valid
        // left = XX_AT_POSITION, right = XX_AT_SWITCH
        // left = XX_AT_SWITCH, right = XX_AT_POSITION
        // left = MOVING_XX, right = MOVING_XX
        return BeaconPusherState.UNKNOWN;
    }

    // UNKNOWN state - how does it impact movements?
    // If the beacon pusher is initialized and the left and right pushers are not against the limit
    // switches, it is not possible to know where they are. In this case the state is UNKNOWN. It is
    // not possible to correct this until the robot loop starts up. Any movement of the pushers has
    // to have an update() called in order to determine when the limit switches are hit. The update()
    // can only be called from an opmode loop. So it is possible to make a call to one of the
    // following commands to move the pushers when the pushers are starting from an UNKNOWN state
    // or location. For commands that tell the pushers to move to a switch, this is not a problem.
    // We don't need to know where they are starting from in order to finish at the proper location,
    // which is against the switch. But for any command the is moving the pusher a distance, this is
    // a problem. We don't know where we are starting from. So the workaround is to first move the
    // pushers to a known spot, then move the pushers the distance.

    //---------------------------------------------------------------
    // Commands
    //---------------------------------------------------------------

    public void moveBothPushersBack() {
        // change the state
        beaconPusherState = BeaconPusherState.MOVING_TO_BOTH_BACK;
        updateState();
    }

    public void moveBothPushersForward() {
        // change the state
        beaconPusherState = BeaconPusherState.MOVING_TO_BOTH_FORWARD;
        updateState();
    }

    public void moveLeftPusherForwardRightPusherBack() {
        beaconPusherState = BeaconPusherState.MOVING_TO_LEFT_FORWARD_RIGHT_BACK;
        updateState();
    }

    public void moveLeftPusherBackRightPusherForward() {
        beaconPusherState = BeaconPusherState.MOVING_TO_LEFT_BACK_RIGHT_FORWARD;
        updateState();
    }

    public void moveBothMidway() {
        // As discussed above, if the pushers are in an UNKNOWN state or location, then this command
        // has to put the pushers into a known location, then move to the middle.
        // Also, I cannot handle the situation where the pusher is already moving because I don't know
        // its location. Without knowing its location there is no way to tell how to move it to the
        // middle. So I lock out this command if the pushers are moving.
        if (isStationary()) {
            // we are ok to start a move
            lastBeaconPusherState = beaconPusherState;
            beaconPusherState = BeaconPusherState.MOVING_TO_BOTH_MIDDLE;
        }
        if (beaconPusherState == BeaconPusherState.UNKNOWN) {
            // move the pushers to the back, then to the middle
            beaconPusherState = BeaconPusherState.MOVING_TO_BOTH_BACK_THEN_TO_BOTH_MIDDLE;
        }
        updateState();
    }

    //----------------------------------------------------------
    // State machine
    //----------------------------------------------------------

    // NEED TO HANLDE UNKNOWN STATE IN EACH STATE SO THE PUSHERS CAN BE PUT INTO KNOWN STATES
    public BeaconPusherState updateState() {
        CRServo.CRServoState leftCRServoState;
        CRServo.CRServoState rightCRServoState;
        leftCRServoState = leftCRServo.update();
        rightCRServoState = rightCRServo.update();
        switch (beaconPusherState) {
            case BOTH_BACK:
                // do nothing - until someone issues a command
                break;
            case MOVING_TO_BOTH_BACK:
                // if both servos are at the back now then change state to BOTH_BACK
                if (leftCRServoState == CRServo.CRServoState.BACK_AT_SWITCH &&
                        rightCRServoState == CRServo.CRServoState.BACK_AT_SWITCH) {
                    beaconPusherState = BeaconPusherState.BOTH_BACK;
                } else {
                    // if the left servo is not at the back and is not moving to the back already
                    // then start it moving
                    if (leftCRServoState != CRServo.CRServoState.MOVING_BACK_TO_SWITCH &&
                            leftCRServoState != CRServo.CRServoState.BACK_AT_SWITCH) {
                        leftCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.BACKWARD);
                    }
                    // if the right servo is not at the back and is not moving to the back already
                    // then start it moving
                    if (rightCRServoState != CRServo.CRServoState.MOVING_BACK_TO_SWITCH &&
                            rightCRServoState != CRServo.CRServoState.BACK_AT_SWITCH) {
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
                // if both servos are at the destination then change the state
                if (leftCRServo.isAtPosition() && rightCRServo.isAtPosition() ) {
                    beaconPusherState = BeaconPusherState.BOTH_MIDDLE;
                } else {
                    // if the left servo is not at the middle and is not moving already then start it
                    // moving.
                    if (leftCRServoState == CRServo.CRServoState.BACK_AT_SWITCH) {
                        leftCRServo.startMoveDistance(pusherMidPoint, CRServo.CRServoDirection.FORWARD);
                    }
                    if (leftCRServoState == CRServo.CRServoState.FORWARD_AT_SWITCH) {
                        leftCRServo.startMoveDistance(pusherMidPoint, CRServo.CRServoDirection.BACKWARD);
                    }
                    // if the right servo is not at the middle and is not moving already the start it
                    // moving.
                    if (rightCRServoState == CRServo.CRServoState.BACK_AT_SWITCH) {
                        rightCRServo.startMoveDistance(pusherMidPoint, CRServo.CRServoDirection.FORWARD);
                    }
                    if (rightCRServoState == CRServo.CRServoState.FORWARD_AT_SWITCH) {
                        rightCRServo.startMoveDistance(pusherMidPoint, CRServo.CRServoDirection.BACKWARD);
                    }
                }
                // the only other possibility is that the servos are moving. IN that case just
                // do nothing until they finish moving. Could put a timer here to check to see
                // if they have been moving too long which would mean something is wrong.
                break;
            case LEFT_BACK_RIGHT_FORWARD:
                // do nothing - until someone issues a command
                break;
            case MOVING_TO_LEFT_BACK_RIGHT_FORWARD:
                // if both servos are at the destination then change the state
                if (leftCRServoState == CRServo.CRServoState.BACK_AT_SWITCH &&
                        rightCRServoState == CRServo.CRServoState.FORWARD_AT_SWITCH) {
                    beaconPusherState = BeaconPusherState.LEFT_BACK_RIGHT_FORWARD;
                } else {
                    // if the left servo is not at the back and is not moving to the back already
                    // then start it moving
                    if (leftCRServoState != CRServo.CRServoState.MOVING_BACK_TO_SWITCH &&
                            leftCRServoState != CRServo.CRServoState.BACK_AT_SWITCH) {
                        leftCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.BACKWARD);
                    }
                    // if the right servo is not at the front and is not moving to the front already
                    // then start it moving
                    if (rightCRServoState != CRServo.CRServoState.MOVING_FORWARD_TO_SWITCH &&
                            rightCRServoState != CRServo.CRServoState.FORWARD_AT_SWITCH) {
                        rightCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.FORWARD);
                    }
                    // the only other possibility is that the servos are moving. IN that case just
                    // do nothing until they finish moving. Could put a timer here to check to see
                    // if they have been moving too long which would mean something is wrong.
                }
                break;
            case LEFT_FORWARD_RIGHT_BACK:
                // do nothing - until someone issues a command
                break;
            case MOVING_TO_LEFT_FORWARD_RIGHT_BACK:
                // if both servos are at the destination then change the state
                if (leftCRServoState == CRServo.CRServoState.FORWARD_AT_SWITCH &&
                        rightCRServoState == CRServo.CRServoState.BACK_AT_SWITCH) {
                    beaconPusherState = BeaconPusherState.LEFT_FORWARD_RIGHT_BACK;
                } else {
                    // if the left servo is not at the front and is not moving to the front already
                    // then start it moving
                    if (leftCRServoState != CRServo.CRServoState.MOVING_FORWARD_TO_SWITCH &&
                            leftCRServoState != CRServo.CRServoState.FORWARD_AT_SWITCH) {
                        leftCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.FORWARD);
                    }
                    // if the right servo is not at the back and is not moving to the back already
                    // then start it moving
                    if (rightCRServoState != CRServo.CRServoState.MOVING_BACK_TO_SWITCH &&
                            rightCRServoState != CRServo.CRServoState.BACK_AT_SWITCH) {
                        rightCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.BACKWARD);
                    }
                    // the only other possibility is that the servos are moving. IN that case just
                    // do nothing until they finish moving. Could put a timer here to check to see
                    // if they have been moving too long which would mean something is wrong.
                }
                break;
            case BOTH_FORWARD:
                // do nothing - until someone issues a command
                break;
            case MOVING_TO_BOTH_FORWARD:
                // if both servos are at the back now then change state to BOTH_BACK
                if (leftCRServoState == CRServo.CRServoState.FORWARD_AT_SWITCH &&
                        rightCRServoState == CRServo.CRServoState.FORWARD_AT_SWITCH) {
                    beaconPusherState = BeaconPusherState.BOTH_FORWARD;
                } else {
                    // if the left servo is not forward and is not moving to forward already
                    // then start it moving
                    if (leftCRServoState != CRServo.CRServoState.MOVING_FORWARD_TO_SWITCH &&
                            leftCRServoState != CRServo.CRServoState.FORWARD_AT_SWITCH) {
                        leftCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.FORWARD);
                    }
                    // if the right servo is not at the back and is not moving to the back already
                    // then start it moving
                    if (rightCRServoState != CRServo.CRServoState.MOVING_FORWARD_TO_SWITCH &&
                            rightCRServoState != CRServo.CRServoState.FORWARD_AT_SWITCH) {
                        rightCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.FORWARD);
                    }
                    // the only other possibility is that the servos are moving. IN that case just
                    // do nothing until they finish moving. Could put a timer here to check to see
                    // if they have been moving too long which would mean something is wrong.
                }
                break;
            case UNKNOWN:
                // if the pushers are in an unknown state then the only thing to do is get them
                // into a known position.
                beaconPusherState = BeaconPusherState.MOVING_TO_BOTH_BACK;
                break;
            case MOVING_TO_BOTH_BACK_THEN_TO_BOTH_MIDDLE:
                // This state moves the pushers to the back and then move them to the middle. The
                // state can be gotten into when the State is UNKNOWN and a command to move to the
                // middle is recieved. Moving to the back puts the pushers in a known state and
                // from there they can be moved to the middle.

                // if both servos are at the back now then move them to the middle
                if (leftCRServoState == CRServo.CRServoState.BACK_AT_SWITCH &&
                        rightCRServoState == CRServo.CRServoState.BACK_AT_SWITCH) {
                    beaconPusherState = BeaconPusherState.MOVING_TO_BOTH_MIDDLE;
                } else {
                    // if the left servo is not at the back and is not moving to the back already
                    // then start it moving
                    if (leftCRServoState != CRServo.CRServoState.MOVING_BACK_TO_SWITCH &&
                            leftCRServoState != CRServo.CRServoState.BACK_AT_SWITCH) {
                        leftCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.BACKWARD);
                    }
                    // if the right servo is not at the back and is not moving to the back already
                    // then start it moving
                    if (rightCRServoState != CRServo.CRServoState.MOVING_BACK_TO_SWITCH &&
                            rightCRServoState != CRServo.CRServoState.BACK_AT_SWITCH) {
                        rightCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.BACKWARD);
                    }
                    // the only other possibility is that the servos are moving. IN that case just
                    // do nothing until they finish moving. Could put a timer here to check to see
                    // if they have been moving too long which would mean something is wrong.
                }
                break;
            default:
                break;
        }
        return beaconPusherState;
    }

    //---------------------------------------------------------------
    // Status methods
    //---------------------------------------------------------------

    public boolean isMoving() {
        if (beaconPusherState == BeaconPusherState.MOVING_TO_BOTH_MIDDLE ||
                beaconPusherState == BeaconPusherState.MOVING_TO_BOTH_BACK ||
                beaconPusherState == BeaconPusherState.MOVING_TO_BOTH_FORWARD ||
                beaconPusherState == BeaconPusherState.MOVING_TO_LEFT_BACK_RIGHT_FORWARD ||
                beaconPusherState == BeaconPusherState.MOVING_TO_LEFT_FORWARD_RIGHT_BACK) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isStationary() {
        if (beaconPusherState == BeaconPusherState.BOTH_MIDDLE ||
                beaconPusherState == BeaconPusherState.BOTH_FORWARD ||
                beaconPusherState == BeaconPusherState.BOTH_BACK ||
                beaconPusherState == BeaconPusherState.LEFT_BACK_RIGHT_FORWARD ||
                beaconPusherState == BeaconPusherState.LEFT_FORWARD_RIGHT_BACK) {
            return true;
        } else {
            return false;
        }
    }

}
