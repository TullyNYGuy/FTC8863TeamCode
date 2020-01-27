package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class OdometryModule {

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
    private int countsPerRevolution;

    private double circumference;

    private DistanceUnit units;

    private String odometryModuleConfigName;

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

    public String getOdometryModuleConfigName() {
        return odometryModuleConfigName;
    }

    public void setName(String odometryModuleConfigName) {
        this.odometryModuleConfigName = odometryModuleConfigName;
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
    public OdometryModule(int countsPerRevolution, double circumference, DistanceUnit units, String odometryModuleConfigName, HardwareMap hardwareMap) {
        this.countsPerRevolution = countsPerRevolution;
        this.circumference = circumference;

        // let's pick a unit to use inside this class and do all storage and calculations in that unit
        // When someone wants a different unit we just convert to that unit as the last step
        this.units = units;
        this.odometryModuleConfigName = odometryModuleConfigName;
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

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    private double convertTicksToUnit(DistanceUnit units, int ticks) {
        ///// this should be the circumference
        return (double) ticks / 1440.0 * units.fromUnit(this.units, circumference);
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
        return units.fromUnit(this.units, odometryModule.getCurrentPosition());
    }


    public double getDistanceSinceLastChange(DistanceUnit units) {
        int currentPosition = odometryModule.getCurrentPosition();
        double distanceSinceLastChange = convertTicksToUnit(units, currentPosition - previousEncoderValue);
        previousEncoderValue = currentPosition;
        return distanceSinceLastChange;
    }


}
