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
public class CenterStageArmServo {

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
    private Servo8863New armServo;

    private final String ARM_SERVO_NAME = CenterStageRobot.HardwareName.ARM_SERVO.hwName;

    // change from .04 to .03 based on new servo installed 2/10/2024
    private double intakePosition = 0.03;
    private double highDropPosition = 0.7;
    private double mediumDropPosition = 0.7;
    private double lowDropPosition = 0.7;
    private double setUpDeliveryPosition = 0.75;

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

    public CenterStageArmServo(HardwareMap hardwareMap, Telemetry telemetry) {
        armServo = new Servo8863New(ARM_SERVO_NAME, hardwareMap, telemetry);
        
        armServo.addPosition("intakePosition", intakePosition, 700, TimeUnit.MILLISECONDS);
        armServo.addPosition("highDropPosition", highDropPosition, 1000, TimeUnit.MILLISECONDS);
        armServo.addPosition("mediumDropPosition", mediumDropPosition, 1000, TimeUnit.MILLISECONDS);
        armServo.addPosition("lowDropPosition", lowDropPosition, 1000, TimeUnit.MILLISECONDS);
        armServo.addPosition("setUpForDeliveryPosition", setUpDeliveryPosition, 800, TimeUnit.MILLISECONDS);

        armServo.setDirection(Servo.Direction.FORWARD);
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
        armServo.setPosition("intakePosition");
    }

    public void highDropPosition() {
        armServo.setPosition("highDropPosition");
    }

    public void mediumDropPosition() {
        armServo.setPosition("mediumDropPosition");
    }

    public void lowDropPosition() {
        armServo.setPosition("lowDropPosition");
    }

    public void setSetUpDeliveryPosition() {
        armServo.setPosition("setUpForDeliveryPosition");
    }

    public boolean isPositionReached() {
        return armServo.isPositionReached();
    }

    public void bumpUpBig (){
        armServo.bump(0.1);
    }

    public void bumpDownBig () {
        armServo.bump(-0.1);
    }

    public void bumpUpSmall () {
        armServo.bump(0.01);
    }

    public void bumpDownSmall () {
        armServo.bump(-0.01);
    }

    // wrappers

    public double getCurrentPosition(){
        return armServo.getCurrentPosition();
    }

    public ServoPosition getServoPosition(String positionName) {
        return armServo.getServoPosition(positionName);
    }

    public void changeServoPosition(String positionName, double position) {
        armServo.changePosition(positionName, position);
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        armServo.testPositionsUsingJoystick(opmode);
    }

    public void setupServoPositionsUsingGamepad(LinearOpMode opmode) {
        armServo.setupServoPositionsUsingGamepad(opmode);
    }
}
