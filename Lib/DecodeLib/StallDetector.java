package org.firstinspires.ftc.teamcode.Lib.DecodeLib;


import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

public class StallDetector {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    /**
     * Velocity threshold in rpm
     */
    private double velocityThreshold = 0;

    public double getVelocityThreshold() {
        return velocityThreshold;
    }

    public void setVelocityThreshold(double velocityThreshold) {
        this.velocityThreshold = velocityThreshold;
    }

    /**
     * Current limit in amps
     */
    private double currentLimit = 0;

    public double getCurrentLimit(CurrentUnit currentUnit) {
        if (currentUnit == CurrentUnit.MILLIAMPS){
            return ourCurrentUnit.toMilliAmps(currentLimit);
        }
        else {
            return currentLimit;
        }
    }

    public void setCurrentLimit(double currentLimit, CurrentUnit currentUnit) {
        this.currentLimit = currentUnit.toAmps(currentLimit);
    }

    private CurrentUnit ourCurrentUnit = CurrentUnit.AMPS;

    private DecodeSorterMotor motor8863;

    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
//*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public StallDetector (DecodeSorterMotor motor8863, double velocityThreshold, double currentLimit,
                          CurrentUnit currentUnit){
        this.motor8863 = motor8863;
        this.velocityThreshold = velocityThreshold;
        this.currentLimit = currentLimit;
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

    public boolean isStalled(){
        double actualCurrent = motor8863.getCurrent(CurrentUnit.AMPS);

        double actualVelocity = motor8863.getCurrentRPM();
        if (actualCurrent > currentLimit && actualVelocity < velocityThreshold && enabled){
            return true;

        }
        else {
            return false;
        }
    }

}
