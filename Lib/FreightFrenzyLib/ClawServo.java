package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class ClawServo {

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

    Servo8863 clawServo;
    double openPosition = 0;
    double closePosition = .58;
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

    public ClawServo(HardwareMap hardwareMap, Telemetry telemetry) {
        clawServo = new Servo8863("ClawServo",hardwareMap, telemetry);
        clawServo.setPositionOne(openPosition);
        clawServo.setPositionTwo(closePosition);

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

    public void openClaw() {
        clawServo.goPositionOne();
        timer.reset();
        timeToCompleteMovement = 500; //milliseconds
    }

    public void closeClaw() {
        clawServo.goPositionTwo();
        timer.reset();
        timeToCompleteMovement = 500; //milliseconds
    }

    public boolean isMovementComplete() {
        boolean answer = false;
        if (timer.milliseconds() > timeToCompleteMovement) {
            answer = true;
        }
        return answer;
    }
}
