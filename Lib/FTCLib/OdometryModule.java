package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

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

    private Units units;

    private String odometryModuleConfigName;

    private DcMotor odometryModule;

    private int previousEncoderValue = 0;

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

    public Units getUnits() {
        return units;
    }

    public void setUnits(Units units) {
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
    public OdometryModule(int countsPerRevolution, double circumference, Units units, String odometryModuleConfigName, HardwareMap hardwareMap) {
        this.countsPerRevolution = countsPerRevolution;
        if (units == Units.IN) {
            this.circumference = convertInToCm(circumference);
        }
        this.circumference = circumference;
        // let's pick a unit to use inside this class and do all storage and calculations in that unit
        // When someone wants a different unit we just convert to that unit as the last step
        this.units = units;
        this.odometryModuleConfigName = odometryModuleConfigName;
        //this.name = name;
        //odometryModule = hardwareMap.dcMotor.get(name);
        if(hardwareMap != null)
            odometryModule = hardwareMap.get(DcMotor.class, odometryModuleConfigName);
        if(odometryModule != null)
            odometryModule.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    private double convertTicksToInches(int ticks) {
        ///// this should be the circumference
        return ticks / 1440.0 * circumference;
    }

    private double convertTicksToCm(int ticks) {
        ///// this should be the circumference
        return ticks / 1440.0 * circumference * 2.54;
    }

    private double convertInToCm(double circumference) {
        return circumference * 2.54;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public int getEncoderValue() {
        return odometryModule.getCurrentPosition();
    }

    public void resetEncoderValue() {
        odometryModule.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    //public double getDistanceSinceReset() {
    //if (units == Units.IN) {
    //return convertTicksToInches(odometryModule.getCurrentPosition());
    //} else {
    // return convertTicksToCm(odometryModule.getCurrentPosition());
    // }

    //  }

    public double getDistanceSinceReset(Units units) {
        if (units == Units.IN) {
            return convertTicksToInches(odometryModule.getCurrentPosition());
        } else {
            return convertTicksToCm(odometryModule.getCurrentPosition());
        }
    }


    public double getDistanceSinceLastChange(Units units) {
        double distanceSinceLastChange = 0;
        int currentPosition = odometryModule.getCurrentPosition();
        if (units == Units.IN) {
            distanceSinceLastChange = convertTicksToInches(currentPosition - previousEncoderValue);
        } else {
            distanceSinceLastChange = convertTicksToCm(currentPosition - previousEncoderValue);
        }
        previousEncoderValue = currentPosition;
        return distanceSinceLastChange;
    }


}
