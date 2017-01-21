package org.firstinspires.ftc.teamcode.Lib.VelocityVortex;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServo;
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
        BOTH_MIDDLE,
        LEFT_BACK_RIGHT_FORWARD,
        LEFT_FORWARD_RIGHT_BACK,
        BOTH_FORWARD;
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

   // private AdafruitColorSensor8863 rightColorSensor;

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

    public void moveLeftPusherBackRightPusherBack() {
        leftCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.BACKWARD);
        rightCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.BACKWARD);
        leftCRServo.update();
        rightCRServo.update();
    }

    public void moveLeftPusherForwardRightPusherForward() {
        leftCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.FORWARD);
        rightCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.FORWARD);
        leftCRServo.update();
        rightCRServo.update();
    }

    public void moveLeftPusherForwardRightPusherBack() {
        leftCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.FORWARD);
        rightCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.BACKWARD);
        leftCRServo.update();
        rightCRServo.update();
    }

    public void moveLeftPusherBackRightPusherForward() {
        leftCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.BACKWARD);
        rightCRServo.moveUntilLimitSwitch(CRServo.CRServoDirection.FORWARD);
        leftCRServo.update();
        rightCRServo.update();
    }

    public void moveLeftPusherMidwayRightPusherMidway() {

    }


}
