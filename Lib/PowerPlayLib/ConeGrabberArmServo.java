package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ServoPosition;

import java.util.concurrent.TimeUnit;

@Config
public class ConeGrabberArmServo {

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
    private Servo8863New coneGrabberArmServo;
    private final String CONE_GRABBER_ARM_SERVO_NAME = PowerPlayRobot.HardwareName.CONE_GRABBER_ARM_SERVO.hwName;

    public static double INIT_POSITION = 0.245;
    public static double RELEASE_POSITION = 0.875;
    public static double PICKUP = .895;
    public static double LINEUP_FOR_PICKUP = .825;
    public static double CARRY = .45;

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

    public ConeGrabberArmServo(HardwareMap hardwareMap, Telemetry telemetry) {
        coneGrabberArmServo = new Servo8863New(CONE_GRABBER_ARM_SERVO_NAME, hardwareMap, telemetry);
        coneGrabberArmServo.addPosition("Init", INIT_POSITION, 1000, TimeUnit.MILLISECONDS);
        coneGrabberArmServo.addPosition("Release", RELEASE_POSITION, 1000, TimeUnit.MILLISECONDS);
        coneGrabberArmServo.addPosition("Pickup", PICKUP, 250, TimeUnit.MILLISECONDS);;
        coneGrabberArmServo.addPosition("LineupForPickup", LINEUP_FOR_PICKUP, 1000, TimeUnit.MILLISECONDS);
        coneGrabberArmServo.addPosition("Carry", CARRY, 500, TimeUnit.MILLISECONDS);
        coneGrabberArmServo.addPosition("Replacement", 1.0, 1000, TimeUnit.MILLISECONDS);
        //coneGrabberArmServo.setDirection(Servo.Direction.REVERSE);
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

    public void init() {
        coneGrabberArmServo.setPosition("Init");
    }
    
    public void releasePosition() {
        coneGrabberArmServo.setPosition("Release");
    }

    public void lineupForPickupPosition() {
        coneGrabberArmServo.setPosition("LineupForPickup");
    }

    public void pickupPosition() {
        coneGrabberArmServo.setPosition("Pickup");
    }

    public void carryPosition() {
        coneGrabberArmServo.setPosition("Carry");
    }

    public void replacementPosition() {
        coneGrabberArmServo.setPosition("Replacement");
    }

    public boolean isPositionReached() {
        return coneGrabberArmServo.isPositionReached();
    }

    // wrappers
    public ServoPosition getServoPosition(String positionName) {
        return coneGrabberArmServo.getServoPosition(positionName);
    }

    public void changeServoPosition(String positionName, double position) {
        coneGrabberArmServo.changePosition(positionName, position);
    }



    public void testPositionUsingJoystick(LinearOpMode opmode) {
        coneGrabberArmServo.testPositionsUsingJoystick(opmode);
    }
}
