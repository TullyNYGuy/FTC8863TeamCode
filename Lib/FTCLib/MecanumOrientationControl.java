package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class MecanumOrientationControl {

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

    private PIDControl pidControl;
    private Orientation2D desiredOrientation;

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

    public MecanumOrientationControl(double kp, double ki, double kd, Orientation2D desiredOrientation) {
        this.desiredOrientation = desiredOrientation;
        this.pidControl = new PIDControl(kp, ki, kd, desiredOrientation.getAngle(AngleUnit.DEGREES));
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

    public double getRateOfRotation(Orientation2D currentOrientation) {
        return pidControl.getCorrection(currentOrientation.getAngle(AngleUnit.DEGREES));
    }

}
