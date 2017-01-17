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
    private static String leftMotorName = "leftMotor";
    private static String rightMotorName = "rightMotor";

    // third motor
    private static String thirdMotorName = "thirdMotor";

    //generic servo
    private static String genericServoName = "genericServo";

    //generic continuous rotation servo
    private static String crServoName = "crServo";

    //generic core device interface module
    private static String coreDeviceInterfaceName = "coreDIM";

    //generic IMU
    private static String IMUName = "IMU";

    //generic Adafruit ColorSensor
    private static String adafruitColorSensorName = "colorSensor";

    // left and right servos for front beacon pusher
    private static String frontLeftBeaconServo = "frontLeftBeaconServo";
    private static String frontRightBeaconServo = "frontRightBeaconServo";

    //front beacon pushers
    // port 0
    private static String rightFrontLimitSwitch = "rightFrontSwitch";
    // port 2
    private static String leftFrontLimitSwitch = "leftFrontSwitch";
    // port 1
    private static String rightBackLimitSwitch = "rightBackSwitch";
    // port 3
    private static String leftBackLimitSwitch = "leftBackSwitch";

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public static String getleftMotorName() {
        return leftMotorName;
    }

    public static String getrightMotorName() {
        return rightMotorName;
    }

    public static String getthirdMotorName() {
        return thirdMotorName;
    }

    public static String getgenericServoName() {
        return genericServoName;
    }

    public static String getcrServoName() {
        return crServoName;
    }

    public static String getCoreDeviceInterfaceName() {
        return coreDeviceInterfaceName;
    }

    public static String getIMUName() {
        return IMUName;
    }

    public static String getadafruitColorSensorName() {
        return adafruitColorSensorName;
    }

    public static String getRightFrontLimitSwitchName() {
        return rightFrontLimitSwitch;
    }

    public static String getLeftFrontLimitSwitchName() {
        return leftFrontLimitSwitch;
    }

    public static String getRightBackLimitSwitchName() {
        return rightBackLimitSwitch;
    }

    public static String getLeftBackLimitSwitchName() {
        return leftBackLimitSwitch;
    }
    
        public static String getFrontLeftBeaconServo() {
        return frontLeftBeaconServo;
    }

    public static String getFrontRightBeaconServo() {
        return frontRightBeaconServo;
    }

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
