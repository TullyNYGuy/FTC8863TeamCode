package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

/**
 * This class defines names for generic objects that we want to test. This way we don't have a million
 * different robot configs on the phone
 */
public class RobotConfigMappingForGenericTest {

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

    //2 motors
    private static String leftMotorName = "leftMotor"; //AH00qK3C port 1 (bottom)
    private static String rightMotorName = "rightMotor"; //AH00qK3C port 2 (bottom)

    //generic servo
    private static String genericServo = "genericServo"; //AL004A89 port 2

    //generic continuous rotation servo
    private static String crServo = "crServo"; //AL00VV9L port 2





    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public static String getleftMotorName() {return leftMotorName;}

    public static String getrightMotorName() {
        return rightMotorName;
    }

    public static String getgenericServo() {return genericServo;}

    public static String getcrServo() {return crServo;}


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
