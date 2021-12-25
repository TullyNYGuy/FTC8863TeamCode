package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

import java.nio.charset.CharacterCodingException;

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
    Servo8863 wristServo;
    double wristUpPosition = .60;
    double wristMidPosition = .18;
    double wristDownPosition = .05;
    ElapsedTime timer;
    double timeToCompleteMovement = 0;

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
        wristServo = new Servo8863("wristServo",hardwareMap, telemetry);
        wristServo.setPositionOne(wristUpPosition);
        wristServo.setPositionTwo(wristDownPosition);
        wristServo.setPositionThree(wristMidPosition);

        timer = new ElapsedTime();
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
        wristServo.goPositionTwo();
        timer.reset();
        timeToCompleteMovement = 1000; //milliseconds
    }

    public void wristUp() {
        wristServo.goPositionOne();
        timer.reset();
        timeToCompleteMovement = 1000; //milliseconds
    }

    public void wristMid() {
        wristServo.goPositionThree();
        timer.reset();
        timeToCompleteMovement = 1000; //milliseconds
    }

    public boolean isMovementComplete() {
        boolean answer = false;
        if (timer.milliseconds() > timeToCompleteMovement) {
            answer = true;
        }
        return answer;
    }
}
