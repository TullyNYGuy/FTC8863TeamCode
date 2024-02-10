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
public class CenterStageWristServo {

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
    private Servo8863New wristServo;

    private final String WRIST_SERVO_NAME = CenterStageRobot.HardwareName.WRIST_SERVO.hwName;

    private double intakePosition = 0.28;
    private double highDropPosition = 0.64;
    private double mediumDropPosition = 0.64;
    private double lowDropPosition = 0.64;
    private double setUpForDeliveryPosition = 0.76;

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

    public CenterStageWristServo(HardwareMap hardwareMap, Telemetry telemetry) {
        wristServo = new Servo8863New(WRIST_SERVO_NAME, hardwareMap, telemetry);

        wristServo.addPosition("intakePosition", intakePosition, 200, TimeUnit.MILLISECONDS);
        wristServo.addPosition("highDropPosition", highDropPosition, 1000, TimeUnit.MILLISECONDS);
        wristServo.addPosition("mediumDropPosition", mediumDropPosition, 1000, TimeUnit.MILLISECONDS);
        wristServo.addPosition("lowDropPosition", lowDropPosition, 1000, TimeUnit.MILLISECONDS);
        wristServo.addPosition("setUpForDeliveryPosition", setUpForDeliveryPosition, 250, TimeUnit.MILLISECONDS);

        wristServo.setDirection(Servo.Direction.FORWARD);
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

    public void intakePosition() {
        wristServo.setPosition("intakePosition");
    }

    public void highDropPosition() {
        wristServo.setPosition("highDropPosition");
    }

    public void mediumDropPosition() {
        wristServo.setPosition("mediumDropPosition");
    }

    public void lowDropPosition() {
        wristServo.setPosition("lowDropPosition");
    }

    public void setUpForDeliveryPosition() {
        wristServo.setPosition("setUpForDeliveryPosition");
    }

    public void bumpUpBig (){
        wristServo.bump(0.1);
    }

    public void bumpDownBig () {
        wristServo.bump(-0.1);
    }

    public void bumpUpSmall () {
        wristServo.bump(0.01);
    }

    public void bumpDownSmall () {
        wristServo.bump(-0.01);
    }

    public boolean isPositionReached() {
        return wristServo.isPositionReached();
    }

    // wrappers

    public double getCurrentPosition(){
        return wristServo.getCurrentPosition();
    }

    public ServoPosition getServoPosition(String positionName) {
        return wristServo.getServoPosition(positionName);
    }

    public void changeServoPosition(String positionName, double position) {
        wristServo.changePosition(positionName, position);
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        wristServo.testPositionsUsingJoystick(opmode);
    }

    public void setupServoPositionsUsingGamepad(LinearOpMode opmode) {
        wristServo.setupServoPositionsUsingGamepad(opmode);
    }
}
