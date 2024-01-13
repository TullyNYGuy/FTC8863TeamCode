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

    private String servoName;

    private double intakePosition = 0.0;
    private double normalReadyPosition = 0.0;
    private double lowReadyPosition = 0.0;

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
        armServo = new Servo8863New(servoName, hardwareMap, telemetry);
        this.servoName = servoName;


        armServo.addPosition("intakeposition", intakePosition, 1000, TimeUnit.MILLISECONDS);
        armServo.addPosition("normalreadyposition", normalReadyPosition, 1000, TimeUnit.MILLISECONDS);
        armServo.addPosition("lowreadyposition", lowReadyPosition, 1000, TimeUnit.MILLISECONDS);

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
        armServo.setPosition("intakeposition");
    }

    public void normalReadyPosition() {
        armServo.setPosition("normalreadyposition");
    }

    public void lowReadyPosition() {
        armServo.setPosition("lowreadyposition");
    }

    public boolean isPositionReached() {
        return armServo.isPositionReached();
    }

    // wrappers
    public ServoPosition getServoPosition(String positionName) {
        return armServo.getServoPosition(positionName);
    }

    public void changeServoPosition(String positionName, double position) {
        armServo.changePosition(positionName, position);
    }

    public void testPositionUsingJoystick(LinearOpMode opmode) {
        armServo.testPositionsUsingJoystick(opmode);
    }
}
