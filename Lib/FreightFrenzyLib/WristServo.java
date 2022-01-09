package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;

import java.nio.charset.CharacterCodingException;
import java.util.concurrent.TimeUnit;

public class WristServo {

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
    Servo8863New wristServo;

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

    public WristServo (HardwareMap hardwareMap, Telemetry telemetry) {
        wristServo = new Servo8863New("wristServo",hardwareMap, telemetry);
        wristServo.addPosition("Storage", .0, 2000, TimeUnit.MILLISECONDS);
        wristServo.addPosition("Pickup", .92, 2000, TimeUnit.MILLISECONDS);
        wristServo.addPosition("Down", .05, 2000, TimeUnit.MILLISECONDS);
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

    public void wristDown() {
        wristServo.setPosition("Down");
    }

    public void wristUp() {
        wristServo.setPosition("Up");
    }

    public void wristMid() {
        wristServo.setPosition("Mid");
    }

    public boolean isPositionReached() {
        return wristServo.isPositionReached();
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        wristServo.testPositionsUsingJoystick(opmode);
    }
}
