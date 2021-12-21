package org.firstinspires.ftc.teamcode.Lib.FTCLib;


public class MechanismUnits {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum MechanismUnit {
        MECHANISM_UNIT,
        ENCODER_COUNT
    }
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private double mechanismUnitPerEncoderCount = 0;

    private void setMechanismUnitPerEncoderCount(double movementForOneRevolution, double countsForOneRevolution) {
        mechanismUnitPerEncoderCount = movementForOneRevolution / countsForOneRevolution;
    }

    public double getMechanismUnitPerEncoderCount() {
        return mechanismUnitPerEncoderCount;
    }

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

    public MechanismUnits(double movementForOneRevolution, MotorType motorType) {
        setMechanismUnitPerEncoderCount(movementForOneRevolution, MotorType.getCountsPerRev(motorType));
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

    public int fromMechanism(double mechanismPosition) {
        return (int) (mechanismPosition / mechanismUnitPerEncoderCount);
    }

    public double fromCounts(int counts) {
        return counts * mechanismUnitPerEncoderCount;
    }

    public double toMechanism(int counts) {
        return fromCounts(counts);
    }

    public double toCounts(double mechanismPosition) {
        return fromMechanism(mechanismPosition);
    }

    public String toString(double mechanismPosition) {
        return "position = " + mechanismPosition;
    }

    public String toString(int counts) {
        return "counts = " + counts;
    }
}
