package org.firstinspires.ftc.teamcode.Lib.DecodeLib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
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

    private IndicatorColor currentIndicatorColor = IndicatorColor.BLACK;
    private IndicatorColor blinkingOnColor = currentIndicatorColor;
    private final IndicatorColor blinkingOffColor = IndicatorColor.BLACK;

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
    private DataLogOnChange logCommentOnChange;

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
        logCommentOnChange = new DataLogOnChange(logFile);
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
        // Default the light to off
        setColor(IndicatorColor.BLACK);
        // default the light to solid color
        setMode(Mode.SOLID);
        // default the frequency to 1
        setFrequency(1);
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    private void logComment(String comment) {
        if (loggingOn && logFile != null) {
            logCommentOnChange.log(getName() + " " + comment);
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * Set a color for the light.
     * @param newColor
     */
    public void setColor(IndicatorColor newColor) {
        double servoCommand = 0;
        // only send the command to the servo if there is a change in color so that bus traffic is reduced
        if (newColor != currentIndicatorColor) {
            switch (newColor) {
                case RED:
                    servoCommand = .285;
                    break;
                case BLUE:
                    servoCommand = .611;
                    break;
                case BLACK:
                    servoCommand = .0;
                    break;
                case GREEN:
                    servoCommand = .444;
                    break;
                case WHITE:
                    servoCommand = 1.0;
                    break;
                case ORANGE:
                    servoCommand = .333;
                    break;
                case VIOLET:
                    servoCommand = .722;
                    break;
                case YELLOW:
                    servoCommand = .388;
                    break;
            }
            logComment("RGB command = " + servoCommand);
            // save the color for when it is used to blink the indicator
            if (newColor != IndicatorColor.BLACK) {
                blinkingOnColor = newColor;
            }
            currentIndicatorColor = newColor;
            indicatorLight.setPosition(servoCommand);
        }
    }

    /**
     * Set the light to a continuous on or to blinking at a certain frequency
     * @param mode
     */
    public void setMode(Mode mode) {
        this.mode = mode;
        if (this.mode == Mode.BLINKING) {
            onOffCycler.start();
            logComment("Mode = blinking");
        } else {
            // mode is solid, set the solid color back to the blinking on color
            // this is because the current color at the time this setMode is called might be black
            setColor(blinkingOnColor);
            logComment("Mode = solid");
        }

    }

    public void off() {
        setColor(IndicatorColor.BLACK);
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
                // send black (off) to indicator
                setColor(blinkingOffColor);
                logComment("blinking = off state");
            } else {
                setColor(blinkingOnColor);
                logComment("blinking = on state");
            }
        } else {
            // solid mode
            setColor(currentIndicatorColor);
            logComment("mode = solid");
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
