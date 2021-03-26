package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.HashMap;
import java.util.Map;

public class OdometryModule {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    /**
     * Defines configuration names
     */
    static final private String PROP_MOTOR = ".motor";
    static final private String PROP_COUNTS_PER_REVOLUTION = ".counts_per_revolution";
    static final private String PROP_CIRCUMFERENCE = ".circimference";
    static final private String PROP_CIRCUMFERENCE_UNITS = ".circimference_units";

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private int countsPerRevolution;

    private double circumference;

    private DistanceUnit units;

    private DcMotor odometryModule;

    private int previousEncoderValue = 0;

    private int startingEncoderValue = 0;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public int getCountsPerRevolution() {
        return countsPerRevolution;
    }

    public void setCountsPerRevolution(int countsPerRevolution) {
        this.countsPerRevolution = countsPerRevolution;
    }

    public double getCircumference() {
        return circumference;
    }

    public void setCircumference(double circumference) {
        this.circumference = circumference;
    }

    public DistanceUnit getUnits() {
        return units;
    }

    public void setUnits(DistanceUnit units) {
        this.units = units;
    }

    public double getPreviousEncoderValue() {
        return previousEncoderValue;
    }

    public void setPreviousEncoderValue(int previousEncoderValue) {
        this.previousEncoderValue = previousEncoderValue;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public OdometryModule(int countsPerRevolution, double circumference, DistanceUnit circumferenceUnits, String odometryModuleConfigName, HardwareMap hardwareMap) {
        this.countsPerRevolution = countsPerRevolution;
        this.circumference = circumference;

        // let's pick a unit to use inside this class and do all storage and calculations in that unit
        // When someone wants a different unit we just convert to that unit as the last step
        this.units = circumferenceUnits;
        //this.name = name;
        //odometryModule = hardwareMap.dcMotor.get(name);
        if(hardwareMap != null)
            odometryModule = hardwareMap.get(DcMotor.class, odometryModuleConfigName);
        if (odometryModule != null) {
            // BUG
            // We can't stop and reset the motor. The odometry module can't affect the motor.
            // Some other code controls that.
            //odometryModule.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            // instead
            startingEncoderValue = odometryModule.getCurrentPosition();
        }
    }

    public OdometryModule(int countsPerRevolution, double circumference, DistanceUnit circumferenceUnits, DcMotor motor) {
        this.countsPerRevolution = countsPerRevolution;
        this.circumference = circumference;
        this.units = circumferenceUnits;
        odometryModule = motor;
        if (odometryModule != null) {
            startingEncoderValue = odometryModule.getCurrentPosition();
        }
    }

    static protected Map<String, OdometryModule> odometryModulesMap = new HashMap<>();

    static public OdometryModule createOdometryModuleFromFile(Configuration config, String section, HardwareMap hardwareMap) {
        if (config == null)
            return null;
        if(odometryModulesMap.containsKey(section))
            return odometryModulesMap.get(section);
        String motorName = config.getPropertyString(section + PROP_MOTOR);
        Integer countsPerRevolution = config.getPropertyInteger(section + PROP_COUNTS_PER_REVOLUTION);
        Double circumference = config.getPropertyDouble(section + PROP_CIRCUMFERENCE);
        DistanceUnit circumferenceUnits = config.getPropertyDistanceUnit(section + PROP_CIRCUMFERENCE_UNITS);
        if(motorName == null || countsPerRevolution == null || circumference == null || circumferenceUnits == null)
            return null;
        DcMotor8863 motor = DcMotor8863.createMotorFromFile(config, motorName, hardwareMap);
        return new OdometryModule(countsPerRevolution, circumference,  circumferenceUnits, motor.getMotorInstance());

    }

    static public boolean saveOdometryModuleConfiguration(Configuration config, String section, String motorName, int countsPerRevolution, double circumference, DistanceUnit circumferenceUnits) {
        if (config == null)
            return false;
        config.setProperty(section + PROP_MOTOR, motorName);
        config.setProperty(section + PROP_COUNTS_PER_REVOLUTION, String.valueOf(countsPerRevolution));
        config.setProperty(section + PROP_CIRCUMFERENCE, String.valueOf(circumference));
        config.setProperty(section + PROP_CIRCUMFERENCE_UNITS, circumferenceUnits);
        return true;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    private double convertTicksToUnit(DistanceUnit units, int ticks) {
        ///// this should be the circumference
        return units.fromUnit(this.units, (double) ticks / 1440.0 * circumference);
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public int getEncoderValue() {
        return odometryModule.getCurrentPosition() - startingEncoderValue;
    }

    public void resetEncoderValue() {
        // BUG
        // We can't stop and reset the motor. The odometry module can't affect the motor.
        // Some other code controls that.
        //odometryModule.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // instead
        startingEncoderValue = odometryModule.getCurrentPosition();
    }

    //public double getDistanceSinceReset() {
    //if (units == Units.IN) {
    //return convertTicksToInches(odometryModule.getCurrentPosition());
    //} else {
    // return convertTicksToCm(odometryModule.getCurrentPosition());
    // }

    //  }

    public double getDistanceSinceReset(DistanceUnit units) {
        return convertTicksToUnit(units, getEncoderValue());
    }


    public double getDistanceSinceLastChange(DistanceUnit units) {
        int currentPosition = odometryModule.getCurrentPosition();
        double distanceSinceLastChange = convertTicksToUnit(units, currentPosition - previousEncoderValue);
        previousEncoderValue = currentPosition;
        return distanceSinceLastChange;
    }


}
