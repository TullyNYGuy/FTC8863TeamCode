package org.firstinspires.ftc.teamcode.Lib.ResQLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class TapeMeasureWinch {

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

    private Servo8863 tapeMeasureAimingServo;
    private DcMotor8863 tapeMeasureMotor;

    // home position
    private double aimingHomePosition = 0.8;
    
    // current position of the servo
    private double currentPosition = 0;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getMotorPosition
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************


    public TapeMeasureWinch(HardwareMap hardwareMap, Telemetry telemetry) {
        
        // setup the servo 
        tapeMeasureAimingServo = new Servo8863(RobotConfigMapping.getTapeMeasureAimingServoName(),hardwareMap, telemetry);
        tapeMeasureAimingServo.setHomePosition(aimingHomePosition);
        tapeMeasureAimingServo.setDirection(Servo.Direction.REVERSE);
        this.currentPosition = 0;
        
        // setup the tape measure motor
        tapeMeasureMotor = new DcMotor8863(RobotConfigMapping.getTapeMeasureMotorName(), hardwareMap);

        tapeMeasureMotor.setDirection(DcMotor.Direction.REVERSE);
        tapeMeasureMotor.setMaxMotorPower(1);
        tapeMeasureMotor.setMinMotorPower(-1);
        tapeMeasureMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        tapeMeasureMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        tapeMeasureMotor.setEncoderTolerance(3);
        tapeMeasureMotor.setMovementPerRev(3.07 * Math.PI);
        tapeMeasureMotor.setNextMotorState(DcMotor8863.NextMotorState.HOLD);
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

    // servo functions

    public void goHome() {
        tapeMeasureAimingServo.goHome();
        this.currentPosition = aimingHomePosition;
    }

    public void setPosition(double position) {
        tapeMeasureAimingServo.setPosition(position);
    }

    public double getPosition() {
        return tapeMeasureAimingServo.getPosition();
    }
    
    public void incrementServoPosition (double increment) {
        this.currentPosition = currentPosition + increment;
        tapeMeasureAimingServo.setPosition(this.currentPosition);
    }

    //motor functions

    public void setPower(double power) {
        tapeMeasureMotor.setPower(power);
    }
}
