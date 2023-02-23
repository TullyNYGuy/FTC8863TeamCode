package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
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

    public PoleLocation getPoleLocationLeftOrRight() {
        return poleLocation;
    }

    private enum State {
        IDLE,
        ACTIVE
    }

    private State state = State.IDLE;

    private PowerPlayDual2mDistanceSensors distanceSensors;
    private double sensorDifference = 0;
    private double normalDistance = 0;
    private double inverseDistance = 0;

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

    private double OUT_VIEW_DISTANCE = 7000; //mm
    private double LEFT_OR_RIGHT_DIFFERENCE = 7500; //MM

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
        state = State.ACTIVE;
        distanceSensors.startContinuousMode();
        logCommand("pole location enabled");
    }

    public void disablePoleLocationDetermination() {
        state = State.IDLE;
        distanceSensors.stopReading();
        logCommand("pole location disabled");
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////state machine//////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void update() {
        switch (state) {
            case IDLE: {
                // do nothing
            }
            break;
            case ACTIVE: {
                distanceSensors.update();

                sensorDifference = distanceSensors.getContinuousDifference(DistanceUnit.MM);
                normalDistance = distanceSensors.getContinuousDistanceNormal(DistanceUnit.MM);
                inverseDistance = distanceSensors.getContinuousDistanceInverse(DistanceUnit.MM);

                poleLocation = PoleLocation.OUT_OF_VIEW;
                if (sensorDifference > centeredOnPoleLimit) {
                    poleLocation = PoleLocation.LEFT;
                }
                if (sensorDifference < centeredOnPoleLimit) {
                    poleLocation = PoleLocation.RIGHT;
                }
                if (Math.abs(sensorDifference) <= centeredOnPoleLimit) {
                    poleLocation = PoleLocation.CENTER;
                }
                logPoleLocation();
            }
            break;
        }

    }
}
