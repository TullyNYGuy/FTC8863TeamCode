package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

public class CRServo {

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

    private double centerValue = 0.5;
    private double deadBandRange = 0.1;
    private Servo crServo;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public double getCenterValue() {
        return centerValue;
    }

    public void setCenterValue(double centerValue) {
        this.centerValue = centerValue;
    }

    public double getDeadBandRange() {
        return deadBandRange;
    }

    public void setDeadBandRange(double deadBandRange) {
        this.deadBandRange = deadBandRange;
    }


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public CRServo(String servoName, HardwareMap hardwareMap, double centerValue, double deadBandRange) {
        crServo = hardwareMap.servo.get(servoName);
        this.centerValue = centerValue;
        this.deadBandRange = deadBandRange;
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

    public void updatePosition(double throttle) {
        double servoPosition;
        if (-deadBandRange < throttle && throttle < deadBandRange) {
            crServo.setPosition(centerValue);
        }

        else {
            servoPosition = 0.5 * throttle + centerValue;
            servoPosition = Range.clip(servoPosition, 0, 1);
            crServo.setPosition(servoPosition);
        }
    }

    public double getPosition() {
        return crServo.getPosition();
    }

    public void setDirection(Servo.Direction direction){
        crServo.setDirection(direction);
    }

}
