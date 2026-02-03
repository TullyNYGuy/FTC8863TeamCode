package org.firstinspires.ftc.teamcode.Lib.DecodeLib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OnOffCycler;

/**
 * This class drives the Gobilda RGB indicator. The device takes a servo command and lights up in a color
 * that is related to the servo command.
 */
public class DecodeRGBIndicator implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum IndicatorColor {
        WHITE,
        BLACK, // off
        GREEN,
        VIOLET,
        YELLOW,
        ORANGE,
        RED,
        BLUE;

    }

    private IndicatorColor indicatorColor = IndicatorColor.GREEN;
    private IndicatorColor previousIndicatorColor = indicatorColor;
    private IndicatorColor indicatorColorWhenBlinkBackOn = indicatorColor;

    public enum Mode {
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

    /**
     * Set a color for the light.
     * @param color
     */
    public void setColor(IndicatorColor color) {
        double servoCommand = 0;
        // only send the command to the servo if there is a change in color so that bus traffic is reduced
        if (color != previousIndicatorColor) {
            switch (color) {
                case RED:
                    previousIndicatorColor = IndicatorColor.RED;
                    servoCommand = .280;
                    break;
                case BLUE:
                    previousIndicatorColor = IndicatorColor.BLUE;
                    servoCommand = .611;
                    break;
                case BLACK:
                    previousIndicatorColor = IndicatorColor.BLACK;
                    servoCommand = .0;
                    break;
                case GREEN:
                    previousIndicatorColor = IndicatorColor.GREEN;
                    servoCommand = .444;
                    break;
                case WHITE:
                    previousIndicatorColor = IndicatorColor.WHITE;
                    servoCommand = 1.0;
                    break;
                case ORANGE:
                    previousIndicatorColor = IndicatorColor.ORANGE;
                    servoCommand = .333;
                    break;
                case VIOLET:
                    previousIndicatorColor = IndicatorColor.VIOLET;
                    servoCommand = .722;
                    break;
                case YELLOW:
                    previousIndicatorColor = IndicatorColor.YELLOW;
                    servoCommand = .388;
                    break;
            }
            indicatorLight.setPosition(servoCommand);
        }
    }

    /**
     * Set the light to a continuous on or to blinking at a certain frequency
     * @param mode
     */
    public void setMode(Mode mode) {
        this.mode = mode;
        if (mode == Mode.BLINKING) {
            onOffCycler.start();
        }

    }

    /**
     * Set the frequency to blink the light at.
     * @param frequencyInHZ
     */
    public void setFrequency(double frequencyInHZ) {
        onOffCycler.setFrequency(frequencyInHZ);
    }

    //*********************************************************************************************
    //          METHODS needed to implement FTCRobotSubsystem
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * The update has to be called in order to get the blinking to work.
     */
    @Override
    public void update() {
        if (mode == Mode.BLINKING) {
            if (onOffCycler.getState() == OnOffCycler.State.OFF) {
                indicatorColorWhenBlinkBackOn = indicatorColor;
                setColor(IndicatorColor.BLACK);
            } else {
                setColor(indicatorColorWhenBlinkBackOn);
            }
        } else {
            setColor(indicatorColor);
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
