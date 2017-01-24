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

    //generic IMU
    private static String IMUName = "IMU";

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

    // Color Sensors and mux
    private static String muxName = "mux";
    private static byte muxAddress = 0x70;
    // Even though there are multiple color sensors we only need 1 name since the mux makes it look
    // like there is only 1 color sensor attached to the core DIM
    private static String adafruitColorSensorName = "colorSensor";
    private static int frontBeaconPusherRightColorSensorLEDPort = 0;
    private static int rightBeaconPusherColorSensorLEDPort = 1;
    private static int leftBeaconPusherColorSensorLEDPort = 2;
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
