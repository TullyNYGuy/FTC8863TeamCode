package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExponentialMovingAverage;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.StatTrackerGB;

import java.util.concurrent.TimeUnit;

public class PowerPlay2mDistanceSensor implements FTCRobotSubsystem {

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

    private DistanceSensor sensorRange;
    private Rev2mDistanceSensor sensorTimeOfFlight;
    ;
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
    private double runningSum = 0;
    private int numberOfReadingsInAverage = 0;
    private int numberOfReadingsTaken = 0;
    private boolean isAverageReady = false;
    private double averageDistance = 0;
    private StatTrackerGB statTracker;
    private double singleReadingDistance = 0;

    private double greaterThanDistanceLimit = 0;

    public double getGreaterThanDistanceLimit() {
        return greaterThanDistanceLimit;
    }

    public void setGreaterThanDistanceLimit(double greaterThanDistanceLimit) {
        this.greaterThanDistanceLimit = greaterThanDistanceLimit;
    }

    private double lessThanDistanceLimit = 0;

    public double getLessThanDistanceLimit() {
        return lessThanDistanceLimit;
    }

    public void setLessThanDistanceLimit(double lessThanDistanceLimit) {
        this.lessThanDistanceLimit = lessThanDistanceLimit;
    }

    public double withinDistanceLowerLimit = 0;

    public double getWithinDistanceLowerLimit() {
        return withinDistanceLowerLimit;
    }

    public void setWithinDistanceLowerLimit(double withinDistanceLowerLimit) {
        this.withinDistanceLowerLimit = withinDistanceLowerLimit;
    }

    public double withinDistanceUpperLimit = 0;

    public double getWithinDistanceUpperLimit() {
        return withinDistanceUpperLimit;
    }

    public void setWithinDistanceUpperLimit(double withinDistanceUpperLimit) {
        this.withinDistanceUpperLimit = withinDistanceUpperLimit;
    }

    private double timeBetweenReadings = 50; // milliseconds

    /**
     * The 2m distance sensors seem to produce occasional glitchs in readings when they are at max
     * range. This controls whether those glitches are removed and the previous reading reported in
     * its place.
     */
    private boolean removeLargeTransitions = true;

    /**
     * Remove any large transitions between readings. This is most likely a glitch.
     */
    public void enableRemoveLargeTransitions() {
        removeLargeTransitions = true;
    }

    /**
     * Do not remove any large transitions.
     */
    public void disableRemoveLargeTransitions() {
        removeLargeTransitions = false;
    }

    /**
     * Any transition greater than this is defined as a large transition.
     */
    private double largeTransitionLimit = distanceUnit.fromUnit(DistanceUnit.MM, 7000);

    /**
     * If the large transition occurs in a time less than this, remove it. If not, then leave it.
     */
    private double timeLimitForRemoveLargeTransitions = 200; // mSec

    /**
     * If there is no last reading, it is null.
     */
    private Double lastReading = null;

    private ExponentialMovingAverage movingAverage;

    public void enableMovingAverage(double weightOfNewValue) {
        movingAverage = new ExponentialMovingAverage(weightOfNewValue);
    }

