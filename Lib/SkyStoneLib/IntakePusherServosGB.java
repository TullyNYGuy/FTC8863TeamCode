package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class IntakePusherServosGB {

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
    private Servo8863 leftServo;
    private Servo8863 rightServo;

    final public double DEFAULT_LEFT_POSITION_IN = 0.34;
    final public double DEFAULT_LEFT_POSITION_OUT = 0.05;
    final public double DEFAULT_RIGHT_POSITION_IN = 0.60;
    final public double DEFAULT_RIGHT_POSITION_OUT = 0.95;

    private double initPositionLeft = DEFAULT_LEFT_POSITION_OUT;
    private double homePositionLeft = initPositionLeft;

    private double initPositionRight = DEFAULT_RIGHT_POSITION_OUT;
    private double homePositionRight = initPositionRight;

    private double upPositionLeft = DEFAULT_LEFT_POSITION_IN;
    private double upPositionRight = DEFAULT_RIGHT_POSITION_IN;

    private double downPositionLeft = DEFAULT_LEFT_POSITION_OUT;
    private double downPositionRight = DEFAULT_RIGHT_POSITION_OUT;

    private boolean initState = false;
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
    public IntakePusherServosGB(String leftServoName, String rightServoName, HardwareMap hardwareMap, Telemetry telemetry) {
        leftServo = new Servo8863(leftServoName, hardwareMap, telemetry, homePositionLeft, upPositionLeft, downPositionLeft, initPositionLeft, Servo.Direction.FORWARD);
        rightServo = new Servo8863(rightServoName, hardwareMap, telemetry, homePositionRight, upPositionRight, downPositionRight, initPositionRight, Servo.Direction.FORWARD);
        initState = false;
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
    public void pushStoneIn() {
        leftServo.goUp();
        rightServo.goUp();
    }

    public void home() {
        leftServo.goHome();
        rightServo.goHome();
    }

    public void init() {
        leftServo.goInitPosition();
        rightServo.goInitPosition();
        initState = true;
    }

    public boolean isInitComplete() {
        return initState;
    }

    public void shutdown() {
        home();
    }

}
