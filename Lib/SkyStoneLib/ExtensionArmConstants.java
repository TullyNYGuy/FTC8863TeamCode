package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

public class ExtensionArmConstants {

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

    // 2.75" spool size
    // 2 stages
    // in theory 17.27
    public static double movementPerRevolution = 2.75 * Math.PI * 2;

    public static double resetPower = -0.1;

    // with the drag chain installed the max is limited due to the length of the drag chain.
    // It was experimentally determined to be 1900 but let's give a little safety margin
    public static Double maximumExtensionInEncoderCounts = 1800.0;

    public static String mechanismName = "extensionArm";

    public static DcMotor8863.MotorType motorType = DcMotor8863.MotorType.ANDYMARK_40;

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
