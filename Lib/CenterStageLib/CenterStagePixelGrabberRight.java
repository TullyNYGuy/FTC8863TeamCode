package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class CenterStagePixelGrabberRight implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public CenterStagePixelGrabber.State getState() {
        return pixelGrabber.getState();
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private final String PIXEL_GRABBER_NAME = CenterStageRobot.HardwareName.RIGHT_PIXEL_GRABBER.hwName;

    private CenterStagePixelGrabber pixelGrabber;

    private CenterStageFingerServo fingerServo;
    // finger servo positions
    public static double INIT_POSITION = 0.55;
    public static double OPEN_POSITION = 0.55;
    public static double CLOSE_POSITION = 0.15;
    private final String RIGHT_FINGER_SERVO_NAME = CenterStageRobot.HardwareName.RIGHT_FINGER_SERVO.hwName;

    private CenterStageIntakeColorSensor colorSensor;
    private final String INTAKE_RIGHT_COLOR_SENSOR_NAME = CenterStageRobot.HardwareName.RIGHT_INTAKE_COLOR_SENSOR.hwName;

    private DataLogging logFile;
    private boolean enableLogging = false;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public CenterStagePixelGrabberRight(HardwareMap hardwareMap, Telemetry telemetry) {
        fingerServo = new CenterStageFingerServo(
                hardwareMap,
                telemetry,
                RIGHT_FINGER_SERVO_NAME,
                INIT_POSITION,
                OPEN_POSITION,
                CLOSE_POSITION,
                Servo.Direction.REVERSE);

        colorSensor = new CenterStageIntakeColorSensor(
                hardwareMap,
                telemetry,
                INTAKE_RIGHT_COLOR_SENSOR_NAME);

        pixelGrabber = new CenterStagePixelGrabber(
                hardwareMap,
                telemetry,
                fingerServo,
                colorSensor,
                PIXEL_GRABBER_NAME);
    }

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

    //*******************************************************
    // commands
    //*******************************************************

    @Override
    public boolean init(Configuration config) {
        fingerServo.init();
        pixelGrabber.init(config);
        update();
        return true;
    }

    public void on() {
        pixelGrabber.on();
    }

    public void off() {
        pixelGrabber.off();
    }

    public void grabPixel() {
        pixelGrabber.grabPixel();
    }

    public void deliverPixel() {
        pixelGrabber.deliverPixel();
    }

    //*******************************************************
    // status
    //*******************************************************

    @Override
    public boolean isInitComplete() {
        return pixelGrabber.isInitComplete();
    }

    public boolean isCommandComplete() {
        return pixelGrabber.isCommandComplete();
    }

    public boolean isPixelPresent() {
        return pixelGrabber.isPixelPresent();
    }

    public boolean isPixelGrabbed() {
        return pixelGrabber.isPixelGrabbed();
    }

    public boolean didPixelGrabFail() {
        return pixelGrabber.didPixelGrabFail();
    }

    public boolean isDeliveryComplete() {
        return pixelGrabber.isDeliveryComplete();
    }

    public String getStateAsString(){
        return pixelGrabber.getStateAsString();
    }

    public String getCommandAsString(){
        return pixelGrabber.getCommandAsString();
    }

    @Override
    public String getName() {
        return PIXEL_GRABBER_NAME;
    }

    //*******************************************************
    // state machine
    //*******************************************************

    @Override
    public void update() {
        pixelGrabber.update();
    }

    @Override
    public void shutdown() {
        fingerServo.open();
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        pixelGrabber.setDataLog(logFile);
    }

    @Override
    public void enableDataLogging() {
        pixelGrabber.enableDataLogging();
    }

    @Override
    public void disableDataLogging() {
        pixelGrabber.disableDataLogging();
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }
}
