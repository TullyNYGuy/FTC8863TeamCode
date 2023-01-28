package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;

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
    private Rev2mDistanceSensor sensorTimeOfFlight;;
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
        sensorTimeOfFlight = (Rev2mDistanceSensor)sensorRange;
        this.distanceUnit = distanceUnit;
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
    /**
     * Move the arm to the init position, move the cone grabber to the init position.
     */
    public boolean init(Configuration config) {
        // start the init for the extension retraction mechanism
        logCommand("Init starting");
        // start the init of the extension retraction mechanism
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
        if (measuredDistance >= withinDistanceLowerLimit && measuredDistance <= withinDistanceUpperLimit ) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isWithinDistance(double lowerLimit, double upperLimit, DistanceUnit distanceUnit) {
        double measuredDistance = sensorRange.getDistance(distanceUnit);
        if (measuredDistance >= lowerLimit && measuredDistance <= upperLimit ) {
            return true;
        } else {
            return false;
        }
    }
}
