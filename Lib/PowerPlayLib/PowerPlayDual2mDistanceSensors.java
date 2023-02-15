package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.StatTrackerGB;
import org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest.TestDual2mDistanceSensorContinuousOrAverage;

public class PowerPlayDual2mDistanceSensors implements FTCRobotSubsystem {

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
    private enum Mode {
        START_AVERAGE,
        START_CONTINUOUS,
        AVERAGE,
        CONTINUOUS,
        COMPLETE
    }

    private Mode mode = Mode.CONTINUOUS;

    private enum SensorBeingRead {
        INVERSE,
        NORMAL
    }

    private SensorBeingRead sensorBeingRead = SensorBeingRead.INVERSE;

    private String sensorName = "";

    private DistanceUnit distanceUnit = DistanceUnit.INCH;

    public DistanceUnit getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(DistanceUnit distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    private ElapsedTime averageTimer;
    private ElapsedTime singleReadingTimer;
    private int numberOfReadingsInAverage = 0;
    private int numberOfReadingsTaken = 0;
    private boolean isAverageReady = false;
    private double averageDistance = 0;
    private double singleReadingDistance = 0;

    private PowerPlay2mDistanceSensor distanceSensorInverse;
    private PowerPlay2mDistanceSensor distanceSensorNormal;
    private StatTrackerGB statTrackerInverse;
    private StatTrackerGB statTrackerNormal;

    private double centeredOnPoleLimit = 25;

    public double getCenteredOnPoleLimit() {
        return centeredOnPoleLimit;
    }

    public void setCenteredOnPoleLimit(double centeredOnPoleLimit, DistanceUnit unit) {
        this.centeredOnPoleLimit = this.distanceUnit.fromUnit(unit, centeredOnPoleLimit);
    }

    private double timeBetweenReadings = 50; // milliseconds

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

    public PowerPlayDual2mDistanceSensors(HardwareMap hardwareMap, Telemetry telemetry, String sensorName, DistanceUnit distanceUnit) {
        this.sensorName = sensorName;
        this.distanceUnit = distanceUnit;
        averageTimer = new ElapsedTime();
        singleReadingTimer = new ElapsedTime();
        statTrackerInverse = new StatTrackerGB();
        statTrackerNormal = new StatTrackerGB();
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    @Override
    public String getName() {
        return sensorName;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
    }

    @Override
    public void enableDataLogging() {
        enableLogging = true;
    }

    @Override
    public void disableDataLogging() {
        enableLogging = false;
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    /**
     * Placeholder
     */
    private void logState() {
        if (enableLogging && logFile != null) {
            logStateOnChange.log(getName() + " state = ");
        }
    }

    private void logCommand(String command) {
        if (enableLogging && logFile != null) {
            logCommandOnchange.log(getName() + " command = " + command);
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************


    @Override
    public boolean init(Configuration config) {
        logCommand("Init starting");
        logCommand("Init");
        logCommand("Init complete");
        return true;
    }

    public boolean isInitComplete() {
        return true;
    }

    @Override
    public void update() {
    }
}
