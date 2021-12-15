package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

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
    private Servo8863 shoulderServo;
    private double shoulderUpPosition = .63;
    private double shoulderDownPosition = .1;
    private ElapsedTime timer;
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
        shoulderServo = new Servo8863("shoulderServo", hardwareMap, telemetry);
        shoulderServo.setPositionOne(shoulderUpPosition);
        shoulderServo.setPositionTwo(shoulderDownPosition);

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
    public void shoulderUp() {
        shoulderServo.goPositionOne();
    }

    public void shoulderDown() {
        shoulderServo.goPositionTwo();
    }

    public double add(double number1, double number2) {
        return number1 + number2;
    }

}
