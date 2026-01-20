package org.firstinspires.ftc.teamcode.Lib.DecodeLib;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Color;

public class DecodeSorterSlot {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private double angle;
    private AngleUnit unit = AngleUnit.DEGREES;
    private DecodeArtifact artifact;

    public DecodeArtifact getArtifact() {
        return artifact;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        if (angle == 0 || angle == 120 || angle == 240) {
            this.angle = angle;
        } else {
            throw new ArithmeticException("Invalid Slot Angle");
        }
    }

    public void setArtifact(DecodeArtifact artifact) {
        this.artifact = artifact;
    }
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public DecodeSorterSlot(double sorterSlotAngle) {
        setAngle(sorterSlotAngle);
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

}
