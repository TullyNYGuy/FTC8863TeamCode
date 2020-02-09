package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class GripperRotator implements FTCRobotSubsystem {

    private final static String SUBSYSTEM_NAME = "GripperRotator";

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
    private Servo8863 servoRotator;
    private double initPos = 0.87;
    private double outwardPos = 0.09;
    private double inwardPos = 0.98;
    private double homePos = 0.00;
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
    public GripperRotator(HardwareMap hardwareMap, String servoName, Telemetry telemetry) {
        servoRotator = new Servo8863(servoName, hardwareMap, telemetry, homePos, outwardPos, inwardPos, initPos, Servo.Direction.FORWARD);

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
    public void rotateOutward() {
        servoRotator.goUp();

    }

    public void rotateInward() {
        servoRotator.goDown();

    }

    @Override
    public String getName() {
        return SUBSYSTEM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public boolean init(Configuration config) {
        rotateInward();
        return true;
    }

    @Override
    public void update() {

    }

    @Override
    public void shutdown() {
        rotateInward();
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

}
