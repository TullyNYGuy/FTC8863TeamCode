package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class Shooter implements FTCRobotSubsystem {

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
    private DualMotorGearBox dualMotorGearBox;
    private DataLogging logFile;
    private boolean loggingOn = false;
    private AngleChanger angleChanger;
    private FiringSolution firingSolution;
    private final double SHOOTER_LENGTH = 9.25 * 0.0254;//Units are meters
    private final double SHOOTER_HEIGHT_PARALLEL = 5.75 * 0.0254;//Units are meters
    private final double VELOCITY = 10;

    //internal units
    private AngleUnit angleUnit = AngleUnit.RADIANS;
    private DistanceUnit distanceUnit = DistanceUnit.METER;
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
    public Shooter(String leftMotorName, String rightMotorName, HardwareMap hardwareMap, Telemetry telemetry) {
        dualMotorGearBox = new DualMotorGearBox(leftMotorName, rightMotorName, hardwareMap, telemetry);
        angleChanger =  AngleChanger.createAngleChanger(hardwareMap, telemetry);
        firingSolution = new FiringSolution();
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
    public boolean requestFire(double distanceToGoal, DistanceUnit unit, UltimateGoalGoal goal) {

       double distanceToGoalMeters = distanceUnit.fromUnit(unit, distanceToGoal);
        boolean result = false;
        double angle = 0;
        // angle is in radians
        angle = firingSolution.calculateShooterAngle(AngleUnit.RADIANS, distanceToGoalMeters, goal.getHeight(DistanceUnit.METER), VELOCITY, SHOOTER_HEIGHT_PARALLEL, SHOOTER_LENGTH);
        if (angle > 0) {
            result = true;
            angleChanger.setCurrentAngle(AngleUnit.RADIANS, angle);
        }
        return result;
    }

    public void setAngle (AngleUnit units, double angle) {
        angleChanger.setCurrentAngle(units, angle);
    }

    public boolean isAngleAdjustmentComplete() {
        return angleChanger.isAngleAdjustComplete();
    }

    public double calculateAngle(AngleUnit desiredAngleUnits, double distanceToGoal, DistanceUnit distanceUnit, UltimateGoalGoal goal) {
        double distanceToGoalMeters = distanceUnit.toMeters(distanceToGoal);
        return desiredAngleUnits.fromRadians(firingSolution.calculateShooterAngle(AngleUnit.RADIANS, distanceToGoalMeters, goal.getHeight(DistanceUnit.METER), VELOCITY, SHOOTER_HEIGHT_PARALLEL, SHOOTER_LENGTH));
    }

    public void setSpeed(int motorRPM) {
        dualMotorGearBox.setSpeed(motorRPM);
    }

    public double getSpeed() {
        return dualMotorGearBox.getSpeed();
    }

    public boolean isReady () {
        return true;
    }

    public void stop() {
        dualMotorGearBox.stopGearbox();
    }

    @Override
    public String getName() {
        return "Shooter";
    }

    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public boolean init(Configuration config) {
        dualMotorGearBox.init(config);
        angleChanger.init(config);
        return true;
    }

    @Override
    public void update() {
        dualMotorGearBox.update();
        angleChanger.update();
    }

    @Override
    public void shutdown() {
        dualMotorGearBox.stopGearbox();
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
    }
}