    public void disableMovingAverage() {
        movingAverage = null;
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

    public PowerPlay2mDistanceSensor(HardwareMap hardwareMap, Telemetry telemetry, String sensorName, DistanceUnit distanceUnit) {
        this.sensorName = sensorName;
        sensorRange = hardwareMap.get(DistanceSensor.class, sensorName);
        sensorTimeOfFlight = (Rev2mDistanceSensor) sensorRange;
        this.distanceUnit = distanceUnit;
        averageTimer = new ElapsedTime();
        singleReadingTimer = new ElapsedTime();
        statTracker = new StatTrackerGB();
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

    /**
     * Immediately read and return a distance from the sensor.
     * @param unit
     * @return
     */
    public double getDistance(DistanceUnit unit) {
        return sensorRange.getDistance(unit);
    }

    /**
     * Setup and start a single reading of the distance. The distance sensor takes 33 mSec to get a
     * reading. Reads of I2C devices can be cached. This method starts a timer that ensures the given
     * time passes before a new reading can be taken. This ensure fresh data. Distance sensors can
     * also read reflections of light emitted by another sensor and result in erroneous reading.
     * Enforcing a time between readings can help enforce an interleaving of readings between different
     * sensors and lessen the chance of interference between sensors.
     * @param timeBetweenReadings
     */
    public void startSingleReading(double timeBetweenReadings) {
        this.timeBetweenReadings = timeBetweenReadings;
        // if there has been at least this much time since the last reading, then it is possible
        // that the large transition is real. So leave the reading intact. Do this by setting the
        // last reading to null.
        if (singleReadingTimer.milliseconds() > timeLimitForRemoveLargeTransitions) {
            lastReading = null;
        }
        singleReadingTimer.reset();
        // take the reading but do not return it until after the timer has expired
        singleReadingDistance = getDistance(this.distanceUnit);
        if (removeLargeTransitions) {
            if (lastReading == null) {
                lastReading = singleReadingDistance;
            } else {
                // is this a large transition?
                if (Math.abs(lastReading - singleReadingDistance) > largeTransitionLimit) {
                    // yes so throw this reading away and use the last one instead
                    singleReadingDistance = lastReading;
                } else {
                    lastReading = singleReadingDistance;
                }
            }
        }
        if (movingAverage != null) {
            singleReadingDistance = movingAverage.average(singleReadingDistance);
        }
    }

    /**
     * Check to see if the timer has expired. For use in polling the timer to determine when a
     * reading is ready.
     * @return
     */
    public boolean isSingleReadingReady() {
        boolean ready = false;
        if (singleReadingTimer.milliseconds() > timeBetweenReadings) {
            ready = true;
        }
        return ready;
    }

    /**
     * Actually return a distance. For use after isSingleReadingReady returns true.
     * @return
     */
    public double getSingleReading(DistanceUnit unit) {
        return unit.fromUnit(this.distanceUnit, singleReadingDistance);
    }

    public void startAverage(int numberOfReadingsInAverage) {
        this.numberOfReadingsInAverage = numberOfReadingsInAverage;
        isAverageReady = false;
        numberOfReadingsTaken = 1;
        statTracker.clear();
        statTracker.addDataPoint(sensorRange.getDistance(DistanceUnit.INCH));
        averageTimer.reset();
    }

    public boolean isAverageReady() {
        if (averageTimer.milliseconds() > 50) {
            if (numberOfReadingsTaken < numberOfReadingsInAverage) {
                statTracker.addDataPoint(sensorRange.getDistance(DistanceUnit.INCH));
                numberOfReadingsTaken++;
                averageTimer.reset();
                if (numberOfReadingsTaken == numberOfReadingsInAverage) {
                    isAverageReady = true;
                    averageDistance = statTracker.getAverage();
                }
            } else {
                // an average is already calculated
            }

        }
        return isAverageReady;
    }

    public double getAverageDistance(DistanceUnit distanceUnit) {
        if (isAverageReady) {
            // an average is ready. Prep for the next one.
            isAverageReady = false;
            return distanceUnit.fromUnit(this.distanceUnit, averageDistance);
        } else {
            return 0;
        }
    }

    public boolean isGreaterThanDistance() {
        if (sensorRange.getDistance(distanceUnit) >= greaterThanDistanceLimit) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isGreaterThanDistance(double limit, DistanceUnit distanceUnit) {
        if (sensorRange.getDistance(distanceUnit) >= limit) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLessThanDistance() {
        if (sensorRange.getDistance(distanceUnit) >= lessThanDistanceLimit) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLessThanDistance(double limit, DistanceUnit distanceUnit) {
        if (sensorRange.getDistance(distanceUnit) <= limit) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isWithinDistance() {
        double measuredDistance = sensorRange.getDistance(distanceUnit);
        if (measuredDistance >= withinDistanceLowerLimit && measuredDistance <= withinDistanceUpperLimit) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isWithinDistance(double lowerLimit, double upperLimit, DistanceUnit distanceUnit) {
        double measuredDistance = sensorRange.getDistance(distanceUnit);
        if (measuredDistance >= lowerLimit && measuredDistance <= upperLimit) {
            return true;
        } else {
            return false;
        }
    }

    public void dumpDataToCSV(String prefix) {
        statTracker.dumpDataCSV(prefix);
    }
}
