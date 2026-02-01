package org.firstinspires.ftc.teamcode.Lib.DecodeLib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OnOffCycler;

public class DecodeRGBIndicator implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum IndicaterColor {
        WHITE,
        BLACK,
        GREEN,
        VIOLET,
        YELLOW,
        ORANGE,
        RED,
        BLUE;

    }

    private IndicaterColor indicaterColor = IndicaterColor.GREEN;

    private enum Mode {
        SOLID,
        BLINKING;
    }

    private Mode mode = Mode.SOLID;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private ServoImplEx indicatorLight;
    private OnOffCycler onOffCycler;
    //private

    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods For Implementing FTCRobotSubsystem
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    /**
     * Property that holds a log file
     */
    private DataLogging logFile;

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    /**
     * Property that holds whether data is being logged into the log file.
     */
    private boolean loggingOn = false;

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    private String subsystemName = DecodeRobot.HardwareName.INDICATOR_LIGHT.hwName;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public DecodeRGBIndicator(HardwareMap hardwareMap, Telemetry telemetry) {
        indicatorLight = (ServoImplEx) hardwareMap.get(Servo.class, subsystemName);
        onOffCycler = new OnOffCycler(1);
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
    public void setColor(IndicaterColor color) {
        switch (color) {
            case RED:
                indicatorLight.setPosition(.277);
                break;
            case BLUE:
                indicatorLight.setPosition(.611);
                break;
            case BLACK:
                indicatorLight.setPosition(.0);
                break;
            case GREEN:
                indicatorLight.setPosition(.444);
                break;
            case WHITE:
                indicatorLight.setPosition(1.0);
                break;
            case ORANGE:
                indicatorLight.setPosition(.333);
                break;
            case VIOLET:
                indicatorLight.setPosition(.722);
                break;
            case YELLOW:
                indicatorLight.setPosition(.388);
                break;
        }
    }
public void setMode(Mode mode){
    this.mode = mode;
    if (mode == Mode.BLINKING){
        onOffCycler.start();
    }

}
public void setFrequency(double frequency){
        onOffCycler.setFrequency(frequency);
}
    //*********************************************************************************************
    //          METHODS needed to implement FTCRobotSubsystem
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    @Override
    public void update() {
        if (mode == Mode.BLINKING){
            //if ()
        }
    }

    @Override
    public String getName() {
        return subsystemName;
    }

    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
    }

    @Override
    public boolean init(Configuration config) {
        return true;
    }
}
