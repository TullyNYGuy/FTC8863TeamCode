package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ServoPosition;

import java.util.concurrent.TimeUnit;

@Config
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
    private Servo8863New clawServo;

    private final String ARM_SERVO_NAME = "clawServo";

    private double openPosition = 0.71;
    private double clampPosition = 0.41;

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
        clawServo = new Servo8863New(ARM_SERVO_NAME, hardwareMap, telemetry);

        clawServo.addPosition("openPosition", openPosition, 1000, TimeUnit.MILLISECONDS);
        clawServo.addPosition("clampPosition", clampPosition, 1000, TimeUnit.MILLISECONDS);

        clawServo.setDirection(Servo.Direction.FORWARD);
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

    public void open() {
        clawServo.setPosition("openPosition");
    }

    public void clamp() {
        clawServo.setPosition("clampPosition");
    }

    public void bumpUpBig (){
        clawServo.bump(0.1);
    }

    public void bumpDownBig () {
        clawServo.bump(-0.1);
    }

    public void bumpUpSmall () {
        clawServo.bump(0.01);
    }

    public void bumpDownSmall () {
        clawServo.bump(-0.01);
    }

    // wrappers

    public double getCurrentPosition(){
        return clawServo.getCurrentPosition();
    }

    public ServoPosition getServoPosition(String positionName) {
        return clawServo.getServoPosition(positionName);
    }

    public void changeServoPosition(String positionName, double position) {
        clawServo.changePosition(positionName, position);
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        clawServo.testPositionsUsingJoystick(opmode);
    }

    public void setupServoPositionsUsingGamepad(LinearOpMode opmode) {
        clawServo.setupServoPositionsUsingGamepad(opmode);
    }
}
