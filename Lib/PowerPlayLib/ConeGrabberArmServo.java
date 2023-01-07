package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;

import java.util.concurrent.TimeUnit;

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
        coneGrabberArmServo.addPosition("Init", .22, 2000, TimeUnit.MILLISECONDS);
        coneGrabberArmServo.addPosition("Release", .80, 2000, TimeUnit.MILLISECONDS);
        coneGrabberArmServo.addPosition("Pickup", .850, 2000, TimeUnit.MILLISECONDS);;
        coneGrabberArmServo.addPosition("LineupForPickup", .70, 2000, TimeUnit.MILLISECONDS);
        coneGrabberArmServo.addPosition("Carry", .37, 2000, TimeUnit.MILLISECONDS);
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

    public boolean isPositionReached() {
        return coneGrabberArmServo.isPositionReached();
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        coneGrabberArmServo.testPositionsUsingJoystick(opmode);
    }
}
