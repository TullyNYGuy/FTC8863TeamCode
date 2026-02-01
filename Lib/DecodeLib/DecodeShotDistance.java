package org.firstinspires.ftc.teamcode.Lib.DecodeLib;


import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class DecodeShotDistance {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum ShotType {
        LONG,
        MEDIUM,
        SHORT;
    }
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
private static final double LONGSHOT_LIMIT = 72;
    private static DistanceUnit ourUnits = DistanceUnit.INCH;
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

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
public static ShotType getShotType(double rangeToTarget, DistanceUnit unit){
        if (ourUnits.fromUnit(unit, rangeToTarget) <= LONGSHOT_LIMIT) {
            return ShotType.SHORT;
        }
        else {
            return ShotType.LONG;
        }
}
}
