package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitI2CMux;

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

    // IMU
    // Core DIM module I2C port 0, configure as Adafruit IMU
    private static String IMUName = "IMU";

    // left and right servos for front beacon pusher

    // Core servo controller port 5, configure as Servo
    private static String frontLeftBeaconServo = "frontLeftBeaconServo";
    // Core servo controller port 6, configure as Servo
    private static String frontRightBeaconServo = "frontRightBeaconServo";

    //front beacon pushers

    // Core DIM module Digital port 0
    private static String rightFrontLimitSwitch = "rightFrontSwitch";
    // Core DIM module Digital port 1
    private static String rightBackLimitSwitch = "rightBackSwitch";
    // Core DIM module Digital port 2
    private static String leftFrontLimitSwitch = "leftFrontSwitch";
    // Core DIM module Digital port 3
    private static String leftBackLimitSwitch = "leftBackSwitch";

    // Color Sensors and mux

    // mux
    // Core DIM I2C port 0, configure as I2C Device
    private static String muxName = "mux";
    private static byte muxAddress = 0x70;

    // color sensors
    // Even though there are multiple color sensors we only need 1 name since the mux makes it look
    // like there is only 1 color sensor attached to the core DIM

    // Core DIM I2C Port 1, configure as I2C Device
    private static String adafruitColorSensorName = "colorSensor";
    // LED ports for the color sensors
    // Core DIM module Digital port 4
    private static int frontBeaconPusherRightColorSensorLEDPort = 4;
    // Core DIM module Digital port 5
    private static int rightBeaconPusherColorSensorLEDPort = 5;
    // Core DIM module Digital port 6
    private static int leftBeaconPusherColorSensorLEDPort = 6;
    // mux ports for the color sensors
    private static AdafruitI2CMux.PortNumber frontBeaconPusherRightColorSensorPort = AdafruitI2CMux.PortNumber.PORT0;
    private static AdafruitI2CMux.PortNumber rightSideBeaconPusherColorSensorPort = AdafruitI2CMux.PortNumber.PORT1;
    private static AdafruitI2CMux.PortNumber leftSideBeaconPusherColorSensorPort = AdafruitI2CMux.PortNumber.PORT2;

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
    
    public static String getFrontLeftBeaconServoName() {
        return frontLeftBeaconServo;
    }

    public static String getFrontRightBeaconServoName() {
        return frontRightBeaconServo;
    }

    public static String getMuxName() {
        return muxName;
    }

    public static byte getMuxAddress() {
        return muxAddress;
    }

    public static int getFrontBeaconPusherRightColorSensorLEDPort() {
        return frontBeaconPusherRightColorSensorLEDPort;
    }

    public static int getRightBeaconPusherColorSensorLEDPort() {
        return rightBeaconPusherColorSensorLEDPort;
    }

    public static int getLeftBeaconPusherColorSensorLEDPort() {
        return leftBeaconPusherColorSensorLEDPort;
    }

    public static AdafruitI2CMux.PortNumber getFrontBeaconPusherRightColorSensorPort() {
        return frontBeaconPusherRightColorSensorPort;
    }

    public static AdafruitI2CMux.PortNumber getRightSideBeaconPusherColorSensorPort() {
        return rightSideBeaconPusherColorSensorPort;
    }

    public static AdafruitI2CMux.PortNumber getLeftSideBeaconPusherColorSensorPort() {
        return leftSideBeaconPusherColorSensorPort;
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
