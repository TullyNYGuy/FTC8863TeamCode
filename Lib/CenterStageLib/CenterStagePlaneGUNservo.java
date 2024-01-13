package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ServoPosition;

import java.util.concurrent.TimeUnit;

@Config
public class CenterStagePlaneGUNservo {

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
    private Servo8863New gunServo;

    private String servoName = "gunServo";

    private double killPosition = .75;
    private double nonKillPosition = 0;

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

    public CenterStagePlaneGUNservo(HardwareMap hardwareMap, Telemetry telemetry) {
        gunServo = new Servo8863New(servoName, hardwareMap, telemetry);

        gunServo.addPosition("killPosition", killPosition, 1000, TimeUnit.MILLISECONDS);
        gunServo.addPosition("nonKillPosition", nonKillPosition, 1000, TimeUnit.MILLISECONDS);

        gunServo.setDirection(Servo.Direction.REVERSE);
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

    public void killPosition() {
        gunServo.setPosition("killPosition");
    }

    public void nonKillPosition() {
        gunServo.setPosition("nonKillPosition");
    }

    public boolean isPositionReached() {
        return gunServo.isPositionReached();
    }

    // wrappers
    public ServoPosition getServoPosition(String positionName) {
        return gunServo.getServoPosition(positionName);
    }

    public void changeServoPosition(String positionName, double position) {
        gunServo.changePosition(positionName, position);
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        gunServo.testPositionsUsingJoystick(opmode);
    }
}
