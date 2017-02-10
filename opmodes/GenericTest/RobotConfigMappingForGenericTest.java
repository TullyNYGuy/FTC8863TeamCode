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

    //2 drive motors
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

    // sweeper
    private static String sweeperMotor = "sweeperMotor";

    // IMU
    // Core DIM module I2C port 0, configure as Adafruit IMU
    private static String IMUName = "IMU";

    //-------------------------------------------------
    // Color Sensors and mux
    //-------------------------------------------------

    // mux
    // Core DIM I2C port 1, configure as I2C Device
    private static String muxName = "mux";
    private static byte muxAddress = 0x70;

    // mux ports for the color sensors
    private static AdafruitI2CMux.PortNumber frontBeaconPusherRightColorSensorPort = AdafruitI2CMux.PortNumber.PORT0;
    private static AdafruitI2CMux.PortNumber frontBeaconPusherLeftColorSensorPort = AdafruitI2CMux.PortNumber.PORT1;
    private static AdafruitI2CMux.PortNumber rightSideBeaconPusherColorSensorPort = AdafruitI2CMux.PortNumber.PORT2;
    private static AdafruitI2CMux.PortNumber leftSideBeaconPusherColorSensorPort = AdafruitI2CMux.PortNumber.PORT3;

    // color sensors
    // Even though there are multiple color sensors we only need 1 name since the mux makes it look
    // like there is only 1 color sensor attached to the core DIM

    // Core DIM I2C Port 2, configure as I2C Device
    private static String adafruitColorSensorName = "colorSensor";

    //-----------------------------------------------
    //front beacon pushers
    //-----------------------------------------------

    //Limit Switches

    // Core DIM module Digital port 0, configure as Digital Device
    private static String rightFrontLimitSwitchName = "rightFrontSwitch";
    // Core DIM module Digital port 1, configure as Digital Device
    private static String rightBackLimitSwitchName = "rightBackSwitch";
    // Core DIM module Digital port 2, configure as Digital Device
    private static String leftFrontLimitSwitchName = "leftFrontSwitch";
    // Core DIM module Digital port 3, configure as Digital Device
    private static String leftBackLimitSwitchName = "leftBackSwitch";

    // left and right servos for front beacon pusher

    // Core servo controller port 5, configure as Servo
    private static String frontLeftBeaconServoName = "frontLeftBeaconPusherServo";
    // Core servo controller port 6, configure as Servo
    private static String frontRightBeaconServoName = "frontRightBeaconPusherServo";

    // LED ports for the color sensors

    // Core DIM module Digital port 4, configure as digital device
    private static int frontBeaconPusherRightColorSensorLEDPort = 4;
    private static String frontBeaconPusherRightColorSensorLEDName = "frontRightBeaconLED";
    // Core DIM module Digital port 5, configure as digital device
    private static int frontBeaconPusherLeftColorSensorLEDPort = 5;
    private static String frontBeaconPusherLeftColorSensorLEDName = "frontLeftBeaconLED";

    //----------------------------------------------------
    // Side Beacon pushers
    //----------------------------------------------------

    // Servos

    // Core servo controller port 4, configure as Servo
    private static String rightSideBeaconPusherServoName = "rightSideBeaconPusherServo";

    // Color sensors

    private static String rightSideBeaconColorSensorName = "rightBeaconColorSensor";
    private static String leftSideBeaconColorSensorName = "leftBeaconColorSensor";

    // LED ports for the color sensors

    // Core DIM module Digital port 5
    private static int rightSideBeaconPusherColorSensorLEDPort = 6;
    // Core DIM module Digital port 6
    private static int leftSideBeaconPusherColorSensorLEDPort = 7;

    //---------------------------------------------
    // shooter
    //---------------------------------------------

    private static String shooterLeadscrewMotorName = "shooterLeadscrewMotor";
    private static String shooterMotorName = "shooterMotor";
    private static String ballGateServoName = "ballGateServo";
    private static String shooterLimitSwitchName = "shooterLimitSwitch";

    //---------------------------------------------
    // Alliance Switches
    //---------------------------------------------

    // Core DIM module Digital port 6, configure as Digital Device
    private static String blueAllianceSwitchName = "blueAllianceSwitch";
    // Core DIM module Digital port 7, configure as Digital Device
    private static String redAllianceSwitchName = "redAllianceSwitch";


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

    public static String getSweeperMotor() {
        return sweeperMotor;
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

    public static String getLeftSideBeaconColorSensorName() {
        return leftSideBeaconColorSensorName;}

    public static String getRightSideBeaconColorSensorName() {
        return rightSideBeaconColorSensorName;
    }

    public static String getLeftFrontLimitSwitchName() {
        return leftFrontLimitSwitchName;
    }

    public static String getLeftBackLimitSwitchName() {
        return leftBackLimitSwitchName;
    }

    public static String getRightFrontLimitSwitchName() {
        return rightFrontLimitSwitchName;
    }

    public static String getRightBackLimitSwitchName() {
        return rightBackLimitSwitchName;
    }

    public static String getRightSideBeaconPusherServo() {
        return rightSideBeaconPusherServoName;
    }

    
    public static String getFrontLeftBeaconServoName() {
        return frontLeftBeaconServoName;
    }

    public static String getFrontRightBeaconServoName() {
        return frontRightBeaconServoName;
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

    public static int getFrontBeaconPusherLeftColorSensorLEDPort() {
        return frontBeaconPusherLeftColorSensorLEDPort;
    }

    public static int getRightSideBeaconPusherColorSensorLEDPort() {
        return rightSideBeaconPusherColorSensorLEDPort;
    }

    public static int getLeftSideBeaconPusherColorSensorLEDPort() {
        return leftSideBeaconPusherColorSensorLEDPort;
    }

    public static AdafruitI2CMux.PortNumber getFrontBeaconPusherRightColorSensorPort() {
        return frontBeaconPusherRightColorSensorPort;
    }

    public static AdafruitI2CMux.PortNumber getFrontBeaconPusherLeftColorSensorPort() {
        return frontBeaconPusherLeftColorSensorPort;
    }

    public static AdafruitI2CMux.PortNumber getRightSideBeaconPusherColorSensorPort() {
        return rightSideBeaconPusherColorSensorPort;
    }

    public static AdafruitI2CMux.PortNumber getLeftSideBeaconPusherColorSensorPort() {
        return leftSideBeaconPusherColorSensorPort;
    }

    public static String getShooterLeadscrewMotorName() {
        return shooterLeadscrewMotorName;
    }

    public static String getShooterMotorName() {
        return shooterMotorName;
    }

    public static String getBallGateServoName() {
        return ballGateServoName;
    }

    public static String getShooterLimitSwitchName() {
        return shooterLimitSwitchName;
    }

    public static String getBlueAllianceSwitchName() {
        return blueAllianceSwitchName;
    }

    public static String getRedAllianceSwitchName() {
        return redAllianceSwitchName;
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
