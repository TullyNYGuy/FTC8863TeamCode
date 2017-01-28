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

    public enum BeaconSide {
        RIGHT,
        LEFT;
    }

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
    private AdafruitI2CMux.PortNumber frontBeaconPusherLeftColorSensorPort =
            RobotConfigMappingForGenericTest.getFrontBeaconPusherLeftColorSensorPort();
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

    private Telemetry telemetry;

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
            activeColorSensor.reportStatus("right side beacon color sensor", telemetry);
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
            activeColorSensor.reportStatus("left side beacon color sensor", telemetry);
        }
        activeColorSensor.turnLEDOff();

        this.telemetry = telemetry;
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
        return activeColorSensor.isRedUsingRGB();

    }

    public boolean rightSideBeaconPusherColorSensorIsBlue() {
        setPort(rightSideBeaconPusherColorSensorPort);
        return activeColorSensor.isBlueUsingRGB();
    }

    public boolean rightSideBeaconPusherColorSensorIsRed() {
        setPort(rightSideBeaconPusherColorSensorPort);
        return activeColorSensor.isRedUsingRGB();
    }


    public boolean leftSideBeaconPusherColorSensorIsBlue() {
        setPort(leftSideBeaconPusherColorSensorPort);
        return activeColorSensor.isBlueUsingRGB();
    }

    public boolean leftSideBeaconPusherColorSensorIsRed() {
        setPort(leftSideBeaconPusherColorSensorPort);
        return activeColorSensor.isRedUsingRGB();
    }

    public String frontBeaconPusherRightColorSensorRGBValuesScaledAsString() {
        setPort(frontBeaconPusherRightColorSensorPort);
        return activeColorSensor.rgbValuesScaledAsString();
    }

    public String frontBeaconPusherLeftColorSensorRGBValuesScaledAsString() {
        setPort(frontBeaconPusherLeftColorSensorPort);
        return activeColorSensor.rgbValuesScaledAsString();
    }

    public String rightSideBeaconPusherColorSensorRGBValuesScaledAsString() {
        setPort(rightSideBeaconPusherColorSensorPort);
        return activeColorSensor.rgbValuesScaledAsString();
    }

    public String leftSideBeaconPusherColorSensorRGBValuesScaledAsString() {
        setPort(leftSideBeaconPusherColorSensorPort);
        return activeColorSensor.rgbValuesScaledAsString();
    }


    public void displayAllColorResults() {
        telemetry.addData("Front right blue = ", frontRightBeaconPusherColorSensorIsBlue());
        telemetry.addData("Front right red = ", frontRightBeaconPusherColorSensorIsRed());
        telemetry.addData("Right side blue = ", rightSideBeaconPusherColorSensorIsBlue());
        telemetry.addData("Right side red = ", rightSideBeaconPusherColorSensorIsRed());
        telemetry.addData("Left side blue = ", leftSideBeaconPusherColorSensorIsBlue());
        telemetry.addData("Left side blue = ", leftSideBeaconPusherColorSensorIsBlue());
        telemetry.update();
    }

    /**
     * Set the blue or red led in the coreDIM module to match the color that the sensor sees
     * @param side LEFT or RIGHT color sensor
     */
    public void setCoreDimLedToMatchColorSensor(BeaconSide side) {
        if (side == BeaconSide.LEFT) {
            if (leftSideBeaconPusherColorSensorIsRed()) {
                activeColorSensor.turnCoreDIMRedLEDOn();
                activeColorSensor.turnCoreDIMBlueLEDOff();
            }
            if (leftSideBeaconPusherColorSensorIsBlue()) {
                activeColorSensor.turnCoreDIMRedLEDOff();
                activeColorSensor.turnCoreDIMBlueLEDOn();
            }
        } else {
            // want the right side
            if (rightSideBeaconPusherColorSensorIsRed()) {
                activeColorSensor.turnCoreDIMRedLEDOn();
                activeColorSensor.turnCoreDIMBlueLEDOff();
            }
            if (rightSideBeaconPusherColorSensorIsBlue()) {
                activeColorSensor.turnCoreDIMRedLEDOff();
                activeColorSensor.turnCoreDIMBlueLEDOn();
            }
        }
    }

}
