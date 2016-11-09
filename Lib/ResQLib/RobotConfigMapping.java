package org.firstinspires.ftc.teamcode.Lib.ResQLib;

public class RobotConfigMapping {

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

    // motor congtrollers:
    // AH00qK3C - bottom of robot
    // AL00XPUH - top of robot

    // servo controllers:
    // AL00VV9L - bottom of robot\
    // AL004A89 - top of robot

    //Drive Train
    private static String leftDriveMotorName = "leftDriveMotor"; //AH00qK3C port 1 (bottom)
    private static String rightDriveMotorName = "rightDriveMotor"; //AH00qK3C port 2 (bottom)

    //Sweeper
    private static String sweeperMotorName = "sweeperMotor"; //AL00XPUH port 2

    //Zip Line
    private static String leftZipLineServoName = "leftZipLineServo"; //AL00VV9L port 3
    private static String rightZipLineServoName = "rightZipLineServo"; //AL00VV9L port 4

    //Box Slider
    private static String linearSlideServoName = "slideServo"; //AL004A89 port 1
    private static String rampServoName = "rampServo"; //AL00VV9L port 1
    private static String leftBoxLimitSwitchName = "leftBoxLimitSwitch";
    private static String rightBoxLimitSwitchName = "rightBoxLimitSwitch";

    //Tape Measure
    private static String tapeMeasureMotorName = "tapeMeasureMotor"; //AL00XPUH port 1
    private static String tapeMeasureLimitSwitchName = "tapeMeasureLimitSwitch";
    private static String tapeMeasureAimingServoName = "tapeMeasureAimingServo"; //AL00VV9L port 5

    //Climber Dump
    private static String climberDumpServoName = "climberDumpServo"; //AL004A89 port 2

    //Bar Grabber
    private static String barGrabberServoName = "barGrabberServo"; //AL00VV9L port 2





    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public static String getLeftDriveMotorName() {return leftDriveMotorName;}

    public static String getRightDriveMotorName() {
        return rightDriveMotorName;
    }

    public static String getSweeperMotorName() {
        return sweeperMotorName;
    }

    public static String getLeftZipLineServoName() {
        return leftZipLineServoName;
    }

    public static String getRightZipLineServoName() {
        return rightZipLineServoName;
    }

    public static String getLinearSlideServoName() {
        return linearSlideServoName;
    }

    public static String getRampServoName() {return rampServoName;}

    public static String getLeftBoxLimitSwitchName() {return leftBoxLimitSwitchName; }

    public static String getRightBoxLimitSwitchName() {return rightBoxLimitSwitchName;}

    public static String getTapeMeasureMotorName() {return tapeMeasureMotorName;}

    public static String getTapeMeasureLimitSwitchName() {return tapeMeasureLimitSwitchName;}

    public static String getTapeMeasureAimingServoName() {return tapeMeasureAimingServoName;}

    public static String getClimberDumpServoName() {return climberDumpServoName;}

    public static String getBarGrabberServoName() {return barGrabberServoName;}


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
