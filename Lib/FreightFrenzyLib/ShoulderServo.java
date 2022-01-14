package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;

import java.util.concurrent.TimeUnit;

public class ShoulderServo {

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
    private Servo8863New shoulderServo;
    private final String SHOULDER_SERVO_NAME = FreightFrenzyRobotRoadRunner.HardwareName.SHOULDER_SERVO.hwName;
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
    public ShoulderServo(HardwareMap hardwareMap, Telemetry telemetry) {
        shoulderServo = new Servo8863New(SHOULDER_SERVO_NAME, hardwareMap, telemetry);
        shoulderServo.addPosition("up", .56, 2000, TimeUnit.MILLISECONDS);
        shoulderServo.addPosition("down", .14, 1000, TimeUnit.MILLISECONDS);
        shoulderServo.addPosition("storage", 1.0, 1000, TimeUnit.MILLISECONDS);
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

    public void up() {
        shoulderServo.setPosition("up");
    }

    public void down() {
        shoulderServo.setPosition("down");
    }

    public void storage() {
        shoulderServo.setPosition("storage");
    }

    public boolean isPositionReached() {
        return shoulderServo.isPositionReached();
    }
}
