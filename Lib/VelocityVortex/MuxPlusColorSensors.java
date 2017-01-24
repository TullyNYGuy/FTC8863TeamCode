package org.firstinspires.ftc.teamcode.Lib.VelocityVortex;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitI2CMux;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

public class MuxPlusColorSensors {

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
    private AdafruitI2CMux mux;
    private AdafruitColorSensor8863 frontBeaconPusherRightColorSensor;
    private AdafruitColorSensor8863 rightSideBeaconPusherColorSensor;
    private AdafruitColorSensor8863 leftSideBeaconPusherColorSensor;
    
    private AdafruitI2CMux.PortNumber frontBeaconPusherRightColorSensorPort = 
            RobotConfigMappingForGenericTest.getFrontBeaconPusherRightColorSensorPort();
    private AdafruitI2CMux.PortNumber rightSideBeaconPusherColorSensorPort =
            RobotConfigMappingForGenericTest.getRightSideBeaconPusherColorSensorPort();
    private AdafruitI2CMux.PortNumber leftSideBeaconPusherColorSensorPort =
            RobotConfigMappingForGenericTest.getLeftSideBeaconPusherColorSensorPort();

    // The next variable will point to last color sensor object that is created. All communication
    // will occur though it.
    private AdafruitColorSensor8863 activeColorSensor;

    // The next variable is needed because I need to access the led through the actual color sensor
    // object, not the objeect I am using to communicate with
    private AdafruitColorSensor8863 actualColorSensor;

    private AdafruitI2CMux.PortNumber currentPort;

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
    //NEED TO TEST THE CLASS
    public MuxPlusColorSensors(HardwareMap hardwareMap, Telemetry telemetry) {
        // Create an I2C mux and initialize it
        mux = new AdafruitI2CMux(hardwareMap, RobotConfigMappingForGenericTest.getMuxName(), 
                RobotConfigMappingForGenericTest.getMuxAddress());
        // disconnect all 8 ports
        mux.disablePorts();

        mux.selectAndEnableAPort(frontBeaconPusherRightColorSensorPort);
        // create colorSensor1 and initialize it
        frontBeaconPusherRightColorSensor = new AdafruitColorSensor8863(hardwareMap, 
                RobotConfigMappingForGenericTest.getadafruitColorSensorName(), 
                RobotConfigMappingForGenericTest.getCoreDeviceInterfaceName(),
                RobotConfigMappingForGenericTest.getFrontBeaconPusherRightColorSensorLEDPort());
        activeColorSensor = frontBeaconPusherRightColorSensor;
        // if the color sensor is not operation properly report it
        if (!activeColorSensor.checkDeviceId() || !activeColorSensor.isDataValid()) {
            activeColorSensor.reportStatus("front right beacon color sensor", telemetry);
        }
        // shut off the led
        activeColorSensor.turnLEDOff();

        mux.selectAndEnableAPort(rightSideBeaconPusherColorSensorPort);
        // create colorSensor1 and initialize it
        rightSideBeaconPusherColorSensor = new AdafruitColorSensor8863(hardwareMap,
                RobotConfigMappingForGenericTest.getadafruitColorSensorName(),
                RobotConfigMappingForGenericTest.getCoreDeviceInterfaceName(),
                RobotConfigMappingForGenericTest.getRightBeaconPusherColorSensorLEDPort());
        activeColorSensor = rightSideBeaconPusherColorSensor;
        // if the color sensor is not operation properly report it
        if (!activeColorSensor.checkDeviceId() || !activeColorSensor.isDataValid()) {
            activeColorSensor.reportStatus("front right beacon color sensor", telemetry);
        }
        activeColorSensor.turnLEDOff();

        mux.selectAndEnableAPort(leftSideBeaconPusherColorSensorPort);
        // create colorSensor1 and initialize it
        leftSideBeaconPusherColorSensor = new AdafruitColorSensor8863(hardwareMap,
                RobotConfigMappingForGenericTest.getadafruitColorSensorName(),
                RobotConfigMappingForGenericTest.getCoreDeviceInterfaceName(),
                RobotConfigMappingForGenericTest.getLeftBeaconPusherColorSensorLEDPort());
        activeColorSensor = leftSideBeaconPusherColorSensor;
        // if the color sensor is not operation properly report it
        if (!activeColorSensor.checkDeviceId() || !activeColorSensor.isDataValid()) {
            activeColorSensor.reportStatus("front right beacon color sensor", telemetry);
        }
        activeColorSensor.turnLEDOff();
    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private void setPort(AdafruitI2CMux.PortNumber port) {
        if (currentPort != port) {
            mux.selectAndEnableAPort(port);
            currentPort = port;
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public boolean frontRightBeaconPusherColorSensorIsBlue() {
        setPort(frontBeaconPusherRightColorSensorPort);
        return activeColorSensor.isBlueUsingRGB();
    }

    public boolean frontRightBeaconPusherColorSensorIsRed() {
        setPort(frontBeaconPusherRightColorSensorPort);
        return activeColorSensor.isBlueUsingRGB();
    }

    public boolean rightSideBeaconPusherColorSensorIsBlue() {
        setPort(rightSideBeaconPusherColorSensorPort);
        return activeColorSensor.isBlueUsingRGB();
    }

    public boolean rightSideBeaconPusherColorSensorIsRed() {
        setPort(rightSideBeaconPusherColorSensorPort);
        return activeColorSensor.isBlueUsingRGB();
    }


    public boolean leftSideBeaconPusherColorSensorIsBlue() {
        setPort(leftSideBeaconPusherColorSensorPort);
        return activeColorSensor.isBlueUsingRGB();
    }

    public boolean leftSideBeaconPusherColorSensorIsRed() {
        setPort(leftSideBeaconPusherColorSensorPort);
        return activeColorSensor.isBlueUsingRGB();
    }

}
