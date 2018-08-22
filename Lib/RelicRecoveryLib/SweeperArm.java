package org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class SweeperArm {

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
    private Servo8863 sweeperServo;
    private Telemetry telemetry;
    private double homePosition = 1;
    private double initPosition = 1;
    private double positionOne = .70;

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
    public SweeperArm(HardwareMap hardwareMap, Telemetry telemetry) {
        sweeperServo = new Servo8863("sweeperArmServo", hardwareMap, telemetry);
        this.telemetry = telemetry;
        sweeperServo.setDirection(Servo.Direction.REVERSE);
        sweeperServo.setHomePosition(homePosition);
        sweeperServo.setInitPosition(initPosition);
        sweeperServo.setPositionOne(positionOne);
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
        sweeperServo.goInitPosition();
        telemetry.addData("Sweeper Servo initialized", "!");
    }

    public void goHome() {
        sweeperServo.goHome();
    }

    public void goDown() { sweeperServo.goPositionOne();}

    public void update() {
        // put any update commands here
    }

    public void shutdown() {
        goHome();
    }
}
