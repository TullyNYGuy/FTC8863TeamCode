package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;

import java.util.concurrent.TimeUnit;

public class ConeGrabberServo {

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
    private Servo8863New coneGrabberServo;
    private final String CONE_GRABBER_SERVO_NAME = PowerPlayRobot.HardwareName.CONE_GRABBER_SERVO.hwName;
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

    public ConeGrabberServo(HardwareMap hardwareMap, Telemetry telemetry) {
        coneGrabberServo = new Servo8863New(CONE_GRABBER_SERVO_NAME, hardwareMap, telemetry);
        coneGrabberServo.addPosition("Init", 0.0, 2000, TimeUnit.MILLISECONDS);
        coneGrabberServo.addPosition("Storage", .95,2000, TimeUnit.MILLISECONDS);
        coneGrabberServo.addPosition("Open", .64, 2000, TimeUnit.MILLISECONDS);
        coneGrabberServo.addPosition("Close", .75, 2000, TimeUnit.MILLISECONDS);
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
        coneGrabberServo.setPosition("Init");
    }

    public void store() {
        coneGrabberServo.setPosition("Storage");
    }

    public void open() {
        coneGrabberServo.setPosition("Open");
    }

    public void close() {
        coneGrabberServo.setPosition("Close");
    }

    public boolean isPositionReached() {
        return coneGrabberServo.isPositionReached();
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        coneGrabberServo.testPositionsUsingJoystick(opmode);
    }
}
