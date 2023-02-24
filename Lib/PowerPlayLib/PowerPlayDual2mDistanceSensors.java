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

import java.util.concurrent.TimeUnit;

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
    private enum State {
        IDLE,
        START_CONTINUOUS,
        CONTINUOUS_READ_INVERSE,
        CONTINUOUS_READ_NORMAL,
        START_AVERAGE,
        AVERAGE_READ_INVERSE,
        AVERAGE_READ_NORMAL,
        AVERAGE_COMPLETE
    }

    private State state = State.IDLE;

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

    private int numberOfReadingsInAverage = 10;

    public PowerPlay2mDistanceSensor distanceSensorInverse;
    public PowerPlay2mDistanceSensor distanceSensorNormal;

    private double continuousDistanceNormal = 0;

    public double getContinuousDistanceNormal(DistanceUnit unit) {
        return distanceUnit.fromUnit(this.distanceUnit, continuousDistanceNormal);
    }

    private double continuousDistanceInverse = 0;

    public double getContinuousDistanceInverse(DistanceUnit distanceUnit) {
        return distanceUnit.fromUnit(this.distanceUnit, continuousDistanceInverse);
    }

    private Double continuousDifference = null; // 0 is what we are hunting for do don't initialize to that

    public double getContinuousDifference(DistanceUnit distanceUnit) {
        return distanceUnit.fromUnit(this.distanceUnit, continuousDifference);
    }

    private boolean dataValid = false;

    public boolean isDataValid() {
        return dataValid;
    }

    private double averageDistanceNormal = 0;

    public double getAverageDistanceNormal(DistanceUnit distanceUnit) {
        if (isAverageReady()) {
            return distanceUnit.fromUnit(this.distanceUnit, averageDistanceNormal);
        } else {
            return 10000;
        }
    }

    private double averageDistanceInverse = 0;

    public double getAverageDistanceInverse(DistanceUnit distanceUnit) {
        if (isAverageReady()) {
            return distanceUnit.fromUnit(this.distanceUnit, averageDistanceInverse);
        } else {
            return 10000;
        }
    }

    private double averageDistance = 1000;

    public double getAverageDistance(DistanceUnit distanceUnit) {
        if (isAverageReady()) {
            return distanceUnit.fromUnit(this.distanceUnit, averageDistance);
        } else {
            return 10000;
        }
    }

    private double adjustedAverageDistance = 1000;

    public double getAdjustedAverageDistance(DistanceUnit distanceUnit) {
        if (isAverageReady()) {
            return distanceUnit.fromUnit(this.distanceUnit, adjustedAverageDistance);
        } else {
            return 10000;
        }
    }

    private double averageDifference = 1000;

    public double getAverageDifference(DistanceUnit distanceUnit) {
        if (isAverageReady()) {
            return distanceUnit.fromUnit(this.distanceUnit, averageDifference);
        } else {
            return 10000;
        }
    }

    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    private long timeBetweenReadings = 50; // milliseconds

    public double getTimeBetweenReadings(TimeUnit timeUnit) {
        return timeUnit.convert(timeBetweenReadings, this.timeUnit);
    }

    public void setTimeBetweenReadings(long timeBetweenReadings, TimeUnit timeUnit) {
        this.timeBetweenReadings = this.timeUnit.convert(timeBetweenReadings, timeUnit);
    }

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
        distanceSensorNormal = new PowerPlay2mDistanceSensor(hardwareMap, telemetry, PowerPlayRobot.HardwareName.DISTANCE_SENSOR_NORMAL.hwName, distanceUnit);
        distanceSensorNormal.enableMovingAverage(.5);
        distanceSensorNormal.enableRemoveLargeTransitions(7000, DistanceUnit.MM);
        distanceSensorInverse = new PowerPlay2mDistanceSensor(hardwareMap, telemetry, PowerPlayRobot.HardwareName.DISTANCE_SENSOR_INVERSE.hwName, distanceUnit);
        distanceSensorInverse.enableMovingAverage(.5);
        distanceSensorInverse.enableRemoveLargeTransitions(7000, DistanceUnit.MM);
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

    public void startContinuousMode() {
        state = State.START_CONTINUOUS;
        continuousDistanceInverse = 0;
        continuousDistanceNormal = 0;
    }

    public void startAverageMode(int numberOfReadingsInAverage) {
        state = State.START_AVERAGE;
        this.numberOfReadingsInAverage = numberOfReadingsInAverage;
        averageDistanceInverse = 0;
        averageDistanceNormal = 0;
    }

    public boolean isAverageReady() {
        boolean answer = false;
        if (state == State.AVERAGE_COMPLETE) {
            answer = true;
        }
        return answer;
    }

    public void stopReading() {
        state = State.IDLE;
    }

    public void dumpCSVDataNormal() {

    }

    @Override
    public void update() {
        switch (state) {
            case IDLE: {
                // do nothing
                dataValid = false;
            }
            break;

            case START_CONTINUOUS: {
                distanceSensorInverse.startSingleReading(timeBetweenReadings);
                state = State.CONTINUOUS_READ_INVERSE;
            }
            break;

            case CONTINUOUS_READ_INVERSE: {
                if (distanceSensorInverse.isSingleReadingReady()) {
                    continuousDistanceInverse = distanceSensorInverse.getSingleReading(this.distanceUnit);
                    distanceSensorNormal.startSingleReading(timeBetweenReadings);
                    state = State.CONTINUOUS_READ_NORMAL;
                }
            }
            break;

            case CONTINUOUS_READ_NORMAL: {
                if (distanceSensorNormal.isSingleReadingReady()) {
                    continuousDistanceNormal = distanceSensorNormal.getSingleReading(this.distanceUnit);
                    distanceSensorInverse.startSingleReading(timeBetweenReadings);
                    continuousDifference = continuousDistanceNormal - continuousDistanceInverse;
                    dataValid = true;
                    state = State.CONTINUOUS_READ_INVERSE;
                }
            }
            break;

            case START_AVERAGE: {
                distanceSensorInverse.startAverage(numberOfReadingsInAverage);
                state = State.AVERAGE_READ_INVERSE;
            }
            break;

            case AVERAGE_READ_INVERSE: {
                if (distanceSensorInverse.isAverageReady()) {
                    averageDistanceInverse = distanceSensorInverse.getAverageDistance(this.distanceUnit);
                    distanceSensorNormal.startAverage(numberOfReadingsInAverage);
                }
            }
            break;

            case AVERAGE_READ_NORMAL: {
                if (distanceSensorNormal.isAverageReady()) {
                    DistanceUnit distanceUnitForLineEquation = DistanceUnit.MM;
                    averageDistanceNormal = distanceSensorNormal.getAverageDistance(this.distanceUnit);
                    averageDifference = averageDistanceNormal - averageDistanceInverse;
                    averageDistance = (averageDistanceInverse + averageDistanceNormal) / 2;
                    // curve fit for adjusting sensor readings into an actual distance. This equation is in mm.
                    adjustedAverageDistance = 1.23 * distanceUnitForLineEquation.fromUnit(this.distanceUnit, averageDistance) - 70.3;
                    // convert the adjustedAverageDistance to units for this class
                    adjustedAverageDistance = this.distanceUnit.fromUnit(distanceUnitForLineEquation, adjustedAverageDistance);
                    state = State.AVERAGE_COMPLETE;
                }
            }
            break;

            case AVERAGE_COMPLETE: {
                // the average is complete, wait for someone to read it
            }
            break;
        }
    }
}

