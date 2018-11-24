package org.firstinspires.ftc.teamcode.Lib.FTCLib;


public interface ProfileFunction {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    enum ProfileType{
        RAMP_UP_DOWN,
        RAMP_UP_DOWN_FLAT_TOP,
        RAMP_UP
    }

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    ProfileType getProfileType();

    boolean isFinished();

    double getYValue(double xValue);

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
