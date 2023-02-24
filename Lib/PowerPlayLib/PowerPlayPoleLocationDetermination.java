package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Distance;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExponentialMovingAverage;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class PowerPlayPoleLocationDetermination implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum PoleLocation {
        OUT_OF_VIEW,
        LEFT,
        CENTER,
        RIGHT
    }

    private PoleLocation poleLocation = PoleLocation.OUT_OF_VIEW;

    public PoleLocation getPoleLocation() {
        return poleLocation;
    }

    public enum State {
        IDLE,
        WAITING_FOR_VALID_SENSOR_DATA,
        ACTIVE
    }

    private State state = State.IDLE;

    public State getState() {
        return state;
    }

    private PowerPlayDual2mDistanceSensors distanceSensors;
    private ExponentialMovingAverage distanceFromPoleFilter;

    private double sensorDifference = 0;

    public double getSensorDifference(DistanceUnit unit) {
        return unit.fromUnit(this.distanceUnit, sensorDifference);
    }

    private double normalDistance = 0;

    public double getNormalDistance(DistanceUnit unit) {
        return unit.fromUnit(this.distanceUnit, normalDistance);
    }

    private double inverseDistance = 0;

    public double getInverseDistance(DistanceUnit unit) {
        return unit.fromUnit(this.distanceUnit, inverseDistance);
    }

    private double distanceFromPole = 0;

    public double getDistanceFromPole(DistanceUnit unit) {
        return unit.fromUnit(this.distanceUnit, distanceFromPole);
    }

    private DistanceUnit distanceUnit = DistanceUnit.MM;

    /**
     * The limit for saying the sensors are centered on the pole. It is +/- so any difference in the
     * sensors between +100mm (4") and -100mm (4") says the sensors are centered. Note that the value
     * here is meaningless. The difference is just an arbitrary number and not a real distance.
     */
    private double centeredOnPoleLimit = 100; // mm

    public double getCenteredOnPoleLimit() {
        return centeredOnPoleLimit;
    }

    public void setCenteredOnPoleLimit(double centeredOnPoleLimit, DistanceUnit unit) {
        this.centeredOnPoleLimit = this.distanceUnit.fromUnit(unit, centeredOnPoleLimit);
    }

    /**
     * Anything beyond this distance is bogus and will be reported as OUT_OF_VIEW
     */
    private double OUT_VIEW_DISTANCE = 300; //mm (12")

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    private boolean initComplete = false;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public PowerPlayPoleLocationDetermination(PowerPlayDual2mDistanceSensors distanceSensors) {
        this.distanceSensors = distanceSensors;
        distanceFromPoleFilter = new ExponentialMovingAverage(.3);
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    @Override
    public String getName() {
        return "Pole Location Determinaton";
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

    private void logPoleLocation() {
        if (enableLogging && logFile != null) {
            logStateOnChange.log(getName() + " phase = " + poleLocation.toString());
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
        logCommand("Init starting");
        logCommand("Init");
        initComplete = true;
        return true;
    }

    public boolean isInitComplete() {
        if (initComplete) {
            logCommand("Init complete");
        }
        return initComplete;
    }


    public void enablePoleLocationDetermination() {
        state = State.WAITING_FOR_VALID_SENSOR_DATA;
        distanceSensors.startContinuousMode();
        distanceFromPole = 0;
        distanceFromPoleFilter.clear();
        logCommand("pole location enabled");
    }

    public void disablePoleLocationDetermination() {
        state = State.IDLE;
        distanceSensors.stopReading();
        distanceFromPole = 0;
        distanceFromPoleFilter.clear();
        logCommand("pole location disabled");
    }

    /**
     * The distance from the pole to the robot is not accurately given by the distance sensors. But
     * we did take some data to see if there was a linear relationship between the actual distance
     * and the distance given by the sensors. There was. We used a linear regression to find a
     * formula for the actual distance when we have the distance sensors readings.
     * @return 0 means that the distance is not valid
     */
    private void calculateDistanceFromPole() {
        // the distance sensor readings have to be valid and the robot has to be centered on the pole
        if (state == State.ACTIVE && poleLocation == PoleLocation.CENTER) {
            // this is from a linear regression. The units are MM
            distanceFromPole = (inverseDistance + normalDistance)/2 * 1.23 -70.3;
            distanceFromPole = distanceFromPoleFilter.average(distanceFromPole);
        } else {
            // the distance sensor data is not valid, or the pole is not centered on the robot
            distanceFromPole = 0;
            distanceFromPoleFilter.clear();
        }
    }

    public boolean isDataValid() {
        if (state == State.ACTIVE) {
            return true;
        } else {
            return false;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////state machine//////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void update() {
        distanceSensors.update();
        switch (state) {
            case IDLE: {
                // do nothing
            }
            case WAITING_FOR_VALID_SENSOR_DATA: {
               if (distanceSensors.isDataValid()) {
                   sensorDifference = distanceSensors.getContinuousDifference(DistanceUnit.MM);
                   normalDistance = distanceSensors.getContinuousDistanceNormal(DistanceUnit.MM);
                   inverseDistance = distanceSensors.getContinuousDistanceInverse(DistanceUnit.MM);
                    state = State.ACTIVE;
                }
            }
            break;

            case ACTIVE: {
                sensorDifference = distanceSensors.getContinuousDifference(DistanceUnit.MM);
                normalDistance = distanceSensors.getContinuousDistanceNormal(DistanceUnit.MM);
                inverseDistance = distanceSensors.getContinuousDistanceInverse(DistanceUnit.MM);

                poleLocation = PoleLocation.OUT_OF_VIEW;
                if (normalDistance > OUT_VIEW_DISTANCE && inverseDistance > OUT_VIEW_DISTANCE) {
                    poleLocation = PoleLocation.OUT_OF_VIEW;
                } else {
                    if (Math.abs(sensorDifference) <= centeredOnPoleLimit) {
                        poleLocation = PoleLocation.CENTER;
                    } else {
                        if (sensorDifference > centeredOnPoleLimit) {
                            poleLocation = PoleLocation.LEFT;
                        } else {
                            if (sensorDifference < centeredOnPoleLimit) {
                                poleLocation = PoleLocation.RIGHT;
                            }
                        }
                    }
                }
                calculateDistanceFromPole();
                logPoleLocation();
            }
            break;
        }

    }
}
